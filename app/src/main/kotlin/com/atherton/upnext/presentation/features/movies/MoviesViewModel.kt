package com.atherton.upnext.presentation.features.movies

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.ContentList
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.repository.MovieRepository
import com.atherton.upnext.presentation.base.BaseViewEffect
import com.atherton.upnext.presentation.base.UpNextViewModel
import com.atherton.upnext.presentation.util.AppStringProvider
import com.atherton.upnext.presentation.util.extension.preventMultipleClicks
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

class MoviesViewModel @Inject constructor(
    initialState: MoviesState?,
    private val movieRepository: MovieRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
): UpNextViewModel<MoviesAction, MoviesState, MoviesViewEffect>() {

    override val initialState = initialState ?: MoviesState.Idle

    private val reducer: Reducer<MoviesState, MoviesChange> = { oldState, change ->
        when (change) {
            is MoviesChange.Loading -> {
                when (oldState) {
                    is MoviesState.Idle -> MoviesState.Loading(results = null)
                    is MoviesState.Loading -> oldState.copy()
                    is MoviesState.Content -> {
                        MoviesState.Loading(results = oldState.results)
                    }
                    is MoviesState.Error -> MoviesState.Loading(results = null)
                }
            }
            is MoviesChange.Result -> {
                when (change.response) {
                    is LceResponse.Loading -> MoviesState.Loading(results = change.response.data)
                    is LceResponse.Content -> MoviesState.Content(results = change.response.data)
                    is LceResponse.Error -> {
                        MoviesState.Error(
                            message = appStringProvider.generateErrorMessage(change.response),
                            canRetry = change.response is LceResponse.Error.NetworkError
                        )
                    }
                }
            }
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {

        val loadDataChange = actions.ofType<MoviesAction.Load>()
            .distinctUntilChanged()
            .switchMap {
                movieRepository.getMovieLists()
                    .subscribeOn(schedulers.io)
                    .map<MoviesChange> { movieListsResponse -> MoviesChange.Result(movieListsResponse) }
                    .startWith(MoviesChange.Loading)
            }

        val searchActionClickedViewEffect = actions.ofType<MoviesAction.SearchActionClicked>()
            .preventMultipleClicks()
            .map { MoviesViewEffect.ShowSearchScreen }

        val settingsActionClickedViewEffect = actions.ofType<MoviesAction.SettingsActionClicked>()
            .preventMultipleClicks()
            .map { MoviesViewEffect.ShowSettingsScreen }

        val viewEffectChanges = Observable.merge(searchActionClickedViewEffect, settingsActionClickedViewEffect)

        disposables += loadDataChange
            .scan(initialState, reducer)
            .filter { it !is MoviesState.Idle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)

        disposables += viewEffectChanges
            .observeOn(schedulers.main)
            .subscribe(viewEffects::accept, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class MoviesAction : BaseAction {
    object Load : MoviesAction()
    object SearchActionClicked : MoviesAction()
    object SettingsActionClicked : MoviesAction()
}

sealed class MoviesChange {
    object Loading : MoviesChange()
    data class Result(val response: LceResponse<List<ContentList>>) : MoviesChange()
}

sealed class MoviesState: BaseState, Parcelable {

    @Parcelize
    object Idle : MoviesState()

    @Parcelize
    data class Loading(val results: List<ContentList>?) : MoviesState()

    @Parcelize
    data class Content(val results: List<ContentList>) : MoviesState()

    @Parcelize
    data class Error(val message: String, val canRetry: Boolean) : MoviesState()
}

sealed class MoviesViewEffect : BaseViewEffect {
    object ShowSearchScreen : MoviesViewEffect()
    object ShowSettingsScreen : MoviesViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class MoviesViewModelFactory(
    private val initialState: MoviesState?,
    private val movieRepository: MovieRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MoviesViewModel(
            initialState,
            movieRepository,
            appStringProvider,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "MoviesViewModelFactory"
    }
}
