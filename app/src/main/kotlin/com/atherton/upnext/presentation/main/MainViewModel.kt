package com.atherton.upnext.presentation.main

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.GridViewMode
import com.atherton.upnext.presentation.features.settings.licenses.License
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
) : UpNextViewModel<MainAction, MainState, MainViewEffect>() {

    override val initialState = initialState ?: MainState()

    init {
        bindActions()
    }

    private fun bindActions() {

        // used to notify all screens in the application that the view mode has changed and data should be reloaded
        val viewModeToggleChangedViewEffect = actions.ofType<MainAction.ViewModeToggled>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { MainViewEffect.ToggleViewMode(it.viewMode) }

        val searchActionClickedViewEffect = actions.ofType<MainAction.SearchActionClicked>()
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

        val settingsActionClickedViewEffect = actions.ofType<MainAction.SettingsActionClicked>()
            .subscribeOn(schedulers.io)
            .map { MainViewEffect.Navigation.ShowSettingsScreen }

        val openSourceLicensesClickedViewEffect = actions.ofType<MainAction.SettingsAction.OpenSourceLicensesClicked>()
            .subscribeOn(schedulers.io)
            .preventMultipleClicks()
            .map { MainViewEffect.Navigation.Settings.ShowLicensesScreen }

        val licenseClickedViewEffect = actions.ofType<MainAction.LicenseClicked>()
            .subscribeOn(schedulers.io)
            .preventMultipleClicks()
            .map { action ->
                MainViewEffect.Navigation.ShowLicenseInBrowser(action.license.url)
            }

        val viewEffectChanges = mergeArray(
            searchActionClickedViewEffect,
            viewModeToggleChangedViewEffect,
            tvShowClickedViewEffect,
            movieClickedViewEffect,
            personClickedViewEffect,
            youtubeVideoClickedViewEffect,
            settingsActionClickedViewEffect,
            openSourceLicensesClickedViewEffect,
            licenseClickedViewEffect
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
    data class ViewModeToggled(val viewMode: GridViewMode) : MainAction()
    data class TvShowClicked(val tvShowId: Long) : MainAction()
    data class MovieClicked(val movieId: Long) : MainAction()
    data class PersonClicked(val personId: Long) : MainAction()
    data class YouTubeVideoClicked(val videoKey: String) : MainAction()
    object SettingsActionClicked : MainAction()
    sealed class SettingsAction : MainAction() {
        object OpenSourceLicensesClicked : SettingsAction()
    }
    data class LicenseClicked(val license: License) : MainAction()
}

sealed class MainChange {
    object Loading : MainChange() //todo maybe remove
}

@Parcelize
data class MainState(val isIdle: Boolean = true): BaseState, Parcelable

sealed class MainViewEffect : BaseViewEffect {
    sealed class Navigation : MainViewEffect() {
        object ShowSearchScreen : Navigation()
        data class ShowMovieDetailScreen(val movieId: Long): Navigation()
        data class ShowTvDetailScreen(val tvShowId: Long): Navigation()
        data class ShowPersonDetailScreen(val personId: Long): Navigation()
        data class PlayYoutubeVideo(val videoKey: String) : Navigation()
        object ShowSettingsScreen : Navigation()
        sealed class Settings : Navigation() {
            object ShowLicensesScreen : Settings()
        }
        data class ShowLicenseInBrowser(val url: String) : Navigation()
    }
    data class ToggleViewMode(val viewMode: GridViewMode) : MainViewEffect()
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
