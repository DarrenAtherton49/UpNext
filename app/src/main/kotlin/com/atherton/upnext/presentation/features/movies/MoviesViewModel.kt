package com.atherton.upnext.presentation.features.movies

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
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

class MoviesViewModel @Inject constructor(
    initialState: MoviesState?,
    private val schedulers: RxSchedulers
): UpNextViewModel<MoviesAction, MoviesState, MoviesViewEffect>() {

    override val initialState = initialState ?: MoviesState()

    init {
        bindActions()
    }

    private fun bindActions() {
        val addMovieClickedViewEffect = actions.ofType<MoviesAction.AddMovieButtonClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { MoviesViewEffect.ShowSearchScreen }

        disposables += addMovieClickedViewEffect
            .observeOn(schedulers.main)
            .subscribe(viewEffects::onNext, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class MoviesAction : BaseAction {
    object Load : MoviesAction() //todo maybe remove
    object AddMovieButtonClicked : MoviesAction()
}

sealed class MoviesChange {
    object Loading : MoviesChange() //todo maybe remove
}

@Parcelize
data class MoviesState(@Transient val isIdle: Boolean = true): BaseState, Parcelable

sealed class MoviesViewEffect : BaseViewEffect {
    object ShowSearchScreen : MoviesViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class MoviesViewModelFactory(
    private val initialState: MoviesState?,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T  = MoviesViewModel(initialState, schedulers) as T

    companion object {
        const val NAME = "MoviesViewModelFactory"
    }
}
