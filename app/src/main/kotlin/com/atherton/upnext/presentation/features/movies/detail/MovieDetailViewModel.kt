package com.atherton.upnext.presentation.features.MovieDetail.detail

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

class MovieDetailViewModel @Inject constructor(
    initialState: MovieDetailState?,
    private val schedulers: RxSchedulers
): UpNextViewModel<MovieDetailAction, MovieDetailState, MovieDetailViewEffect>() {

    override val initialState = initialState ?: MovieDetailState()

    init {
        bindActions()
    }

    private fun bindActions() {

    }
}

//================================================================================
// MVI
//================================================================================

sealed class MovieDetailAction : BaseAction

sealed class MovieDetailChange {
    object Loading : MovieDetailChange() //todo maybe remove
}

@Parcelize
data class MovieDetailState(val isIdle: Boolean = true): BaseState, Parcelable

sealed class MovieDetailViewEffect : BaseViewEffect

//================================================================================
// Factory
//================================================================================

@PerView
class MovieDetailViewModelFactory(
    private val initialState: MovieDetailState?,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T  = MovieDetailViewModel(initialState, schedulers) as T

    companion object {
        const val NAME = "MovieDetailViewModelFactory"
    }
}
