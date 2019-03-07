package com.atherton.upnext.presentation.features.discover

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.DiscoverFilter
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModelViewMode
import com.atherton.upnext.domain.usecase.GetDiscoverFiltersUseCase
import com.atherton.upnext.domain.usecase.GetDiscoverViewModeUseCase
import com.atherton.upnext.domain.usecase.ToggleDiscoverViewModeUseCase
import com.atherton.upnext.util.base.BaseViewEffect
import com.atherton.upnext.util.base.UpNextViewModel
import com.atherton.upnext.util.extensions.preventMultipleClicks
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
    private val toggleDiscoverViewModeUseCase: ToggleDiscoverViewModeUseCase,
    private val getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
    private val getDiscoverFiltersUseCase: GetDiscoverFiltersUseCase,
    private val schedulers: RxSchedulers
): UpNextViewModel<DiscoverTabsAction, DiscoverTabsState, DiscoverTabsViewEffect>() {

    override val initialState = initialState ?: DiscoverTabsState.Idle

    private val reducer: Reducer<DiscoverTabsState, DiscoverTabsChange> = { oldState, change ->
        when (change) {
            is DiscoverTabsChange.Loading -> {
                when (oldState) {
                    is DiscoverTabsState.Idle -> DiscoverTabsState.Loading()
                    is DiscoverTabsState.Loading -> oldState.copy()
                    is DiscoverTabsState.Content -> {
                        DiscoverTabsState.Loading(results = oldState.results)
                    }
                    is DiscoverTabsState.Error -> DiscoverTabsState.Loading()
                }
            }
            is DiscoverTabsChange.Result -> {
                when (change.response) {
                    is Response.Success -> {
                        DiscoverTabsState.Content(
                            results = change.response.data
                        )
                    }
                    is Response.Failure -> DiscoverTabsState.Error(failure = change.response)
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
                getDiscoverFiltersUseCase.invoke()
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map<DiscoverTabsChange> { filtersResponse -> DiscoverTabsChange.Result(filtersResponse) }
                    .startWith(DiscoverTabsChange.Loading)
            }

        // handles the initial loading of the view mode menu action icon
        val loadViewModeViewEffect = actions.ofType<DiscoverTabsAction.LoadViewMode>()
            .preventMultipleClicks()
            .switchMap {
                getDiscoverViewModeUseCase.invoke()
                    .subscribeOn(schedulers.io)
                    .map { DiscoverTabsViewEffect.ViewModeLoaded(it) }
            }

        // handles the toggling of the view mode setting and updating of the toggle button icon in view
        val viewModeToggleViewEffect = actions.ofType<DiscoverTabsAction.ViewModeToggleActionClicked>()
            .preventMultipleClicks()
            .switchMap {
                toggleDiscoverViewModeUseCase.invoke()
                    .flatMap { getDiscoverViewModeUseCase.invoke() }
                    .subscribeOn(schedulers.io)
                    .map { DiscoverTabsViewEffect.ViewModeToggled(it) }
            }

        disposables += loadDataChange
            .scan(initialState, reducer)
            .filter { it !is DiscoverTabsState.Idle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)

        val viewEffectChanges = merge(loadViewModeViewEffect, viewModeToggleViewEffect)

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
}

sealed class DiscoverTabsChange {
    object Loading : DiscoverTabsChange()
    data class Result(
        val response: Response<List<DiscoverFilter>>
    ) : DiscoverTabsChange()
}

sealed class DiscoverTabsState : BaseState, Parcelable {

    @Parcelize
    object Idle : DiscoverTabsState()

    @Parcelize
    data class Loading(val results: List<DiscoverFilter> = emptyList()) : DiscoverTabsState()

    @Parcelize
    data class Content(val results: List<DiscoverFilter> = emptyList()) : DiscoverTabsState()

    @Parcelize
    data class Error(val failure: Response.Failure) : DiscoverTabsState()
}

sealed class DiscoverTabsViewEffect : BaseViewEffect {
    data class ViewModeToggled(val viewMode: SearchModelViewMode) : DiscoverTabsViewEffect()
    data class ViewModeLoaded(val viewMode: SearchModelViewMode) : DiscoverTabsViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class DiscoverTabsViewModelFactory(
    private val initialState: DiscoverTabsState?,
    private val toggleDiscoverViewModeUseCase: ToggleDiscoverViewModeUseCase,
    private val getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
    private val getDiscoverFiltersUseCase: GetDiscoverFiltersUseCase,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DiscoverTabsViewModel(
            initialState,
            toggleDiscoverViewModeUseCase,
            getDiscoverViewModeUseCase,
            getDiscoverFiltersUseCase,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "DiscoverTabsViewModelFactory"
    }
}
