package com.atherton.upnext.presentation.main

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.util.base.BaseViewEffect
import com.atherton.upnext.util.base.UpNextViewModel
import com.atherton.upnext.util.extensions.preventMultipleClicks
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import io.reactivex.Observable.merge
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
        val searchActionClickedViewEffect = actions.ofType<MainAction.SearchActionClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { MainViewEffect.ShowSearchScreen }

        val addTvShowClickedViewEffect = actions.ofType<MainAction.AddShowButtonClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { MainViewEffect.ShowSearchScreen }

        val addMovieClickedViewEffect = actions.ofType<MainAction.AddMovieButtonClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { MainViewEffect.ShowSearchScreen }

        val viewEffectChanges = merge(
            searchActionClickedViewEffect,
            addTvShowClickedViewEffect,
            addMovieClickedViewEffect
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
    object AddShowButtonClicked : MainAction()
    object AddMovieButtonClicked : MainAction()
}

sealed class MainChange {
    object Loading : MainChange() //todo maybe remove
}

@Parcelize
data class MainState(val isIdle: Boolean = true): BaseState, Parcelable

sealed class MainViewEffect : BaseViewEffect {
    object ShowSearchScreen : MainViewEffect()
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
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(initialState, schedulers) as T

    companion object {
        const val NAME = "MainViewModelFactory"
    }
}
