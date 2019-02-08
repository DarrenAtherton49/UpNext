package com.atherton.upnext.data.repository

import com.atherton.upnext.data.mapper.toDomainMovie
import com.atherton.upnext.data.mapper.toDomainResponse
import com.atherton.upnext.data.network.TmdbMovieService
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

    override fun popular(): Single<Response<List<Movie>>> {
        return movieService.getPopular()
            .map {
                it.toDomainResponse(false) { response ->
                    response.results.map { tvShow ->
                        tvShow.toDomainMovie()
                    }
                }
            }
    }
}
