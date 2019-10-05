package com.atherton.upnext.presentation.features.shows.content

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.model.ContentList
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.TvShow
import com.atherton.upnext.domain.repository.ConfigRepository
import com.atherton.upnext.domain.repository.TvShowRepository
import com.atherton.upnext.presentation.base.BaseViewEffect
import com.atherton.upnext.presentation.base.UpNextViewModel
import com.atherton.upnext.presentation.util.AppStringProvider
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

class ShowListViewModel @Inject constructor(
    initialState: ShowListState?,
    private val tvShowRepository: TvShowRepository,
    private val configRepository: ConfigRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
): UpNextViewModel<ShowListAction, ShowListState, ShowListViewEffect>() {

    override val initialState = initialState?: ShowListState.Idle

    init {
        bindActions()
    }

    private fun bindActions() {


    }
}

//================================================================================
// MVI
//================================================================================

sealed class ShowListAction : BaseAction {
    data class Load(val showList: ContentList) : ShowListAction()
    data class RetryButtonClicked(val showList: ContentList) : ShowListAction()
    data class ShowClicked(val showId: Long) : ShowListAction()
    data class ToggleWatchlistButtonClicked(val showList: ContentList, val showId: Long) : ShowListAction()
    data class ToggleWatchedButtonClicked(val showList: ContentList, val showId: Long) : ShowListAction()
    data class AddToListButtonClicked(val showId: Long) : ShowListAction()
    //todo NextEpisodeWatchedButtonClicked
}

sealed class ShowListChange {

    object Loading : ShowListChange()

    data class Result(
        val response: LceResponse<List<TvShow>>,
        val config: Config
    ) : ShowListChange()
}

sealed class ShowListState : BaseState, Parcelable {

    @Parcelize
    object Idle : ShowListState()

    @Parcelize
    data class Loading(val results: List<ShowListItem>?) : ShowListState()

    @Parcelize
    data class Content(val results: List<ShowListItem>) : ShowListState()

    @Parcelize
    data class Error(
        val message: String,
        val canRetry: Boolean,
        val fallbackResults: List<ShowListItem>?
    ) : ShowListState()
}

sealed class ShowListViewEffect : BaseViewEffect {
    data class ShowDetailScreen(val showId: Long) : ShowListViewEffect()
    data class ShowAddToListMenu(val showId: Long) : ShowListViewEffect()
    data class ShowRemovedFromListMessage(val message: String, val showId: Long) : ShowListViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class ShowListViewModelFactory(
    private val initialState: ShowListState?,
    private val showRepository: TvShowRepository,
    private val configRepository: ConfigRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ShowListViewModel(
            initialState,
            showRepository,
            configRepository,
            appStringProvider,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "ShowListViewModelFactory"
    }
}
