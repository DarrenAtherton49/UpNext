package com.atherton.upnext.presentation.features.discover.content

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.*
import com.atherton.upnext.domain.repository.ConfigRepository
import com.atherton.upnext.domain.repository.SettingsRepository
import com.atherton.upnext.domain.usecase.GetDiscoverItemsForFilterUseCase
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
import javax.inject.Inject

class DiscoverContentViewModel @Inject constructor(
    initialState: DiscoverContentState?,
    private val settingsRepository: SettingsRepository,
    private val getDiscoverItemsForFilterUseCase: GetDiscoverItemsForFilterUseCase,
    private val configRepository: ConfigRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
): UpNextViewModel<DiscoverContentAction, DiscoverContentState, DiscoverContentViewEffect>() {

    override val initialState = initialState ?: DiscoverContentState.Idle

    private val reducer: Reducer<DiscoverContentState, DiscoverContentChange> = { oldState, change ->
        when (change) {
            is DiscoverContentChange.Loading -> {
                when (oldState) {
                    is DiscoverContentState.Idle -> DiscoverContentState.Loading(results = null, viewMode = null)
                    is DiscoverContentState.Loading -> oldState.copy()
                    is DiscoverContentState.Content -> {
                        DiscoverContentState.Loading(results = oldState.results, viewMode = null)
                    }
                    is DiscoverContentState.Error -> DiscoverContentState.Loading(results = null, viewMode = null)
                }
            }
            is DiscoverContentChange.Result -> {
                when (change.response) {
                    is LceResponse.Loading -> {
                        DiscoverContentState.Loading(
                            results = change.response.data.withSearchModelListImageUrls(change.config),
                            viewMode = change.viewMode
                        )
                    }
                    is LceResponse.Content -> {
                        DiscoverContentState.Content(
                            results = change.response.data.withSearchModelListImageUrls(change.config),
                            cached = change.response.cached,
                            viewMode = change.viewMode
                        )
                    }
                    is LceResponse.Error -> {
                        DiscoverContentState.Error(
                            message = appStringProvider.generateErrorMessage(change.response),
                            canRetry = change.response is LceResponse.Error.NetworkError
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
        fun Observable<DiscoverContentAction.Load>.toResultChange(): Observable<DiscoverContentChange> {
            return this.switchMap { action ->
                getDiscoverItemsForFilterUseCase.invoke(action.filter)
                    .map<DiscoverContentChange> { contentModels ->
                        DiscoverContentChange.Result(
                            response = contentModels,
                            config = configRepository.getConfig(),
                            viewMode = settingsRepository.getGridViewMode()
                        )
                    }
                    .subscribeOn(schedulers.io)
                    .startWith(DiscoverContentChange.Loading)
            }
        }

        val loadDataChange = actions.ofType<DiscoverContentAction.Load>()
            .distinctUntilChanged()
            .toResultChange()

        val retryButtonChange = actions.ofType<DiscoverContentAction.RetryButtonClicked>()
            .map { DiscoverContentAction.Load(null, it.filter) }
            .toResultChange()

        val searchModelClickedViewEffect = actions.ofType<DiscoverContentAction.SearchModelClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { action ->
                when (action.searchModel) {
                    is TvShow -> DiscoverContentViewEffect.ShowTvShowDetailScreen(action.searchModel.id)
                    is Movie -> DiscoverContentViewEffect.ShowMovieDetailScreen(action.searchModel.id)
                    is Person -> DiscoverContentViewEffect.ShowPersonDetailScreen(action.searchModel.id)
                    else -> throw IllegalStateException("Search model must be either a tv show, movie or person")
                }
            }

        val stateChanges = merge(loadDataChange, retryButtonChange)

        disposables += stateChanges
            .scan(initialState, reducer)
            .filter { it !is DiscoverContentState.Idle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)

        disposables += searchModelClickedViewEffect
            .observeOn(schedulers.main)
            .subscribe(viewEffects::accept, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class DiscoverContentAction : BaseAction {
    data class Load(val viewMode: GridViewMode?, val filter: DiscoverFilter) : DiscoverContentAction()
    data class RetryButtonClicked(val filter: DiscoverFilter) : DiscoverContentAction()
    data class SearchModelClicked(val searchModel: Searchable) : DiscoverContentAction()
}

sealed class DiscoverContentChange {
    object Loading : DiscoverContentChange()
    data class Result(
        val response: LceResponse<List<Searchable>>,
        val config: Config,
        val viewMode: GridViewMode
    ) : DiscoverContentChange()
}

sealed class DiscoverContentState : BaseState, Parcelable {

    @Parcelize
    object Idle : DiscoverContentState()

    @Parcelize
    data class Loading(val results: List<Searchable>?, val viewMode: GridViewMode?) : DiscoverContentState()

    @Parcelize
    data class Content(
        val results: List<Searchable>,
        val cached: Boolean = false,
        val viewMode: GridViewMode
    ) : DiscoverContentState()

    @Parcelize
    data class Error(val message: String, val canRetry: Boolean) : DiscoverContentState()
}

sealed class DiscoverContentViewEffect : BaseViewEffect {
    data class ShowTvShowDetailScreen(val tvShowId: Int) : DiscoverContentViewEffect()
    data class ShowMovieDetailScreen(val movieId: Int) : DiscoverContentViewEffect()
    data class ShowPersonDetailScreen(val personId: Int) : DiscoverContentViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class DiscoverContentViewModelFactory(
    private val initialState: DiscoverContentState?,
    private val settingsRepository: SettingsRepository,
    private val getDiscoverItemsForFilterUseCase: GetDiscoverItemsForFilterUseCase,
    private val configRepository: ConfigRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DiscoverContentViewModel(
            initialState,
            settingsRepository,
            getDiscoverItemsForFilterUseCase,
            configRepository,
            appStringProvider,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "DiscoverContentViewModelFactory"
    }
}
