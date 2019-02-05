package com.atherton.upnext.presentation.features.shows

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.util.injection.PerView
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

class ShowsViewModel @Inject constructor(
    initialState: ShowsState?
): BaseViewModel<ShowsAction, ShowsState>() {

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

sealed class ShowsAction : BaseAction {
    object Load : ShowsAction() //todo maybe remove
}

sealed class ShowsChange {
    object Loading : ShowsChange() //todo maybe remove
}

@Parcelize
data class ShowsState(@Transient val isIdle: Boolean = true): BaseState, Parcelable


//================================================================================
// Factory
//================================================================================

@PerView
class ShowsViewModelFactory(
    private val initialState: ShowsState?
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T  = ShowsViewModel(initialState) as T

    companion object {
        const val NAME = "ShowsViewModelFactory"
    }
}
