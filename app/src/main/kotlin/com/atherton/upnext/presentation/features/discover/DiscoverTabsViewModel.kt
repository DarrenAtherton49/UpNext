package com.atherton.upnext.presentation.features.discover

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.DiscoverFilter
import com.atherton.upnext.domain.model.GridViewMode
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.repository.FilterRepository
import com.atherton.upnext.domain.repository.SettingsRepository
import com.atherton.upnext.presentation.util.AppStringProvider
import com.atherton.upnext.presentation.base.BaseViewEffect
import com.atherton.upnext.presentation.base.UpNextViewModel
import com.atherton.upnext.presentation.util.extension.preventMultipleClicks
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.Reducer
import io.reactivex.Observable.merge
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

class DiscoverTabsViewModel @Inject constructor(
    initialState: DiscoverTabsState?,
    private val settingsRepository: SettingsRepository,
    private val filterRepository: FilterRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
): UpNextViewModel<DiscoverTabsAction, DiscoverTabsState, DiscoverTabsViewEffect>() {

    override val initialState = initialState ?: DiscoverTabsState.Idle

    private val reducer: Reducer<DiscoverTabsState, DiscoverTabsChange> = { oldState, change ->
        when (change) {
            is DiscoverTabsChange.Loading -> {
                when (oldState) {
                    is DiscoverTabsState.Idle -> DiscoverTabsState.Loading(results = null)
                    is DiscoverTabsState.Loading -> oldState.copy()
                    is DiscoverTabsState.Content -> {
                        DiscoverTabsState.Loading(results = oldState.results)
                    }
                    is DiscoverTabsState.Error -> DiscoverTabsState.Loading(results = null)
                }
            }
            is DiscoverTabsChange.Result -> {
                when (change.response) {
                    is LceResponse.Loading -> DiscoverTabsState.Loading(results = change.response.data)
                    is LceResponse.Content -> DiscoverTabsState.Content(results = change.response.data)
                    is LceResponse.Error -> {
                        DiscoverTabsState.Error(
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

        val loadDataChange = actions.ofType<DiscoverTabsAction.Load>()
            .distinctUntilChanged()
            .switchMap {
                filterRepository.getFiltersObservable()
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map<DiscoverTabsChange> { filtersResponse -> DiscoverTabsChange.Result(filtersResponse) }
                    .startWith(DiscoverTabsChange.Loading)
            }

        // handles the initial loading of the view mode menu action icon
        val loadViewModeViewEffect = actions.ofType<DiscoverTabsAction.LoadViewMode>()
            .preventMultipleClicks()
            .switchMap {
                settingsRepository.getGridViewModeObservable()
                    .subscribeOn(schedulers.io)
                    .map { DiscoverTabsViewEffect.ViewModeLoaded(it) }
            }

        // handles the toggling of the view mode setting and updating of the toggle button icon in view
        val viewModeToggleViewEffect = actions.ofType<DiscoverTabsAction.ViewModeToggleActionClicked>()
            .preventMultipleClicks()
            .switchMap {
                settingsRepository.toggleGridViewModeObservable()
                    .flatMap { settingsRepository.getGridViewModeObservable() }
                    .subscribeOn(schedulers.io)
                    .map { DiscoverTabsViewEffect.ViewModeToggled(it) }
            }

        val settingsActionClickedViewEffect = actions.ofType<DiscoverTabsAction.SettingsActionClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { DiscoverTabsViewEffect.ShowSettingsScreen }

        disposables += loadDataChange
            .scan(initialState, reducer)
            .filter { it !is DiscoverTabsState.Idle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)

        val viewEffectChanges = merge(
            loadViewModeViewEffect,
            viewModeToggleViewEffect,
            settingsActionClickedViewEffect
        )

        disposables += viewEffectChanges
            .observeOn(schedulers.main)
            .subscribe(viewEffects::accept, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class DiscoverTabsAction : BaseAction {
    object Load : DiscoverTabsAction()
    object LoadViewMode : DiscoverTabsAction()
    object ViewModeToggleActionClicked : DiscoverTabsAction()
    object SettingsActionClicked : DiscoverTabsAction()
}

sealed class DiscoverTabsChange {

    object Loading : DiscoverTabsChange()

    data class Result(
        val response: LceResponse<List<DiscoverFilter>>
    ) : DiscoverTabsChange()
}

sealed class DiscoverTabsState : BaseState, Parcelable {

    @Parcelize
    object Idle : DiscoverTabsState()

    @Parcelize
    data class Loading(val results: List<DiscoverFilter>?) : DiscoverTabsState()

    @Parcelize
    data class Content(val results: List<DiscoverFilter>) : DiscoverTabsState()

    @Parcelize
    data class Error(val message: String, val canRetry: Boolean) : DiscoverTabsState()
}

sealed class DiscoverTabsViewEffect : BaseViewEffect {
    data class ViewModeToggled(val viewMode: GridViewMode) : DiscoverTabsViewEffect()
    data class ViewModeLoaded(val viewMode: GridViewMode) : DiscoverTabsViewEffect()
    object ShowSettingsScreen : DiscoverTabsViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class DiscoverTabsViewModelFactory(
    private val initialState: DiscoverTabsState?,
    private val settingsRepository: SettingsRepository,
    private val filterRepository: FilterRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DiscoverTabsViewModel(
            initialState,
            settingsRepository,
            filterRepository,
            appStringProvider,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "DiscoverTabsViewModelFactory"
    }
}
