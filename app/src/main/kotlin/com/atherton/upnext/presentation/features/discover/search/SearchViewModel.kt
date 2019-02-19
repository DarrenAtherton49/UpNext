package com.atherton.upnext.presentation.features.discover.search

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModel
import com.atherton.upnext.domain.model.SearchModelViewMode
import com.atherton.upnext.domain.usecase.*
import com.atherton.upnext.presentation.common.withDiscoverSearchImageUrls
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
import io.reactivex.Single.zip
import io.reactivex.functions.Function3
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    initialState: SearchState?,
    private val toggleDiscoverViewModeUseCase: ToggleDiscoverViewModeUseCase,
    private val getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
    private val searchMultiUseCase: SearchMultiUseCase,
    private val popularMoviesTvUseCase: GetPopularMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers
): UpNextViewModel<SearchAction, SearchState, SearchViewEffect>() {

    override val initialState = initialState ?: SearchState.Idle

    private val reducer: Reducer<SearchState, SearchChange> = { oldState, change ->
        when (change) {
            is SearchChange.Loading -> {
                when (oldState) {
                    is SearchState.Loading -> oldState.copy(results = oldState.results, query = oldState.query)
                    is SearchState.Content -> oldState.copy(results = oldState.results, query = oldState.query)
                    else -> SearchState.Loading(results = emptyList(), query = oldState.query)
                }
            }
            is SearchChange.Result -> {
                when (change.response) {
                    is Response.Success -> {
                        SearchState.Content(
                            results = change.response.data.withDiscoverSearchImageUrls(change.config),
                            cached = change.response.cached,
                            query = change.query,
                            viewMode = change.viewMode
                        )
                    }
                    is Response.Failure -> {
                        SearchState.Error(failure = change.response, query = change.query)
                    }
                }
            }
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        fun Observable<SearchAction.SearchTextChanged>.toResultChange(): Observable<SearchChange> {
            return this.switchMap { action ->
                val query = action.query
                val dataSourceSingle = if (query.isBlank()) {
                    popularMoviesTvUseCase.build()
                } else {
                    searchMultiUseCase.build(query)
                }
                zip(dataSourceSingle,
                    getConfigUseCase.build(),
                    getDiscoverViewModeUseCase.build(),
                    Function3<Response<List<SearchModel>>,
                        Config,
                        SearchModelViewMode,
                        SearchViewData> { searchModels, config, viewMode ->
                        SearchViewData(searchModels, config, viewMode)
                    })
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map<SearchChange> { viewData ->
                        SearchChange.Result(
                            query = action.query,
                            response = viewData.searchModels,
                            config = viewData.config,
                            viewMode = viewData.viewMode
                        )
                    }
                    .startWith(SearchChange.Loading)
            }
        }

        val viewModeChange = actions.ofType<SearchAction.ViewModeToggleActionClicked>()
            .map { SearchAction.SearchTextChanged(it.query) }
            .preventMultipleClicks()
            .toResultChange()

        val textSearchedChange = actions.ofType<SearchAction.SearchTextChanged>()
            .debounce { action ->
                // only debounce if query contains text, otherwise show popular straight away
                val milliseconds: Long = if (action.query.isBlank()) 0 else 250
                Observable.just(action).debounce(milliseconds, TimeUnit.MILLISECONDS)
            }
            .distinctUntilChanged()
            .toResultChange()

        val retryButtonChange = actions.ofType<SearchAction.RetryButtonClicked>()
            .map { SearchAction.SearchTextChanged(it.query) }
            .preventMultipleClicks()
            .toResultChange()

        // handles the initial loading of the view mode menu action icon
        val loadViewModeViewEffect = actions.ofType<SearchAction.LoadViewMode>()
            .preventMultipleClicks()
            .switchMap {
                getDiscoverViewModeUseCase.build()
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map { SearchViewEffect.ToggleViewMode(it) }
            }

        // handles the toggling of the view mode setting and updating of the toggle button icon in view
        val viewModeToggleViewEffect = actions.ofType<SearchAction.ViewModeToggleActionClicked>()
            .preventMultipleClicks()
            .switchMap {
                toggleDiscoverViewModeUseCase.build()
                    .flatMap { getDiscoverViewModeUseCase.build() }
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map { SearchViewEffect.ToggleViewMode(it) }
            }

        val stateChanges = merge(textSearchedChange, retryButtonChange, viewModeChange)

        val viewEffectChanges = merge(loadViewModeViewEffect, viewModeToggleViewEffect)

        disposables += viewEffectChanges
            .observeOn(schedulers.main)
            .subscribe(viewEffects::accept, Timber::e)

        disposables += stateChanges
            .scan(initialState, reducer)
            .filter { it !is SearchState.Idle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class SearchAction : BaseAction {
    object LoadViewMode : SearchAction()
    data class ViewModeToggleActionClicked(val query: String) : SearchAction()
    data class SearchTextChanged(val query: String) : SearchAction()
    data class RetryButtonClicked(val query: String) : SearchAction()
    data class SearchResultClicked(val searchModel: SearchModel) : SearchAction()
}

sealed class SearchChange {
    object Loading : SearchChange()
    data class Result(
        val query: String,
        val response: Response<List<SearchModel>>,
        val config: Config,
        val viewMode: SearchModelViewMode
    ) : SearchChange()
}

sealed class SearchState(open val query: String): BaseState, Parcelable {

    @Parcelize
    object Idle : SearchState("")

    @Parcelize
    data class Loading(
        val results: List<SearchModel> = emptyList(),
        override val query: String
    ) : SearchState(query)

    @Parcelize
    data class Content(
        val results: List<SearchModel> = emptyList(),
        val cached: Boolean = false,
        override val query: String,
        val viewMode: SearchModelViewMode
    ) : SearchState(query)

    @Parcelize
    data class Error(
        val failure: Response.Failure,
        override val query: String
    ) : SearchState(query)
}

sealed class SearchViewEffect : BaseViewEffect {
    data class ToggleViewMode(val viewMode: SearchModelViewMode) : SearchViewEffect()
}

//================================================================================
// Data model
//================================================================================

// this class is just used as the result of zipping the necessary Observables together
private data class SearchViewData(
    val searchModels: Response<List<SearchModel>>,
    val config: Config,
    val viewMode: SearchModelViewMode
)

//================================================================================
// Factory
//================================================================================

@PerView
class SearchViewModelFactory(
    private val initialState: SearchState?,
    private val toggleDiscoverViewModeUseCase: ToggleDiscoverViewModeUseCase,
    private val getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
    private val searchMultiUseCase: SearchMultiUseCase,
    private val popularMoviesTvUseCase: GetPopularMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchViewModel(
            initialState,
            toggleDiscoverViewModeUseCase,
            getDiscoverViewModeUseCase,
            searchMultiUseCase,
            popularMoviesTvUseCase,
            getConfigUseCase,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "SearchViewModelFactory"
    }
}
