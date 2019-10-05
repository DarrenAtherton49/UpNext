package com.atherton.upnext.presentation.features.shows

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.ContentList
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.repository.TvShowRepository
import com.atherton.upnext.presentation.base.BaseViewEffect
import com.atherton.upnext.presentation.base.UpNextViewModel
import com.atherton.upnext.presentation.util.AppStringProvider
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

class ShowsViewModel @Inject constructor(
    initialState: ShowsState?,
    private val tvShowRepository: TvShowRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : UpNextViewModel<ShowsAction, ShowsState, ShowsViewEffect>() {

    override val initialState = initialState ?: ShowsState.Idle

    private val reducer: Reducer<ShowsState, ShowsChange> = { oldState, change ->
        when (change) {
            is ShowsChange.Loading -> {
                when (oldState) {
                    is ShowsState.Idle -> ShowsState.Loading(results = null)
                    is ShowsState.Loading -> oldState.copy()
                    is ShowsState.Content -> {
                        ShowsState.Loading(results = oldState.results)
                    }
                    is ShowsState.Error -> ShowsState.Loading(results = null)
                }
            }
            is ShowsChange.Result -> {
                when (change.response) {
                    is LceResponse.Loading -> ShowsState.Loading(results = change.response.data)
                    is LceResponse.Content -> ShowsState.Content(results = change.response.data)
                    is LceResponse.Error -> {
                        ShowsState.Error(
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

        val loadDataChange = actions.ofType<ShowsAction.Load>()
            .distinctUntilChanged()
            .switchMap {
                tvShowRepository.getTvShowLists()
                    .subscribeOn(schedulers.io)
                    .map<ShowsChange> { tvShowListsResponse -> ShowsChange.Result(tvShowListsResponse) }
                    .startWith(ShowsChange.Loading)
            }

        val searchActionClickedViewEffect = actions.ofType<ShowsAction.SearchActionClicked>()
            .preventMultipleClicks()
            .map { ShowsViewEffect.ShowSearchScreen }

        val settingsActionClickedViewEffect = actions.ofType<ShowsAction.SettingsActionClicked>()
            .preventMultipleClicks()
            .map { ShowsViewEffect.ShowSettingsScreen }

        val viewEffectChanges = merge(searchActionClickedViewEffect, settingsActionClickedViewEffect)

        disposables += loadDataChange
            .scan(initialState, reducer)
            .filter { it !is ShowsState.Idle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)

        disposables += viewEffectChanges
            .observeOn(schedulers.main)
            .subscribe(viewEffects::accept, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class ShowsAction : BaseAction {
    object Load : ShowsAction()
    object SearchActionClicked : ShowsAction()
    object SettingsActionClicked : ShowsAction()
}

sealed class ShowsChange {
    object Loading : ShowsChange()
    data class Result(val response: LceResponse<List<ContentList>>) : ShowsChange()
}

sealed class ShowsState : BaseState, Parcelable {

    @Parcelize
    object Idle : ShowsState()

    @Parcelize
    data class Loading(val results: List<ContentList>?) : ShowsState()

    @Parcelize
    data class Content(val results: List<ContentList>) : ShowsState()

    @Parcelize
    data class Error(val message: String, val canRetry: Boolean) : ShowsState()
}

sealed class ShowsViewEffect : BaseViewEffect {
    object ShowSearchScreen : ShowsViewEffect()
    object ShowSettingsScreen : ShowsViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class ShowsViewModelFactory(
    private val initialState: ShowsState?,
    private val tvShowRepository: TvShowRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ShowsViewModel(
            initialState,
            tvShowRepository,
            appStringProvider,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "ShowsViewModelFactory"
    }
}
