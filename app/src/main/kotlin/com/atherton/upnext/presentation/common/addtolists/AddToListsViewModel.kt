package com.atherton.upnext.presentation.common.addtolists

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.ContentList
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Watchable
import com.atherton.upnext.domain.repository.MovieRepository
import com.atherton.upnext.domain.repository.TvShowRepository
import com.atherton.upnext.presentation.util.AppStringProvider
import com.atherton.upnext.util.base.BaseViewEffect
import com.atherton.upnext.util.base.UpNextViewModel
import com.atherton.upnext.util.extensions.preventMultipleClicks
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

class AddToListsViewModel @Inject constructor(
    initialState: AddToListsState?,
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : UpNextViewModel<AddToListsAction, AddToListsState, AddToListsViewEffect>() {

    override val initialState = initialState ?: AddToListsState.Idle

    private val reducer: Reducer<AddToListsState, AddToListsChange> = { oldState, change ->
        when (change) {
            is AddToListsChange.Loading -> {
                when (oldState) {
                    is AddToListsState.Idle -> AddToListsState.Loading(results = null)
                    is AddToListsState.Loading -> oldState.copy()
                    is AddToListsState.Content -> AddToListsState.Loading(results = oldState.results)
                    is AddToListsState.Error -> AddToListsState.Loading(results = null)
                }
            }
            is AddToListsChange.Result -> {
                when (change.response) {
                    is LceResponse.Loading -> {
                        AddToListsState.Loading(results = change.response.data)
                    }
                    is LceResponse.Content -> {
                        AddToListsState.Content(results = change.response.data)
                    }
                    is LceResponse.Error -> AddToListsState.Error(
                        message = appStringProvider.generateErrorMessage(change.response),
                        canRetry = change.response is LceResponse.Error.NetworkError,
                        fallbackResults = change.response.fallbackData
                    )
                }
            }
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {

        fun getContentListsObservable(contentType: AddToListsContentType): Observable<AddToListsChange> {
            val listsObservable = when (contentType) {
                is AddToListsContentType.TvShow -> tvShowRepository.getTvShowLists()
                is AddToListsContentType.Movie -> movieRepository.getMovieLists()
            }
            return listsObservable
                .map<AddToListsChange> { listsResponse -> AddToListsChange.Result(listsResponse) }
                .subscribeOn(schedulers.io)
                .startWith(AddToListsChange.Loading)
        }

        val loadDataChange = actions.ofType<AddToListsAction.Load>()
            .switchMap { action -> getContentListsObservable(action.contentType) }

        val toggleContentListStatusChange = actions.ofType<AddToListsAction.ToggleContentListStatus>()
            .preventMultipleClicks()
            .switchMap { action ->
                when (action.contentType) {
                    is AddToListsContentType.TvShow -> {
                        tvShowRepository.toggleTvShowListStatus(
                            tvShowId = action.contentId,
                            listId = action.listId)
                            .map<LceResponse<Watchable>> { it }
                            .subscribeOn(schedulers.io)
                    }
                    is AddToListsContentType.Movie -> {
                        movieRepository.toggleMovieListStatus(
                            movieId = action.contentId,
                            listId = action.listId)
                            .map<LceResponse<Watchable>> { it }
                            .subscribeOn(schedulers.io)
                    }
                }.flatMap {
                    getContentListsObservable(action.contentType)
                }
            }

        val stateChanges = merge(
            loadDataChange,
            toggleContentListStatusChange
        )

        disposables += stateChanges
            .scan(initialState, reducer)
            .filter { it !is AddToListsState.Idle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)
    }
}

//================================================================================
// Screen-specific view data/functions
//================================================================================

sealed class AddToListsContentType : Parcelable {
    @Parcelize object TvShow : AddToListsContentType()
    @Parcelize object Movie : AddToListsContentType()
}

//================================================================================
// MVI
//================================================================================

sealed class AddToListsAction : BaseAction {

    data class Load(val contentType: AddToListsContentType) : AddToListsAction()

    data class ToggleContentListStatus(
        val contentId: Long,
        val contentType: AddToListsContentType,
        val listId: Long
    ) : AddToListsAction()
}

sealed class AddToListsChange {
    object Loading : AddToListsChange()
    data class Result(val response: LceResponse<List<ContentList>>) : AddToListsChange()
}

sealed class AddToListsState : BaseState, Parcelable {

    @Parcelize
    object Idle : AddToListsState()

    @Parcelize
    data class Loading(val results: List<ContentList>?) : AddToListsState()

    @Parcelize
    data class Content(val results: List<ContentList>) : AddToListsState()

    @Parcelize
    data class Error(
        val message: String,
        val canRetry: Boolean,
        val fallbackResults: List<ContentList>?
    ) : AddToListsState()
}

sealed class AddToListsViewEffect : BaseViewEffect

//================================================================================
// Factory
//================================================================================

@PerView
class AddToListsViewModelFactory(
    private val initialState: AddToListsState?,
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddToListsViewModel(
            initialState,
            movieRepository,
            tvShowRepository,
            appStringProvider,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "AddToListsViewModelFactory"
    }
}
