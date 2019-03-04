package com.atherton.upnext.presentation.main

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.SearchModelViewMode
import com.atherton.upnext.util.base.BaseViewEffect
import com.atherton.upnext.util.base.UpNextViewModel
import com.atherton.upnext.util.extensions.preventMultipleClicks
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import io.reactivex.Observable.mergeArray
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    initialState: MainState?,
    private val schedulers: RxSchedulers
): UpNextViewModel<MainAction, MainState, MainViewEffect>() {

    override val initialState = initialState ?: MainState()

    init {
        bindActions()
    }

    private fun bindActions() {

        // used to notify all screens in the application that the view mode has changed and data should be reloaded
        val viewModeToggleChangedViewEffect = actions.ofType<MainAction.ViewModeToggleChanged>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { MainViewEffect.ToggleViewMode(it.viewMode) }

        val searchActionClickedViewEffect = actions.ofType<MainAction.SearchActionClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { MainViewEffect.Navigation.ShowSearchScreen }

        val addTvShowClickedViewEffect = actions.ofType<MainAction.AddShowButtonClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { MainViewEffect.Navigation.ShowSearchScreen }

        val addMovieClickedViewEffect = actions.ofType<MainAction.AddMovieButtonClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { MainViewEffect.Navigation.ShowSearchScreen }

        val tvShowClickedViewEffect = actions.ofType<MainAction.TvShowClicked>()
            .subscribeOn(schedulers.io)
            .map { action -> MainViewEffect.Navigation.ShowTvDetailScreen(action.tvShowId) }

        val movieClickedViewEffect = actions.ofType<MainAction.MovieClicked>()
            .subscribeOn(schedulers.io)
            .map { action -> MainViewEffect.Navigation.ShowMovieDetailScreen(action.movieId) }

        val personClickedViewEffect = actions.ofType<MainAction.PersonClicked>()
            .subscribeOn(schedulers.io)
            .map { action -> MainViewEffect.Navigation.ShowPersonDetailScreen(action.personId) }

        val youtubeVideoClickedViewEffect = actions.ofType<MainAction.YouTubeVideoClicked>()
            .subscribeOn(schedulers.io)
            .map { action -> MainViewEffect.Navigation.PlayYoutubeVideo(action.videoKey) }

        val viewEffectChanges = mergeArray(
            searchActionClickedViewEffect,
            viewModeToggleChangedViewEffect,
            addTvShowClickedViewEffect,
            addMovieClickedViewEffect,
            tvShowClickedViewEffect,
            movieClickedViewEffect,
            personClickedViewEffect,
            youtubeVideoClickedViewEffect
        )

        disposables += viewEffectChanges
            .observeOn(schedulers.main)
            .subscribe(viewEffects::accept, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class MainAction : BaseAction {
    object SearchActionClicked : MainAction()
    data class ViewModeToggleChanged(val viewMode: SearchModelViewMode) : MainAction()
    object AddShowButtonClicked : MainAction()
    object AddMovieButtonClicked : MainAction()
    data class TvShowClicked(val tvShowId: Int) : MainAction()
    data class MovieClicked(val movieId: Int) : MainAction()
    data class PersonClicked(val personId: Int) : MainAction()
    data class YouTubeVideoClicked(val videoKey: String) : MainAction()
}

sealed class MainChange {
    object Loading : MainChange() //todo maybe remove
}

@Parcelize
data class MainState(val isIdle: Boolean = true): BaseState, Parcelable

sealed class MainViewEffect : BaseViewEffect {
    sealed class Navigation : MainViewEffect() {
        object ShowSearchScreen : Navigation()
        data class ShowMovieDetailScreen(val movieId: Int): Navigation()
        data class ShowTvDetailScreen(val tvShowId: Int): Navigation()
        data class ShowPersonDetailScreen(val personId: Int): Navigation()
        data class PlayYoutubeVideo(val videoKey: String) : Navigation()
    }
    data class ToggleViewMode(val viewMode: SearchModelViewMode) : MainViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class MainViewModelFactory(
    private val initialState: MainState?,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(
        initialState,
        schedulers) as T

    companion object {
        const val NAME = "MainViewModelFactory"
    }
}
