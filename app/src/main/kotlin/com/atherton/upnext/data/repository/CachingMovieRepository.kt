package com.atherton.upnext.data.repository

import com.atherton.upnext.data.db.dao.ListDao
import com.atherton.upnext.data.db.dao.ListDao.Companion.LIST_ID_MOVIE_WATCHED
import com.atherton.upnext.data.db.dao.ListDao.Companion.LIST_ID_MOVIE_WATCHLIST
import com.atherton.upnext.data.db.dao.MovieDao
import com.atherton.upnext.data.db.dao.MovieDao.Companion.PLAYLIST_NOW_PLAYING
import com.atherton.upnext.data.db.dao.MovieDao.Companion.PLAYLIST_POPULAR
import com.atherton.upnext.data.db.dao.MovieDao.Companion.PLAYLIST_TOP_RATED
import com.atherton.upnext.data.db.dao.MovieDao.Companion.PLAYLIST_UPCOMING
import com.atherton.upnext.data.db.model.list.RoomMovieList
import com.atherton.upnext.data.db.model.movie.RoomMovie
import com.atherton.upnext.data.db.model.movie.RoomMovieAllData
import com.atherton.upnext.data.mapper.*
import com.atherton.upnext.data.network.model.NetworkResponse
import com.atherton.upnext.data.network.model.TmdbMovie
import com.atherton.upnext.data.network.service.TmdbMovieService
import com.atherton.upnext.domain.model.ContentList
import com.atherton.upnext.domain.model.ContentListStatus
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.repository.MovieRepository
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingMovieRepository @Inject constructor(
    private val movieDao: MovieDao,
    private val listDao: ListDao,
    private val movieService: TmdbMovieService
) : MovieRepository {

    //todo modify this to check if movie is in database and if it is still valid (time based?)
    //todo modify this call to add a 'forceRefresh' parameter (e.g. in case of pull-to-refresh)
    override fun getMovie(id: Long): Observable<LceResponse<Movie>> {
        return movieDao.getMovieForIdObservable(id)
            .distinctUntilChanged()
            .flatMap { movieList ->
                if (movieList.isNotEmpty() && movieList[0].isModelComplete) { // movie is cached and has all data
                    val movie: Movie? = getFullMovieFromDatabase(id) // fetch the full movie and all relations
                    Observable.fromCallable {
                        if (movie != null) {
                            LceResponse.Content(data = movie)
                        } else {
                            throw IllegalStateException("Movie should be in database - check query")
                        }
                    }
                } else {
                    movieService.getMovieDetails(id)
                        .toObservable()
                        .doOnNext { networkResponse ->
                            if (networkResponse is NetworkResponse.Success) {
                                val networkMovie: TmdbMovie = networkResponse.body
                                saveFullMovieToDatabase(networkMovie)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getFullMovieFromDatabase(id))
                        }
                }
            }
    }

    //todo modify this to check if movies are are still valid (time based?)
    override fun getPopular(): Observable<LceResponse<List<Movie>>> {
        return movieDao.getMoviesForPlaylistSingle(PLAYLIST_POPULAR)
            .toObservable()
            .flatMap { movieList ->
                if (movieList.isNotEmpty()) {
                    Observable.fromCallable {
                        LceResponse.Content(data = movieList.map { it.toDomainMovie() })
                    }
                } else {
                    movieService.getPopular()
                        .toObservable()
                        .doOnNext { networkResponse ->
                            if (networkResponse is NetworkResponse.Success) {
                                val networkPopularMovies: List<TmdbMovie> = networkResponse.body.results
                                saveMoviesForPlaylist(networkPopularMovies, PLAYLIST_POPULAR)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getMoviesForPlaylist(PLAYLIST_POPULAR))
                        }
                }
            }
    }

    //todo modify this to check if movies are are still valid (time based?)
    override fun getTopRated(): Observable<LceResponse<List<Movie>>> {
        return movieDao.getMoviesForPlaylistSingle(PLAYLIST_TOP_RATED)
            .toObservable()
            .flatMap { movieList ->
                if (movieList.isNotEmpty()) {
                    Observable.fromCallable {
                        LceResponse.Content(data = movieList.map { it.toDomainMovie() })
                    }
                } else {
                    movieService.getTopRated()
                        .toObservable()
                        .doOnNext { networkResponse ->
                            if (networkResponse is NetworkResponse.Success) {
                                val networkTopRatedMovies: List<TmdbMovie> = networkResponse.body.results
                                saveMoviesForPlaylist(networkTopRatedMovies, PLAYLIST_TOP_RATED)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getMoviesForPlaylist(PLAYLIST_TOP_RATED))
                        }
                }
            }
    }

    //todo modify this to check if movies are are still valid (time based?)
    override fun getUpcoming(): Observable<LceResponse<List<Movie>>> {
        return movieDao.getMoviesForPlaylistSingle(PLAYLIST_UPCOMING)
            .toObservable()
            .flatMap { movieList ->
                if (movieList.isNotEmpty()) {
                    Observable.fromCallable {
                        LceResponse.Content(data = movieList.map { it.toDomainMovie() })
                    }
                } else {
                    movieService.getUpcoming()
                        .toObservable()
                        .doOnNext { networkResponse ->
                            if (networkResponse is NetworkResponse.Success) {
                                val networkUpcomingMovies: List<TmdbMovie> = networkResponse.body.results
                                saveMoviesForPlaylist(networkUpcomingMovies, PLAYLIST_UPCOMING)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getMoviesForPlaylist(PLAYLIST_UPCOMING))
                        }
                }
            }
    }

    //todo modify this to check if movies are are still valid (time based?)
    override fun getNowPlaying(): Observable<LceResponse<List<Movie>>> {
        return movieDao.getMoviesForPlaylistSingle(PLAYLIST_NOW_PLAYING)
            .toObservable()
            .flatMap { movieList ->
                if (movieList.isNotEmpty()) {
                    Observable.fromCallable {
                        LceResponse.Content(data = movieList.map { it.toDomainMovie() })
                    }
                } else {
                    movieService.getNowPlaying()
                        .toObservable()
                        .doOnNext { networkResponse ->
                            if (networkResponse is NetworkResponse.Success) {
                                val networkNowPlayingVideos: List<TmdbMovie> = networkResponse.body.results
                                saveMoviesForPlaylist(networkNowPlayingVideos, PLAYLIST_NOW_PLAYING)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getMoviesForPlaylist(PLAYLIST_NOW_PLAYING))
                        }
                }
            }
    }

    override fun getMovieListsForMovie(movieId: Long): Observable<LceResponse<List<ContentListStatus>>> {
        return listDao.getMovieListsObservable()
            .map { allLists ->
                if (allLists.isNotEmpty()) {
                    val listsForMovie: List<RoomMovieList> = listDao.getListsForMovie(movieId = movieId)
                    val data = allLists.map { roomMovieList ->
                        val listContainsMovie: Boolean = listsForMovie.contains(roomMovieList)
                        roomMovieList.toDomainContentListStatus(movieId, listContainsMovie)
                    }
                    LceResponse.Content(data = data)
                } else {
                    LceResponse.Content(data = emptyList())
                }
            }
    }

    override fun getMovieLists(): Observable<LceResponse<List<ContentList>>> {
        return listDao.getMovieListsObservable()
            .map { movieLists ->
                if (movieLists.isNotEmpty()) {
                    LceResponse.Content(data = movieLists.toDomainMovieLists())
                } else {
                    LceResponse.Content(data = emptyList())
                }
            }
    }

    override fun getMoviesForList(listId: Long): Observable<LceResponse<List<Movie>>> {
        return listDao.getMoviesForListObservable(listId)
            .distinctUntilChanged()
            .map { movieDataList ->
                val domainMovies = movieDataList.toDomainMovies()
                if (domainMovies.isNotEmpty()) {
                    LceResponse.Content(data = domainMovies)
                } else {
                    LceResponse.Content(data = emptyList())
                }
            }
    }

    override fun toggleMovieWatchlistStatus(movieId: Long): Observable<LceResponse<Movie>> {
        return toggleMovieListStatus(movieId, LIST_ID_MOVIE_WATCHLIST)
    }

    override fun toggleMovieWatchedStatus(movieId: Long): Observable<LceResponse<Movie>> {
        return toggleMovieListStatus(movieId, LIST_ID_MOVIE_WATCHED)
    }

    override fun toggleMovieListStatus(movieId: Long, listId: Long): Observable<LceResponse<Movie>> {
        return movieDao.getMovieForIdSingle(movieId)
            .toObservable()
            .flatMap { movieList ->
                if (movieList.isNotEmpty()) {
                    when (listId) {
                        LIST_ID_MOVIE_WATCHLIST -> { // toggle watchlist state on movie object too
                            val dbMovie: RoomMovie = movieList[0]
                            val newWatchlistState: Boolean = !dbMovie.state.inWatchlist
                            val updatedMovie = dbMovie.copy(state = dbMovie.state.copy(inWatchlist = newWatchlistState))
                            movieDao.updateMovieAndToggleListStatus(updatedMovie, listId)
                        }
                        LIST_ID_MOVIE_WATCHED -> { // toggle watched state on movie object too
                            val dbMovie: RoomMovie = movieList[0]
                            val newWatchedState: Boolean = !dbMovie.state.isWatched
                            val updatedMovie = dbMovie.copy(state = dbMovie.state.copy(isWatched = newWatchedState))
                            movieDao.updateMovieAndToggleListStatus(updatedMovie, listId)
                        }
                        //todo move this to the top of the function so we don't have to fetch movie when we don't need it here
                        else -> movieDao.toggleMovieListStatus(movieId, listId)
                    }

                    // return full movie
                    val domainMovie: Movie? = getFullMovieFromDatabase(movieId)
                    if (domainMovie != null) {
                        Observable.fromCallable { LceResponse.Content(domainMovie) }
                    } else {
                        throw IllegalStateException("Movie should be in database before toggling list status.")
                    }
                } else {
                    val errorMessage = "Cannot toggle movie list status for list id $listId if " +
                        "movie is not in database. If trying to add a movie on search screen to " +
                        "a list, we must first convert search results to be saved in the movie " +
                        "table instead of just it's own table."
                    throw IllegalStateException(errorMessage)
                }
            }
    }

    override fun createMovieList(movieId: Long?, listTitle: String): Observable<LceResponse<Long>> {
        return listDao.getHighestMovieListOrderSingle()
            .flatMapObservable { currentHighestOrder ->
                val movieList = RoomMovieList(name = listTitle, sortOrder = currentHighestOrder + 1)
                val listId: Long = listDao.insertMovieList(movieList)

                if (movieId != null) {
                    movieDao.toggleMovieListStatus(movieId, listId)
                }

                Observable.fromCallable { LceResponse.Content(listId) }
            }
    }

    private fun saveFullMovieToDatabase(movie: TmdbMovie) {
        val movieId: Long = movie.id.toLong()

        movieDao.insertFullMovieData(
            movie = movie.toRoomMovie(true),
            castMembers = movie.credits?.cast?.toRoomMovieCast(movieId),
            crewMembers = movie.credits?.crew?.toRoomMovieCrew(movieId),
            genres = movie.genres?.toRoomMovieGenres(movieId),
            productionCompanies = movie.productionCompanies?.toRoomProductionCompanies(movieId),
            productionCountries = movie.productionCountries?.toRoomMovieProductionCountries(movieId),
            spokenLanguages = movie.spokenLanguages?.toRoomSpokenLanguages(movieId),
            recommendations = movie.recommendations?.results?.toRoomMovies(false),
            videos = movie.videos?.results?.toRoomMovieVideos(movieId)
        )
    }

    private fun getFullMovieFromDatabase(id: Long): Movie? {
        val dbMovieData: RoomMovieAllData? = movieDao.getFullMovieForId(id)
        return if (dbMovieData != null) {
            val movie: RoomMovie? = dbMovieData.movie
            if (movie != null) {
                val recommendations: List<RoomMovie> = movieDao.getRecommendationsForMovie(movie.id)
                return dbMovieData.toDomainMovie(recommendations)
            } else null
        } else null
    }

    private fun getMoviesForPlaylist(playlistName: String): List<Movie> {
        val dbMovieData: List<RoomMovie> = movieDao.getMoviesForPlaylist(playlistName)
        return dbMovieData.map { it.toDomainMovie() }
    }

    private fun saveMoviesForPlaylist(networkMovies: List<TmdbMovie>, playlistName: String) {
        val dbMovies = networkMovies.toRoomMovies(false)
        movieDao.insertAllMoviesForPlaylist(dbMovies, playlistName)
    }
}
