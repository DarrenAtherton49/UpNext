package com.atherton.upnext.presentation.features.movies

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.util.base.BaseViewEffect
import com.atherton.upnext.util.base.UpNextViewModel
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import kotlinx.android.parcel.Parcelize
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

    }
}

//================================================================================
// MVI
//================================================================================

sealed class MoviesAction : BaseAction

sealed class MoviesChange {
    object Loading : MoviesChange() //todo maybe remove
}

@Parcelize
data class MoviesState(val isIdle: Boolean = true): BaseState, Parcelable

sealed class MoviesViewEffect : BaseViewEffect

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
