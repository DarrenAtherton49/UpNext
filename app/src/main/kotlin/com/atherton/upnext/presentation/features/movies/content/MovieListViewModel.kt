package com.atherton.upnext.presentation.features.movies.content

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.model.ContentList
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.repository.ConfigRepository
import com.atherton.upnext.domain.repository.MovieRepository
import com.atherton.upnext.presentation.base.BaseViewEffect
import com.atherton.upnext.presentation.base.UpNextViewModel
import com.atherton.upnext.presentation.common.searchmodel.formattedForMovieList
import com.atherton.upnext.presentation.util.AppStringProvider
import com.atherton.upnext.presentation.util.extension.preventMultipleClicks
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.Observable.mergeArray
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

class MovieListViewModel @Inject constructor(
    initialState: MovieListState?,
    private val movieRepository: MovieRepository,
    private val configRepository: ConfigRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
): UpNextViewModel<MovieListAction, MovieListState, MovieListViewEffect>() {

    override val initialState = initialState ?: MovieListState.Idle

    private val reducer: Reducer<MovieListState, MovieListChange> = { oldState, change ->
        when (change) {
            is MovieListChange.Loading -> {
                when (oldState) {
                    is MovieListState.Idle -> MovieListState.Loading(results = null)
                    is MovieListState.Loading -> oldState.copy()
                    is MovieListState.Content -> {
                        MovieListState.Loading(results = oldState.results)
                    }
                    is MovieListState.Error -> MovieListState.Loading(results = null)
                }
            }
            is MovieListChange.Result -> {
                when (change.response) {
                    is LceResponse.Loading -> {
                        MovieListState.Loading(
                            results = change.response.data?.formattedForMovieList(change.config, appStringProvider)
                        )
                    }
                    is LceResponse.Content -> {
                        MovieListState.Content(
                            results = change.response.data.formattedForMovieList(change.config, appStringProvider)
                        )
                    }
                    is LceResponse.Error -> {
                        MovieListState.Error(
                            message = appStringProvider.generateErrorMessage(change.response),
                            canRetry = change.response is LceResponse.Error.NetworkError,
                            fallbackResults = change.response.fallbackData?.formattedForMovieList(
                                change.config,
                                appStringProvider
                            )
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

        fun Observable<MovieListAction.Load>.toMovieListChange(): Observable<MovieListChange> {
            return this.switchMap { action ->
                movieRepository.getMoviesForList(action.movieList.id)
                    .map<MovieListChange> { moviesResponse ->
                        MovieListChange.Result(
                            response = moviesResponse,
                            config = configRepository.getConfig())
                    }
                    .subscribeOn(schedulers.io)
                    .startWith(MovieListChange.Loading)
            }
        }

        fun Observable<LceResponse<List<Movie>>>.toMovieListChange(): Observable<MovieListChange> {
            return this.map<MovieListChange> { moviesResponse ->
                    MovieListChange.Result(
                        response = moviesResponse,
                        config = configRepository.getConfig()
                    )
                }
                .subscribeOn(schedulers.io)
                .startWith(MovieListChange.Loading)
        }

        val loadDataChange = actions.ofType<MovieListAction.Load>()
            .toMovieListChange()

        val retryButtonChange = actions.ofType<MovieListAction.RetryButtonClicked>()
            .preventMultipleClicks()
            .map { action -> MovieListAction.Load(action.movieList) }
            .toMovieListChange()

        val watchlistButtonChange = actions.ofType<MovieListAction.ToggleWatchlistButtonClicked>()
            .preventMultipleClicks()
            .switchMap { action ->
                movieRepository.toggleMovieWatchlistStatus(action.movieId)
                    .doOnNext { response ->
                        if (response is LceResponse.Content) {
                            val movie = response.data
                            if (!movie.state.inWatchlist) {
                                postViewEffect {
                                    MovieListViewEffect.ShowRemovedFromListMessage(movie, action.movieList)
                                }
                            }
                        }
                    }
                    .flatMap { movieRepository.getMoviesForList(action.movieList.id) }
                    .toMovieListChange()
            }

        val watchedButtonChange = actions.ofType<MovieListAction.ToggleWatchedButtonClicked>()
            .preventMultipleClicks()
            .switchMap { action ->
                movieRepository.toggleMovieWatchedStatus(action.movieId)
                    .flatMap { movieRepository.getMoviesForList(action.movieList.id) }
                    .toMovieListChange()
            }

        val movieClickedViewEffect = actions.ofType<MovieListAction.MovieClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { action -> MovieListViewEffect.ShowMovieDetailScreen(action.movieId) }

        val addToListViewEffect = actions.ofType<MovieListAction.AddToListButtonClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { action -> MovieListViewEffect.ShowAddToListMenu(action.movieId) }

        val stateChanges = mergeArray(
            loadDataChange,
            retryButtonChange,
            watchlistButtonChange,
            watchedButtonChange
        )

        val viewEffectChanges = merge(movieClickedViewEffect, addToListViewEffect)

        disposables += stateChanges
            .scan(initialState, reducer)
            .filter { it !is MovieListState.Idle }
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

sealed class MovieListAction : BaseAction {
    data class Load(val movieList: ContentList) : MovieListAction()
    data class RetryButtonClicked(val movieList: ContentList) : MovieListAction()
    data class MovieClicked(val movieId: Long) : MovieListAction()
    data class ToggleWatchlistButtonClicked(val movieList: ContentList, val movieId: Long) : MovieListAction()
    data class ToggleWatchedButtonClicked(val movieList: ContentList, val movieId: Long) : MovieListAction()
    data class AddToListButtonClicked(val movieId: Long) : MovieListAction()
}

sealed class MovieListChange {

    object Loading : MovieListChange()

    data class Result(
        val response: LceResponse<List<Movie>>,
        val config: Config
    ) : MovieListChange()
}

sealed class MovieListState : BaseState, Parcelable {

    @Parcelize
    object Idle : MovieListState()

    @Parcelize
    data class Loading(val results: List<MovieListItem>?) : MovieListState()

    @Parcelize
    data class Content(val results: List<MovieListItem>) : MovieListState()

    @Parcelize
    data class Error(
        val message: String,
        val canRetry: Boolean,
        val fallbackResults: List<MovieListItem>?
    ) : MovieListState()
}

sealed class MovieListViewEffect : BaseViewEffect {
    data class ShowMovieDetailScreen(val movieId: Long) : MovieListViewEffect()
    data class ShowAddToListMenu(val movieId: Long) : MovieListViewEffect()
    data class ShowRemovedFromListMessage(val movie: Movie, val movieList: ContentList) : MovieListViewEffect()
    data class ShowAddedToListMessage(val movie: Movie, val movieList: ContentList) : MovieListViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class MovieListViewModelFactory(
    private val initialState: MovieListState?,
    private val movieRepository: MovieRepository,
    private val configRepository: ConfigRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieListViewModel(
            initialState,
            movieRepository,
            configRepository,
            appStringProvider,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "MovieListViewModelFactory"
    }
}
