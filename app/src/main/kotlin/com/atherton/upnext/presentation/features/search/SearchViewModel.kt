package com.atherton.upnext.presentation.features.search

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.*
import com.atherton.upnext.domain.repository.ConfigRepository
import com.atherton.upnext.domain.repository.SearchRepository
import com.atherton.upnext.domain.repository.SettingsRepository
import com.atherton.upnext.domain.usecase.GetPopularMoviesTvUseCase
import com.atherton.upnext.presentation.common.searchmodel.withSearchModelListImageUrls
import com.atherton.upnext.presentation.util.AppStringProvider
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
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    initialState: SearchState?,
    private val settingsRepository: SettingsRepository,
    private val searchRepository: SearchRepository,
    private val popularMoviesTvUseCase: GetPopularMoviesTvUseCase,
    private val configRepository: ConfigRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
): UpNextViewModel<SearchAction, SearchState, SearchViewEffect>() {

    override val initialState = initialState ?: SearchState.Idle

    private val reducer: Reducer<SearchState, SearchChange> = { oldState, change ->
        when (change) {
            is SearchChange.Loading -> {
                when (oldState) {
                    is SearchState.Loading -> oldState.copy(results = oldState.results, query = oldState.query)
                    is SearchState.Content -> oldState.copy(results = oldState.results, query = oldState.query)
                    else -> SearchState.Loading(results = null, viewMode = null, query = oldState.query)
                }
            }
            is SearchChange.Result -> {
                when (change.response) {
                    is LceResponse.Loading -> {
                        SearchState.Loading(
                            results = change.response.data?.withSearchModelListImageUrls(change.config),
                            viewMode = change.viewMode,
                            query = change.query
                        )
                    }
                    is LceResponse.Content -> {
                        SearchState.Content(
                            results = change.response.data.withSearchModelListImageUrls(change.config),
                            query = change.query,
                            viewMode = change.viewMode
                        )
                    }
                    is LceResponse.Error -> {
                        SearchState.Error(
                            message = appStringProvider.generateErrorMessage(change.response),
                            canRetry = change.response is LceResponse.Error.NetworkError,
                            query = change.query,
                            viewMode = change.viewMode,
                            fallbackResults = change.response.fallbackData?.withSearchModelListImageUrls(change.config)
                        )
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
                val dataSourceObservable = if (query.isBlank()) {
                    popularMoviesTvUseCase.invoke()
                } else {
                    searchRepository.searchMulti(query)
                }
                dataSourceObservable
                    .subscribeOn(schedulers.io)
                    .map<SearchChange> { searchModels ->
                        SearchChange.Result(
                            query = action.query,
                            response = searchModels,
                            config = configRepository.getConfig(),
                            viewMode = settingsRepository.getGridViewMode()
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
                settingsRepository.getGridViewModeObservable()
                    .subscribeOn(schedulers.io)
                    .map { SearchViewEffect.ToggleViewMode(it) }
            }

        // handles the toggling of the view mode setting and updating of the toggle button icon in view
        val viewModeToggleViewEffect = actions.ofType<SearchAction.ViewModeToggleActionClicked>()
            .preventMultipleClicks()
            .switchMap {
                settingsRepository.toggleGridViewModeObservable()
                    .flatMap { settingsRepository.getGridViewModeObservable() }
                    .subscribeOn(schedulers.io)
                    .map { SearchViewEffect.ToggleViewMode(it) }
            }

        val searchResultClickedViewEffect = actions.ofType<SearchAction.SearchResultClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { action ->
                when (action.searchModel) {
                    is TvShow -> SearchViewEffect.ShowTvShowDetailScreen(action.searchModel.id)
                    is Movie -> SearchViewEffect.ShowMovieDetailScreen(action.searchModel.id)
                    is Person -> SearchViewEffect.ShowPersonDetailScreen(action.searchModel.id)
                    else -> throw IllegalStateException("Search model must be either a tv show, movie or person")
                }
            }

        val settingsActionClickedViewEffect = actions.ofType<SearchAction.SettingsActionClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { SearchViewEffect.ShowSettingsScreen }

        val stateChanges = merge(textSearchedChange, retryButtonChange, viewModeChange)

        val viewEffectChanges = merge(
            loadViewModeViewEffect,
            viewModeToggleViewEffect,
            searchResultClickedViewEffect,
            settingsActionClickedViewEffect
        )

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
    data class SearchResultClicked(val searchModel: Searchable) : SearchAction()
    object SettingsActionClicked : SearchAction()
}

sealed class SearchChange {
    object Loading : SearchChange()
    data class Result(
        val query: String,
        val response: LceResponse<List<Searchable>>,
        val config: Config,
        val viewMode: GridViewMode
    ) : SearchChange()
}

sealed class SearchState(open val query: String): BaseState, Parcelable {

    @Parcelize
    object Idle : SearchState("")

    @Parcelize
    data class Loading(
        val results: List<Searchable>?,
        val viewMode: GridViewMode?,
        override val query: String
    ) : SearchState(query)

    @Parcelize
    data class Content(
        val results: List<Searchable>,
        override val query: String,
        val viewMode: GridViewMode
    ) : SearchState(query)

    @Parcelize
    data class Error(
        val message: String,
        val canRetry: Boolean,
        override val query: String,
        val viewMode: GridViewMode,
        val fallbackResults: List<Searchable>?
    ) : SearchState(query)
}

sealed class SearchViewEffect : BaseViewEffect {
    data class ToggleViewMode(val viewMode: GridViewMode) : SearchViewEffect()
    data class ShowTvShowDetailScreen(val tvShowId: Long) : SearchViewEffect()
    data class ShowMovieDetailScreen(val movieId: Long) : SearchViewEffect()
    data class ShowPersonDetailScreen(val personId: Long) : SearchViewEffect()
    object ShowSettingsScreen : SearchViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class SearchViewModelFactory(
    private val initialState: SearchState?,
    private val settingsRepository: SettingsRepository,
    private val searchRepository: SearchRepository,
    private val popularMoviesTvUseCase: GetPopularMoviesTvUseCase,
    private val configRepository: ConfigRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchViewModel(
            initialState,
            settingsRepository,
            searchRepository,
            popularMoviesTvUseCase,
            configRepository,
            appStringProvider,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "SearchViewModelFactory"
    }
}
