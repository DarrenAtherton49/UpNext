package com.atherton.upnext.presentation.features.movies.detail

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.usecase.GetConfigUseCase
import com.atherton.upnext.domain.usecase.GetMovieDetailUseCase
import com.atherton.upnext.util.base.BaseViewEffect
import com.atherton.upnext.util.base.UpNextViewModel
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.zipWith
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    initialState: MovieDetailState?,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers
): UpNextViewModel<MovieDetailAction, MovieDetailState, MovieDetailViewEffect>() {

    override val initialState = initialState ?: MovieDetailState.Idle

    private val reducer: Reducer<MovieDetailState, MovieDetailChange> = { oldState, change ->
        when (change) {
            is MovieDetailChange.Loading -> {
                when (oldState) {
                    is MovieDetailState.Idle -> MovieDetailState.Loading(null)
                    is MovieDetailState.Loading -> oldState.copy()
                    is MovieDetailState.Content -> {
                        MovieDetailState.Loading(movie = oldState.movie)
                    }
                    is MovieDetailState.Error -> MovieDetailState.Loading(null)
                }
            }
            is MovieDetailChange.Result -> {
                when (change.response) {
                    is Response.Success -> {
                        MovieDetailState.Content(
                            movie = change.response.data.withMovieDetailImageUrls(change.config),
                            cached = change.response.cached
                        )
                    }
                    is Response.Failure -> MovieDetailState.Error(failure = change.response)
                }
            }
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        fun Observable<MovieDetailAction.Load>.toResultChange(): Observable<MovieDetailChange> {
            return this.switchMap { action ->
                getMovieDetailUseCase.build(action.id).zipWith(getConfigUseCase.build())
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map<MovieDetailChange> { MovieDetailChange.Result(it.first, it.second) }
                    .startWith(MovieDetailChange.Loading)
            }
        }

        val loadDataChange = actions.ofType<MovieDetailAction.Load>()
            .toResultChange()

        val retryButtonChange = actions.ofType<MovieDetailAction.RetryButtonClicked>()
            .map { MovieDetailAction.Load(it.id) }
            .toResultChange()

        val stateChanges = merge(loadDataChange, retryButtonChange)

        disposables += stateChanges
            .scan(initialState, reducer)
            .filter { it !is MovieDetailState.Idle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class MovieDetailAction : BaseAction {
    data class Load(val id: Int) : MovieDetailAction()
    data class RetryButtonClicked(val id: Int) : MovieDetailAction()
}

sealed class MovieDetailChange {
    object Loading : MovieDetailChange()
    data class Result(val response: Response<Movie>, val config: Config) : MovieDetailChange()
}

sealed class MovieDetailState : BaseState, Parcelable {

    @Parcelize
    object Idle : MovieDetailState()

    @Parcelize
    data class Loading(val movie: Movie?) : MovieDetailState()

    @Parcelize
    data class Content(val movie: Movie, val cached: Boolean = false) : MovieDetailState()

    @Parcelize
    data class Error(val failure: Response.Failure) : MovieDetailState()
}

sealed class MovieDetailViewEffect : BaseViewEffect

//================================================================================
// Screen-specific view mapper
//================================================================================

//todo write function to generate path based on device screen size?
private fun Movie.withMovieDetailImageUrls(config: Config): Movie {
    // only perform copy if the image paths actually exist
    return if (backdropPath != null || posterPath != null) {
        this.copy(
            backdropPath = backdropPath?.let { "${config.secureBaseUrl}${config.backdropSizes[1]}$backdropPath" },
            detail = detail?.copy(genres = detail.genres?.sortedBy { it.name }),
            posterPath = posterPath?.let { "${config.secureBaseUrl}${config.posterSizes[2]}$posterPath" }
        )
    } else this
}

//================================================================================
// Factory
//================================================================================

@PerView
class MovieDetailViewModelFactory(
    private val initialState: MovieDetailState?,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T  {
        return MovieDetailViewModel(initialState, getMovieDetailUseCase, getConfigUseCase, schedulers) as T
    }

    companion object {
        const val NAME = "MovieDetailViewModelFactory"
    }
}
