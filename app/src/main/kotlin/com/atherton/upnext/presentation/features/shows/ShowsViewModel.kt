package com.atherton.upnext.presentation.features.shows

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

class ShowsViewModel @Inject constructor(
    initialState: ShowsState?,
    private val schedulers: RxSchedulers
) : UpNextViewModel<ShowsAction, ShowsState, ShowsViewEffect>() {

    override val initialState = initialState ?: ShowsState()

    init {
        bindActions()
    }

    private fun bindActions() {

    }
}

//================================================================================
// MVI
//================================================================================

sealed class ShowsAction : BaseAction

sealed class ShowsChange {
    object Loading : ShowsChange() //todo maybe remove
}

@Parcelize
data class ShowsState(val isIdle: Boolean = true): BaseState, Parcelable

sealed class ShowsViewEffect : BaseViewEffect

//================================================================================
// Factory
//================================================================================

@PerView
class ShowsViewModelFactory(
    private val initialState: ShowsState?,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T  = ShowsViewModel(initialState, schedulers) as T

    companion object {
        const val NAME = "ShowsViewModelFactory"
    }
}
