package com.atherton.upnext.presentation.features.shows.content

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.model.ContentList
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.TvShow
import com.atherton.upnext.domain.repository.ConfigRepository
import com.atherton.upnext.domain.repository.TvShowRepository
import com.atherton.upnext.presentation.base.BaseViewEffect
import com.atherton.upnext.presentation.base.UpNextViewModel
import com.atherton.upnext.presentation.common.searchmodel.formattedForShowList
import com.atherton.upnext.presentation.util.AppStringProvider
import com.atherton.upnext.presentation.util.extension.preventMultipleClicks
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

class ShowListViewModel @Inject constructor(
    initialState: ShowListState?,
    private val tvShowRepository: TvShowRepository,
    private val configRepository: ConfigRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
): UpNextViewModel<ShowListAction, ShowListState, ShowListViewEffect>() {

    override val initialState = initialState?: ShowListState.Idle

    private val reducer: Reducer<ShowListState, ShowListChange> = { oldState, change ->
        when (change) {
            is ShowListChange.Loading -> {
                when (oldState) {
                    is ShowListState.Idle -> ShowListState.Loading(results = null)
                    is ShowListState.Loading -> oldState.copy()
                    is ShowListState.Content -> {
                        ShowListState.Loading(results = oldState.results)
                    }
                    is ShowListState.Error -> ShowListState.Loading(results = null)
                }
            }
            is ShowListChange.Result -> {
                when (change.response) {
                    is LceResponse.Loading -> {
                        ShowListState.Loading(
                            results = change.response.data?.formattedForShowList(change.config, appStringProvider)
                        )
                    }
                    is LceResponse.Content -> {
                        ShowListState.Content(
                            results = change.response.data.formattedForShowList(change.config, appStringProvider)
                        )
                    }
                    is LceResponse.Error -> {
                        ShowListState.Error(
                            message = appStringProvider.generateErrorMessage(change.response),
                            canRetry = change.response is LceResponse.Error.NetworkError,
                            fallbackResults = change.response.fallbackData?.formattedForShowList(
                                change.config,
                                appStringProvider
                            )
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

        fun Observable<ShowListAction.Load>.toShowListChange(): Observable<ShowListChange> {
            return this.switchMap { action ->
                tvShowRepository.getTvShowsForList(action.showList.id)
                    .map<ShowListChange> { showsResponse ->
                        ShowListChange.Result(
                            response = showsResponse,
                            config = configRepository.getConfig()
                        )
                    }
                    .subscribeOn(schedulers.io)
                    .startWith(ShowListChange.Loading)
            }
        }

        fun Observable<LceResponse<List<TvShow>>>.toShowListChange(): Observable<ShowListChange> {
            return this.map<ShowListChange> { showResponse ->
                ShowListChange.Result(
                    response = showResponse,
                    config = configRepository.getConfig()
                )
            }
            .subscribeOn(schedulers.io)
            .startWith(ShowListChange.Loading)
        }

        val loadDataChange = actions.ofType<ShowListAction.Load>()
            .toShowListChange()

        val retryButtonChange = actions.ofType<ShowListAction.RetryButtonClicked>()
            .preventMultipleClicks()
            .map { action -> ShowListAction.Load(action.showList) }
            .toShowListChange()

        val watchlistButtonChange = actions.ofType<ShowListAction.ToggleWatchlistButtonClicked>()
            .preventMultipleClicks()
            .switchMap { action ->
                tvShowRepository.toggleTvShowWatchlistStatus(action.showId)
                    .doOnNext { response ->
                        if (response is LceResponse.Content) {
                            val show = response.data
                            if (!show.state.inWatchlist) {
                                postViewEffect {
                                    ShowListViewEffect.ShowRemovedFromListMessage(
                                        message = appStringProvider.generateContentRemovedFromListMessage(
                                            show.title,
                                            action.showList.name
                                        ),
                                        showId = show.id
                                    )
                                }
                            }
                        }
                    }
                    .flatMap { tvShowRepository.getTvShowsForList(action.showList.id) }
                    .toShowListChange()
            }

        val watchedButtonChange = actions.ofType<ShowListAction.ToggleWatchedButtonClicked>()
            .preventMultipleClicks()
            .switchMap { action ->
                tvShowRepository.toggleTvShowWatchedStatus(action.showId)
                    .flatMap { tvShowRepository.getTvShowsForList(action.showList.id) }
                    .toShowListChange()
            }

        val showClickedViewEffect = actions.ofType<ShowListAction.ShowClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { action -> ShowListViewEffect.ShowDetailScreen(action.showId) }

        val addToListViewEffect = actions.ofType<ShowListAction.AddToListButtonClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { action -> ShowListViewEffect.ShowAddToListMenu(action.showId) }

        val stateChanges = Observable.mergeArray(
            loadDataChange,
            retryButtonChange,
            watchlistButtonChange,
            watchedButtonChange
        )

        val viewEffectChanges = Observable.merge(showClickedViewEffect, addToListViewEffect)

        disposables += stateChanges
            .scan(initialState, reducer)
            .filter { it !is ShowListState.Idle }
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

sealed class ShowListAction : BaseAction {
    data class Load(val showList: ContentList) : ShowListAction()
    data class RetryButtonClicked(val showList: ContentList) : ShowListAction()
    data class ShowClicked(val showId: Long) : ShowListAction()
    data class ToggleWatchlistButtonClicked(val showList: ContentList, val showId: Long) : ShowListAction()
    data class ToggleWatchedButtonClicked(val showList: ContentList, val showId: Long) : ShowListAction()
    data class AddToListButtonClicked(val showId: Long) : ShowListAction()
    //todo NextEpisodeWatchedButtonClicked
}

sealed class ShowListChange {

    object Loading : ShowListChange()

    data class Result(
        val response: LceResponse<List<TvShow>>,
        val config: Config
    ) : ShowListChange()
}

sealed class ShowListState : BaseState, Parcelable {

    @Parcelize
    object Idle : ShowListState()

    @Parcelize
    data class Loading(val results: List<ShowListItem>?) : ShowListState()

    @Parcelize
    data class Content(val results: List<ShowListItem>) : ShowListState()

    @Parcelize
    data class Error(
        val message: String,
        val canRetry: Boolean,
        val fallbackResults: List<ShowListItem>?
    ) : ShowListState()
}

sealed class ShowListViewEffect : BaseViewEffect {
    data class ShowDetailScreen(val showId: Long) : ShowListViewEffect()
    data class ShowAddToListMenu(val showId: Long) : ShowListViewEffect()
    data class ShowRemovedFromListMessage(val message: String, val showId: Long) : ShowListViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class ShowListViewModelFactory(
    private val initialState: ShowListState?,
    private val showRepository: TvShowRepository,
    private val configRepository: ConfigRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ShowListViewModel(
            initialState,
            showRepository,
            configRepository,
            appStringProvider,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "ShowListViewModelFactory"
    }
}
