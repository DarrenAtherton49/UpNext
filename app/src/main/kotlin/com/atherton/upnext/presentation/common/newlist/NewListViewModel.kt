package com.atherton.upnext.presentation.common.newlist

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.repository.MovieRepository
import com.atherton.upnext.domain.repository.TvShowRepository
import com.atherton.upnext.presentation.base.BaseViewEffect
import com.atherton.upnext.presentation.base.UpNextViewModel
import com.atherton.upnext.presentation.common.ContentType
import com.atherton.upnext.presentation.util.AppStringProvider
import com.atherton.upnext.presentation.util.extension.preventMultipleClicks
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.Reducer
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

class NewListViewModel @Inject constructor(
    initialState: NewListState?,
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : UpNextViewModel<NewListAction, NewListState, NewListViewEffect>() {

    override val initialState = initialState ?: NewListState.Idle

    private val reducer: Reducer<NewListState, NewListChange> = { _, change ->
        when (change) {
            is NewListChange.Loading -> NewListState.Loading
            is NewListChange.Result -> {
                when (change.response) {
                    is LceResponse.Loading -> NewListState.Loading
                    is LceResponse.Content -> NewListState.Content(listId = change.response.data)
                    is LceResponse.Error -> NewListState.Error
                }
            }
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {

        val cancelClickedViewEffect = actions.ofType<NewListAction.CancelClicked>()
            .preventMultipleClicks()
            .map { NewListViewEffect.CloseScreen }

        val createClickedChange = actions.ofType<NewListAction.CreateClicked>()
            .preventMultipleClicks()
            .switchMap { action ->
                val createListObservable = when (action.contentType) {
                    is ContentType.TvShow -> {
                        tvShowRepository.createTvShowList(action.contentId, action.listTitle)
                    }
                    is ContentType.Movie -> {
                        movieRepository.createMovieList(action.contentId, action.listTitle)
                    }
                }
                createListObservable
                    .subscribeOn(schedulers.io)
                    .map<NewListChange> { response -> NewListChange.Result(response, action.listTitle) }
                    .startWith(NewListChange.Loading)
            }
            .doOnNext { change ->
                if (change is NewListChange.Result &&
                    change.response is LceResponse.Content &&
                    change.response.data != 0L) {
                    postViewEffect {
                        NewListViewEffect.ShowSuccessState(
                            message = appStringProvider.generateListCreatedMessage(change.listTitle),
                            listId = change.response.data
                        )
                    }
                }
            }

        disposables += createClickedChange
            .scan(initialState, reducer)
            .filter { it !is NewListState.Idle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)

        disposables += cancelClickedViewEffect
            .observeOn(schedulers.main)
            .subscribe(viewEffects::accept, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class NewListAction : BaseAction {

    data class CreateClicked(
        val contentId: Long?,
        val contentType: ContentType,
        val listTitle: String
    ) : NewListAction()

    object CancelClicked : NewListAction()
}

sealed class NewListChange {
    object Loading : NewListChange()
    data class Result(val response: LceResponse<Long>, val listTitle: String) : NewListChange()
}

sealed class NewListState : BaseState, Parcelable {

    @Parcelize
    object Idle : NewListState()

    @Parcelize
    object Loading : NewListState()

    @Parcelize
    data class Content(val listId: Long) : NewListState()

    @Parcelize
    object Error : NewListState()
}

sealed class NewListViewEffect : BaseViewEffect {
    data class ShowSuccessState(val message: String, val listId: Long) : NewListViewEffect()
    object CloseScreen : NewListViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class NewListViewModelFactory(
    private val initialState: NewListState?,
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewListViewModel(
            initialState,
            movieRepository,
            tvShowRepository,
            appStringProvider,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "NewListViewModelFactory"
    }
}
