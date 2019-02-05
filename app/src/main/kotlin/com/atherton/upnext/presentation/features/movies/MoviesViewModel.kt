package com.atherton.upnext.presentation.features.movies

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.util.injection.PerView
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

class MoviesViewModel @Inject constructor(
    initialState: MoviesState?
): BaseViewModel<MoviesAction, MoviesState>() {

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

sealed class MoviesAction : BaseAction {
    object Load : MoviesAction() //todo maybe remove
}

sealed class MoviesChange {
    object Loading : MoviesChange() //todo maybe remove
}

@Parcelize
data class MoviesState(@Transient val isIdle: Boolean = true): BaseState, Parcelable


//================================================================================
// Factory
//================================================================================

@PerView
class MoviesViewModelFactory(
    private val initialState: MoviesState?
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T  = MoviesViewModel(initialState) as T

    companion object {
        const val NAME = "MoviesViewModelFactory"
    }
}
