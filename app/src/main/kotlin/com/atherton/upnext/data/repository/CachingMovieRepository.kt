package com.atherton.upnext.data.repository

import com.atherton.upnext.data.mapper.toDomainMovie
import com.atherton.upnext.data.mapper.toDomainResponse
import com.atherton.upnext.data.model.NetworkResponse
import com.atherton.upnext.data.model.TmdbApiError
import com.atherton.upnext.data.model.TmdbMovie
import com.atherton.upnext.data.model.TmdbPagedResponse
import com.atherton.upnext.data.network.service.TmdbMovieService
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.Response
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

    override fun getMovie(id: Int): Observable<Response<Movie>> {
        return movieService.getMovieDetails(id)
            .toObservable()
            .map { it.toDomainResponse(false) { movie -> movie.toDomainMovie() }
        }
    }

    override fun getPopular(): Observable<Response<List<Movie>>> {
        return movieService.getPopular().toDomainMovies()
    }

    override fun getUpcoming(): Observable<Response<List<Movie>>> {
        return movieService.getUpcoming().toDomainMovies()
    }

    override fun getTopRated(): Observable<Response<List<Movie>>> {
        return movieService.getTopRated().toDomainMovies()
    }

    override fun getNowPlaying(): Observable<Response<List<Movie>>> {
        return Observable.concat(
            Observable.fromCallable {
                Response.Success(
                    listOf(
                        Movie(false, "",
                            null, null, 1,
                            null, null, "",
                            24.4f, "", "", "FAKE MOVIE",
                            false, 24.4f, 4)
                    ),
                    true
                )
            },
            movieService.getNowPlaying()
                .map {
                    it.toDomainResponse(false) { response ->
                        response.results.map { movie -> movie.toDomainMovie() }
                    }
                }
                .toObservable()
        )
    }

    private fun Single<NetworkResponse<TmdbPagedResponse<TmdbMovie>, TmdbApiError>>.toDomainMovies()
        : Observable<Response<List<Movie>>> {
        return this.map {
            it.toDomainResponse(false) { response ->
                response.results.map { movie -> movie.toDomainMovie() }
            }
        }.toObservable()
    }
}
