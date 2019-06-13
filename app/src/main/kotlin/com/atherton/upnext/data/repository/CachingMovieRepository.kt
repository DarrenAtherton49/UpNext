package com.atherton.upnext.data.repository

import com.atherton.upnext.data.db.dao.MovieDao
import com.atherton.upnext.data.db.model.movie.RoomMovie
import com.atherton.upnext.data.db.model.movie.RoomMovieAllData
import com.atherton.upnext.data.mapper.*
import com.atherton.upnext.data.network.model.NetworkResponse
import com.atherton.upnext.data.network.model.TmdbMovie
import com.atherton.upnext.data.network.service.TmdbMovieService
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.repository.MovieRepository
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingMovieRepository @Inject constructor(
    private val movieDao: MovieDao,
    private val movieService: TmdbMovieService
) : MovieRepository {

    //todo modify this to check if movie is in database and if it is still valid (time based?)
    override fun getMovie(id: Long): Observable<LceResponse<Movie>> {
        return movieDao.getMovieListForIdSingle(id)
            .toObservable()
            .flatMap { movieList ->
                if (movieList.isNotEmpty() && movieList[0].isModelComplete) { // movie is cached and has all data
                    Observable.fromCallable {
                        LceResponse.Content(data = getMovieFromDatabase(id)) // fetch the full movie and all relations
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
                            networkResponse.toDomainLceResponse(data = getMovieFromDatabase(id))
                        }
                }
            }
    }

    //todo modify this to check if movies are are still valid (time based?)
    override fun getPopular(): Observable<LceResponse<List<Movie>>> {
        return movieDao.getMoviesForPlaylistSingle(POPULAR)
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
                                saveMoviesForPlaylist(networkPopularMovies, POPULAR)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getMoviesForPlaylist(POPULAR))
                        }
                }
            }
    }

    //todo modify this to check if movies are are still valid (time based?)
    override fun getTopRated(): Observable<LceResponse<List<Movie>>> {
        return movieDao.getMoviesForPlaylistSingle(TOP_RATED)
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
                                saveMoviesForPlaylist(networkTopRatedMovies, TOP_RATED)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getMoviesForPlaylist(TOP_RATED))
                        }
                }
            }
    }

    //todo modify this to check if movies are are still valid (time based?)
    override fun getUpcoming(): Observable<LceResponse<List<Movie>>> {
        return movieDao.getMoviesForPlaylistSingle(UPCOMING)
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
                                saveMoviesForPlaylist(networkUpcomingMovies, UPCOMING)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getMoviesForPlaylist(UPCOMING))
                        }
                }
            }
    }

    //todo modify this to check if movies are are still valid (time based?)
    override fun getNowPlaying(): Observable<LceResponse<List<Movie>>> {
        return movieDao.getMoviesForPlaylistSingle(NOW_PLAYING)
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
                                saveMoviesForPlaylist(networkNowPlayingVideos, NOW_PLAYING)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getMoviesForPlaylist(NOW_PLAYING))
                        }
                }
            }
    }

    private fun saveFullMovieToDatabase(movie: TmdbMovie) {
        val movieId: Long = movie.id.toLong()
        movieDao.insertMovieData(
            movie = movie.toRoomMovie(true),
            genres = movie.genres?.toRoomGenres(movieId),
            productionCompanies = movie.productionCompanies?.toRoomProductionCompanies(movieId),
            productionCountries = movie.productionCountries?.toRoomProductionCountries(movieId),
            spokenLanguages = movie.spokenLanguages?.toRoomSpokenLanguages(movieId),
            castMembers = movie.credits?.cast?.toRoomCast(movieId),
            crewMembers = movie.credits?.crew?.toRoomCrew(movieId),
            recommendations = movie.recommendations?.results?.toRoomMovies(false),
            videos = movie.videos?.results?.toRoomVideos(movieId)
        )
    }

    private fun getMovieFromDatabase(id: Long): Movie {
        val dbMovieData: RoomMovieAllData = movieDao.getFullMovieForId(id)
        val recommendations: List<RoomMovie> = movieDao.getRecommendationsForMovie(dbMovieData.movie.id)
        return dbMovieData.toDomainMovie(recommendations)
    }

    private fun getMoviesForPlaylist(playlistName: String): List<Movie> {
        val dbMovieData: List<RoomMovie> = movieDao.getMoviesForPlaylist(playlistName)
        return dbMovieData.map { it.toDomainMovie() }
    }

    private fun saveMoviesForPlaylist(networkMovies: List<TmdbMovie>, playlistName: String) {
        val dbMovies = networkMovies.toRoomMovies(false)
        movieDao.insertAllMoviesForPlaylist(dbMovies, playlistName)
    }

    companion object {
        private const val POPULAR = "Popular"
        private const val TOP_RATED = "Top Rated"
        private const val UPCOMING = "Upcoming"
        private const val NOW_PLAYING = "Now Playing"
    }
}
