package com.atherton.upnext.data.repository.movies

import com.atherton.upnext.data.model.Movie
import com.atherton.upnext.data.repository.Response
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingMoviesRepository @Inject constructor(
    //todo add in-memory cache
    //todo add database
    //private val discoverService: TmdbDiscoverService
) : MoviesRepository {

    override fun popular(): Single<Response<List<Movie>>> {
        return Single.just(
            Response.Success(
                listOf(
                    Movie(
                        true,
                        "",
                        listOf(),
                        0,
                        "",
                        "",
                        "",
                        0f,
                        "",
                        "",
                        "",
                        true,
                        2.1f,
                        1
                    )
                ),
                false
            )
        )
    }
}
