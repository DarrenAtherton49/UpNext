package com.atherton.upnext.presentation.features.discover.featured

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.util.injection.PerView
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

class DiscoverViewModel @Inject constructor(
    initialState: DiscoverState?
): BaseViewModel<DiscoverAction, DiscoverState>() {

    override val initialState = initialState ?: DiscoverState()

    init {
        bindActions()
    }

    private fun bindActions() {

    }
}

//================================================================================
// MVI
//================================================================================

sealed class DiscoverAction : BaseAction {
    object Load : DiscoverAction() //todo maybe remove
}

sealed class DiscoverChange {
    object Loading : DiscoverChange() //todo maybe remove
}

@Parcelize
data class DiscoverState(@Transient val isIdle: Boolean = true): BaseState, Parcelable


//================================================================================
// Factory
//================================================================================

@PerView
class DiscoverViewModelFactory(
    private val initialState: DiscoverState?
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T  = DiscoverViewModel(initialState) as T

    companion object {
        const val NAME = "DiscoverViewModelFactory"
    }
}
