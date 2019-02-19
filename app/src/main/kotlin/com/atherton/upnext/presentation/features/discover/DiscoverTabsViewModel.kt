package com.atherton.upnext.presentation.features.discover

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModel
import com.atherton.upnext.domain.model.SearchModelViewMode
import com.atherton.upnext.domain.usecase.GetDiscoverViewModeUseCase
import com.atherton.upnext.domain.usecase.ToggleDiscoverViewModeUseCase
import com.atherton.upnext.presentation.common.withDiscoverSearchImageUrls
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
    private val schedulers: RxSchedulers
): UpNextViewModel<DiscoverTabsAction, DiscoverTabsState, DiscoverTabsViewEffect>() {

    override val initialState = initialState ?: DiscoverTabsState.Idle

    //todo use reducer to render filters (tabs)
    private val reducer: Reducer<DiscoverTabsState, DiscoverTabsChange> = { oldState, change ->
        when (change) {
            is DiscoverTabsChange.Loading -> {
                when (oldState) {
                    is DiscoverTabsState.Loading -> oldState.copy()
                    is DiscoverTabsState.Content -> {
                        DiscoverTabsState.Loading(results = oldState.results)
                    }
                    is DiscoverTabsState.Error -> DiscoverTabsState.Loading()
                    else -> DiscoverTabsState.Loading()
                }
            }
            is DiscoverTabsChange.Result -> {
                when (change.response) {
                    is Response.Success -> {
                        DiscoverTabsState.Content(
                            results = change.response.data.withDiscoverSearchImageUrls(change.config),
                            cached = change.response.cached,
                            viewMode = change.viewMode
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

        // handles the initial loading of the view mode menu action icon
        val loadViewModeViewEffect = actions.ofType<DiscoverTabsAction.LoadViewMode>()
            .preventMultipleClicks()
            .switchMap {
                getDiscoverViewModeUseCase.build()
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map { DiscoverTabsViewEffect.ToggleViewMode(it) }
            }

        // handles the toggling of the view mode setting and updating of the toggle button icon in view
        val viewModeToggleViewEffect = actions.ofType<DiscoverTabsAction.ViewModeToggleActionClicked>()
            .preventMultipleClicks()
            .switchMap {
                toggleDiscoverViewModeUseCase.build()
                    .flatMap { getDiscoverViewModeUseCase.build() }
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map { DiscoverTabsViewEffect.ToggleViewMode(it) }
            }

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
    object Load : DiscoverTabsAction() //todo use this to load filters (tabs)
    object LoadViewMode : DiscoverTabsAction()
    object ViewModeToggleActionClicked : DiscoverTabsAction()
}

sealed class DiscoverTabsChange {
    object Loading : DiscoverTabsChange()
    data class Result(
        val response: Response<List<SearchModel>>,
        val config: Config,
        val viewMode: SearchModelViewMode
    ) : DiscoverTabsChange()
}

//todo change this class to fetch filters
sealed class DiscoverTabsState : BaseState, Parcelable {

    @Parcelize
    object Idle : DiscoverTabsState()

    @Parcelize
    data class Loading(val results: List<SearchModel> = emptyList()) : DiscoverTabsState()

    @Parcelize
    data class Content(
        val results: List<SearchModel> = emptyList(),
        val cached: Boolean = false,
        val viewMode: SearchModelViewMode
    ) : DiscoverTabsState()

    @Parcelize
    data class Error(val failure: Response.Failure) : DiscoverTabsState()
}

sealed class DiscoverTabsViewEffect : BaseViewEffect {
    data class ToggleViewMode(val viewMode: SearchModelViewMode) : DiscoverTabsViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class DiscoverTabsViewModelFactory(
    private val initialState: DiscoverTabsState?,
    private val toggleDiscoverViewModeUseCase: ToggleDiscoverViewModeUseCase,
    private val getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DiscoverTabsViewModel(
            initialState,
            toggleDiscoverViewModeUseCase,
            getDiscoverViewModeUseCase,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "DiscoverTabsViewModelFactory"
    }
}
