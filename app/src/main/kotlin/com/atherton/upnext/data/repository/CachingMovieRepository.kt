package com.atherton.upnext.data.repository

import com.atherton.upnext.data.mapper.toDomainLceResponse
import com.atherton.upnext.data.mapper.toDomainMovie
import com.atherton.upnext.data.model.NetworkResponse
import com.atherton.upnext.data.model.TmdbApiError
import com.atherton.upnext.data.model.TmdbMovie
import com.atherton.upnext.data.model.TmdbPagedResponse
import com.atherton.upnext.data.network.service.TmdbMovieService
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.repository.MovieRepository
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingMovieRepository @Inject constructor(
    //todo add in-memory cache
    //todo add database
    private val movieService: TmdbMovieService
) : MovieRepository {

    override fun getMovie(id: Int): Observable<LceResponse<Movie>> {
        return movieService.getMovieDetails(id)
            .toObservable()
            .map { it.toDomainLceResponse(false) { movie -> movie.toDomainMovie() }
        }
    }

    override fun getPopular(): Observable<LceResponse<List<Movie>>> {
        return movieService.getPopular().toDomainMovies()
    }

    override fun getUpcoming(): Observable<LceResponse<List<Movie>>> {
        return movieService.getUpcoming().toDomainMovies()
    }

    override fun getTopRated(): Observable<LceResponse<List<Movie>>> {
        return movieService.getTopRated().toDomainMovies()
    }

    override fun getNowPlaying(): Observable<LceResponse<List<Movie>>> {
        //todo remove this temp object and replace with cached list
        return Observable.concat(
            Observable.fromCallable {
                LceResponse.Loading(
                    listOf(
                        Movie(false, "",
                            null, null, 1,
                            null, null, "",
                            24.4f, "", "", "FAKE MOVIE",
                            false, 24.4f, 4)
                    )
                )
            },
            movieService.getNowPlaying()
                .map {
                    it.toDomainLceResponse(false) { response ->
                        response.results.map { movie -> movie.toDomainMovie() }
                    }
                }
                .toObservable()
        )
    }

    private fun Single<NetworkResponse<TmdbPagedResponse<TmdbMovie>, TmdbApiError>>.toDomainMovies()
        : Observable<LceResponse<List<Movie>>> {
        return this.map {
            it.toDomainLceResponse(false) { response ->
                response.results.map { movie -> movie.toDomainMovie() }
            }
        }.toObservable()
    }
}
