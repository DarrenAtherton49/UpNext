package com.atherton.upnext.presentation.features.discover.search

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.*
import com.atherton.upnext.domain.usecase.GetConfigUseCase
import com.atherton.upnext.domain.usecase.PopularMoviesTvUseCase
import com.atherton.upnext.domain.usecase.SearchMultiUseCase
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
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
    private val popularMoviesTvUseCase: PopularMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers
): BaseViewModel<SearchResultsAction, SearchResultsState>() {

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
                    is Response.Success -> { // success state (could be fresh or cached data)
                        SearchResultsState.Content(
                            results = change.response.data.withImageUrls(change.config),
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

        val searchTransformer: ObservableTransformer<SearchResultsAction.SearchTextChanged, SearchResultsChange> = ObservableTransformer {
                it.switchMap { action ->
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
                        .defaultIfEmpty(
                            SearchResultsChange.Result(
                                query = action.query,
                                response = Response.Success(emptyList(), false),
                                config = null
                            )
                        )
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
            .compose(searchTransformer)

        val retryButtonChange = actions.ofType<SearchResultsAction.RetryButtonClicked>()
            .map { SearchResultsAction.SearchTextChanged(it.query) }
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .compose(searchTransformer)

        val allChanges = Observable.merge(textSearchedChange, retryButtonChange)

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
        val config: Config?
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

//================================================================================
// View-Specific Mappers
//================================================================================

// generate image urls for this screen using the base url, size and path
private fun List<SearchModel>.withImageUrls(config: Config?): List<SearchModel> {

    //todo write function to generate path based on device screen size?
    fun buildPosterPath(posterPath: String?, config: Config): String? =
        posterPath?.let { "${config.secureBaseUrl}${config.posterSizes[2]}$posterPath" }

    //todo write function to generate path based on device screen size?
    fun buildProfilePath(profilePath: String?, config: Config): String? =
        profilePath?.let { "${config.secureBaseUrl}${config.profileSizes[1]}$profilePath" }

    //todo write function to generate path based on device screen size?
    fun buildBackdropPath(backdropPath: String?, config: Config): String? =
        backdropPath?.let { "${config.secureBaseUrl}${config.profileSizes[1]}$backdropPath" }

    return if (config != null) {
        this.map {
            when (it) {
                is TvShow -> {
                    it.copy(
                        backdropPath = buildBackdropPath(it.backdropPath, config),
                        posterPath = buildPosterPath(it.posterPath, config)
                    )
                }
                is Movie -> {
                    it.copy(
                        backdropPath = buildBackdropPath(it.backdropPath, config),
                        posterPath = buildPosterPath(it.posterPath, config)
                    )
                }
                is Person -> it.copy(profilePath = buildProfilePath(it.profilePath, config))
            }
        }
    } else this
}

//================================================================================
// Factory
//================================================================================

@PerView
class SearchResultsViewModelFactory(
    private val initialState: SearchResultsState?,
    private val searchMultiUseCase: SearchMultiUseCase,
    private val popularMoviesTvUseCase: PopularMoviesTvUseCase,
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
