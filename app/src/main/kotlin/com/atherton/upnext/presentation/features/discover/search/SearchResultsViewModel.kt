package com.atherton.upnext.presentation.features.discover.search

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModel
import com.atherton.upnext.domain.usecase.GetConfigUseCase
import com.atherton.upnext.domain.usecase.GetPopularMoviesTvUseCase
import com.atherton.upnext.domain.usecase.SearchMultiUseCase
import com.atherton.upnext.presentation.features.discover.withDiscoverSearchImageUrls
import com.atherton.upnext.util.base.BaseViewEffect
import com.atherton.upnext.util.base.UpNextViewModel
import com.atherton.upnext.util.extensions.preventMultipleClicks
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.zipWith
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchResultsViewModel @Inject constructor(
    initialState: SearchResultsState?,
    private val searchMultiUseCase: SearchMultiUseCase,
    private val popularMoviesTvUseCase: GetPopularMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers
): UpNextViewModel<SearchResultsAction, SearchResultsState, SearchResultsViewEffect>() {

    override val initialState = initialState ?: SearchResultsState.Idle

    private val reducer: Reducer<SearchResultsState, SearchResultsChange> = { oldState, change ->
        when (change) {
            is SearchResultsChange.Loading -> {
                when (oldState) {
                    is SearchResultsState.Loading -> oldState.copy(results = oldState.results, query = oldState.query)
                    is SearchResultsState.Content -> oldState.copy(results = oldState.results, query = oldState.query)
                    else -> SearchResultsState.Loading(results = emptyList(), query = oldState.query)
                }
            }
            is SearchResultsChange.Result -> {
                when (change.response) {
                    is Response.Success -> {
                        SearchResultsState.Content(
                            results = change.response.data.withDiscoverSearchImageUrls(change.config),
                            cached = change.response.cached,
                            query = change.query
                        )
                    }
                    is Response.Failure -> {
                        SearchResultsState.Error(failure = change.response, query = change.query)
                    }
                }
            }
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        fun Observable<SearchResultsAction.SearchTextChanged>.toResultChange(): Observable<SearchResultsChange> {
            return this.switchMap { action ->
                val query = action.query
                val dataSourceSingle = if (query.isBlank()) {
                    popularMoviesTvUseCase.build()
                } else {
                    searchMultiUseCase.build(query)
                }
                dataSourceSingle.zipWith(getConfigUseCase.build())
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map<SearchResultsChange> { dataAndConfigPair ->
                        SearchResultsChange.Result(action.query, dataAndConfigPair.first, dataAndConfigPair.second)
                    }
                    .startWith(SearchResultsChange.Loading)
            }
        }

        val textSearchedChange = actions.ofType<SearchResultsAction.SearchTextChanged>()
            .debounce { action ->
                // only debounce if query contains text, otherwise show popular straight away
                val milliseconds: Long = if (action.query.isBlank()) 0 else 250
                Observable.just(action).debounce(milliseconds, TimeUnit.MILLISECONDS)
            }
            .distinctUntilChanged()
            .toResultChange()

        val retryButtonChange = actions.ofType<SearchResultsAction.RetryButtonClicked>()
            .map { SearchResultsAction.SearchTextChanged(it.query) }
            .preventMultipleClicks()
            .toResultChange()

        val allChanges = merge(textSearchedChange, retryButtonChange)

        disposables += allChanges
            .scan(initialState, reducer)
            .filter { it !is SearchResultsState.Idle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class SearchResultsAction : BaseAction {
    data class SearchTextChanged(val query: String) : SearchResultsAction()
    data class RetryButtonClicked(val query: String) : SearchResultsAction()
    data class SearchResultClicked(val searchModel: SearchModel) : SearchResultsAction()
}

sealed class SearchResultsChange {
    object Loading : SearchResultsChange()
    data class Result(
        val query: String,
        val response: Response<List<SearchModel>>,
        val config: Config
    ) : SearchResultsChange()
}

sealed class SearchResultsState(open val query: String): BaseState, Parcelable {

    @Parcelize
    object Idle : SearchResultsState("")

    @Parcelize
    data class Loading(
        val results: List<SearchModel> = emptyList(),
        override val query: String
    ) : SearchResultsState(query)

    @Parcelize
    data class Content(
        val results: List<SearchModel> = emptyList(),
        val cached: Boolean = false,
        override val query: String
    ) : SearchResultsState(query)

    @Parcelize
    data class Error(
        val failure: Response.Failure,
        override val query: String
    ) : SearchResultsState(query)
}

sealed class SearchResultsViewEffect : BaseViewEffect

//================================================================================
// Factory
//================================================================================

@PerView
class SearchResultsViewModelFactory(
    private val initialState: SearchResultsState?,
    private val searchMultiUseCase: SearchMultiUseCase,
    private val popularMoviesTvUseCase: GetPopularMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchResultsViewModel(
            initialState,
            searchMultiUseCase,
            popularMoviesTvUseCase,
            getConfigUseCase,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "SearchResultsViewModelFactory"
    }
}
