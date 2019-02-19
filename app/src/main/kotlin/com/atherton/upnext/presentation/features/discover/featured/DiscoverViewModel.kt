package com.atherton.upnext.presentation.features.discover.featured

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModel
import com.atherton.upnext.domain.model.SearchModelViewMode
import com.atherton.upnext.domain.usecase.GetConfigUseCase
import com.atherton.upnext.domain.usecase.GetDiscoverMoviesTvUseCase
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

class DiscoverViewModel @Inject constructor(
    initialState: DiscoverState?,
    private val toggleDiscoverViewModeUseCase: ToggleDiscoverViewModeUseCase,
    private val getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
    private val getDiscoverMoviesTvUseCase: GetDiscoverMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers
): UpNextViewModel<DiscoverAction, DiscoverState, DiscoverViewEffect>() {

    override val initialState = initialState ?: DiscoverState.Idle

    private val reducer: Reducer<DiscoverState, DiscoverChange> = { oldState, change ->
        when (change) {
            is DiscoverChange.Loading -> {
                when (oldState) {
                    is DiscoverState.Loading -> oldState.copy()
                    is DiscoverState.Content -> {
                        DiscoverState.Loading(results = oldState.results)
                    }
                    is DiscoverState.Error -> DiscoverState.Loading()
                    else -> DiscoverState.Loading()
                }
            }
            is DiscoverChange.Result -> {
                when (change.response) {
                    is Response.Success -> {
                        DiscoverState.Content(
                            results = change.response.data.withDiscoverSearchImageUrls(change.config),
                            cached = change.response.cached,
                            viewMode = change.viewMode
                        )
                    }
                    is Response.Failure -> DiscoverState.Error(failure = change.response)
                }
            }
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {

        // handles the initial loading of the view mode menu action icon
        val loadViewModeViewEffect = actions.ofType<DiscoverAction.LoadViewMode>()
            .preventMultipleClicks()
            .switchMap {
                getDiscoverViewModeUseCase.build()
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map { DiscoverViewEffect.ToggleViewMode(it) }
            }

        // handles the toggling of the view mode setting and updating of the toggle button icon in view
        val viewModeToggleViewEffect = actions.ofType<DiscoverAction.ViewModeToggleActionClicked>()
            .preventMultipleClicks()
            .switchMap {
                toggleDiscoverViewModeUseCase.build()
                    .flatMap { getDiscoverViewModeUseCase.build() }
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map { DiscoverViewEffect.ToggleViewMode(it) }
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

sealed class DiscoverAction : BaseAction {
    object Load : DiscoverAction()
    object LoadViewMode : DiscoverAction()
    object ViewModeToggleActionClicked : DiscoverAction()
}

sealed class DiscoverChange {
    object Loading : DiscoverChange()
    data class Result(
        val response: Response<List<SearchModel>>,
        val config: Config,
        val viewMode: SearchModelViewMode
    ) : DiscoverChange()
}

//todo change this class to fetch filters
sealed class DiscoverState : BaseState, Parcelable {

    @Parcelize
    object Idle : DiscoverState()

    @Parcelize
    data class Loading(val results: List<SearchModel> = emptyList()) : DiscoverState()

    @Parcelize
    data class Content(
        val results: List<SearchModel> = emptyList(),
        val cached: Boolean = false,
        val viewMode: SearchModelViewMode
    ) : DiscoverState()

    @Parcelize
    data class Error(val failure: Response.Failure) : DiscoverState()
}

sealed class DiscoverViewEffect : BaseViewEffect {
    data class ToggleViewMode(val viewMode: SearchModelViewMode) : DiscoverViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class DiscoverViewModelFactory(
    private val initialState: DiscoverState?,
    private val toggleDiscoverViewModeUseCase: ToggleDiscoverViewModeUseCase,
    private val getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
    private val getDiscoverMoviesTvUseCase: GetDiscoverMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DiscoverViewModel(
            initialState,
            toggleDiscoverViewModeUseCase,
            getDiscoverViewModeUseCase,
            getDiscoverMoviesTvUseCase,
            getConfigUseCase,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "DiscoverViewModelFactory"
    }
}
