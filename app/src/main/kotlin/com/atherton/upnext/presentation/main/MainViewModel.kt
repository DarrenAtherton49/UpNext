package com.atherton.upnext.presentation.main

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.util.injection.PerView
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import io.reactivex.subjects.PublishSubject
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

class MainViewModel @Inject constructor(
    initialState: MainState?
): BaseViewModel<MainAction, MainState>() {

    override val initialState = initialState ?: MainState()

    val subject: PublishSubject<String> = PublishSubject.create()

    init {
        bindActions()
    }

    private fun bindActions() {

    }
}

//================================================================================
// MVI
//================================================================================

sealed class MainAction : BaseAction {
    object Load : MainAction() //todo maybe remove
}

sealed class MainChange {
    object Loading : MainChange() //todo maybe remove
}

@Parcelize
data class MainState(@Transient val isIdle: Boolean = true): BaseState, Parcelable

//================================================================================
// Factory
//================================================================================

@PerView
class MainViewModelFactory(
    private val initialState: MainState?
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(initialState) as T

    companion object {
        const val NAME = "MainViewModelFactory"
    }
}
