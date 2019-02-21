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
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingMovieRepository @Inject constructor(
    //todo add in-memory cache
    //todo add database
    private val movieService: TmdbMovieService
) : MovieRepository {

    override fun getMovie(id: Int): Single<Response<Movie>> {
        return movieService.getMovie(id).map {
            it.toDomainResponse(false) { movie -> movie.toDomainMovie() }
        }
    }

    override fun getPopular(): Single<Response<List<Movie>>> {
        return movieService.getPopular().toDomainMovies()
    }

    override fun getUpcoming(): Single<Response<List<Movie>>> {
        return movieService.getUpcoming().toDomainMovies()
    }

    override fun getTopRated(): Single<Response<List<Movie>>> {
        return movieService.getTopRated().toDomainMovies()
    }

    override fun getNowPlaying(): Single<Response<List<Movie>>> {
        return movieService.getNowPlaying()
            .map {
                it.toDomainResponse(false) { response ->
                    response.results.map { movie -> movie.toDomainMovie() }
                }
            }
    }

    private fun Single<NetworkResponse<TmdbPagedResponse<TmdbMovie>, TmdbApiError>>.toDomainMovies()
        : Single<Response<List<Movie>>> {
        return this.map {
            it.toDomainResponse(false) { response ->
                response.results.map { movie -> movie.toDomainMovie() }
            }
        }
    }
}
