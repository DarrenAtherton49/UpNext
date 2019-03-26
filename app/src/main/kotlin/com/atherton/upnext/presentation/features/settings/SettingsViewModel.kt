package com.atherton.upnext.presentation.features.settings

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.util.base.BaseViewEffect
import com.atherton.upnext.util.base.UpNextViewModel
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.Reducer
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    initialState: SettingsState?,
    private val schedulers: RxSchedulers
): UpNextViewModel<SettingsAction, SettingsState, SettingsViewEffect>() {

    override val initialState = initialState ?: SettingsState.Idle

    private val reducer: Reducer<SettingsState, SettingsChange> = { oldState, change ->
        when (change) {
            is SettingsChange.Loading -> {
                SettingsState.Idle //todo remove
            }
            is SettingsChange.Result -> {
               SettingsState.Idle //todo remove
            }
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
//        disposables += viewEffectChanges
//            .observeOn(schedulers.main)
//            .subscribe(viewEffects::accept, Timber::e)
//
//        disposables += stateChanges
//            .scan(initialState, reducer)
//            .filter { it !is SettingsState.Idle }
//            .distinctUntilChanged()
//            .observeOn(schedulers.main)
//            .subscribe(state::setValue, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class SettingsAction : BaseAction

sealed class SettingsChange {
    object Loading : SettingsChange()
    data class Result(val response: LceResponse<List<String>>) : SettingsChange()
}

sealed class SettingsState: BaseState, Parcelable {

    @Parcelize
    object Idle : SettingsState()

    @Parcelize
    data class Loading(val settings: List<String>?) : SettingsState()

    @Parcelize
    data class Content(val settings: List<String>) : SettingsState()

    @Parcelize
    data class Error(val message: String, val canRetry: Boolean) : SettingsState()
}

sealed class SettingsViewEffect : BaseViewEffect

//================================================================================
// Factory
//================================================================================

@PerView
class SettingsViewModelFactory(
    private val initialState: SettingsState?,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel(initialState, schedulers) as T
    }

    companion object {
        const val NAME = "SettingsViewModelFactory"
    }
}
