package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModel
import com.atherton.upnext.domain.repository.MovieRepository
import com.atherton.upnext.domain.repository.TvShowRepository
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class GetPopularMoviesTvUseCase @Inject constructor(
    private val tvShowRepository: TvShowRepository,
    private val movieRepository: MovieRepository
) {

    /**
     * Gets a list of the most popular tv shows and movies combined.
     *
     * If only one of the responses is successful, it just returns that response.
     * If neither of the responses are successful, we propagate the error inside the tv shows response
     * as the movies response will likely have the same error reason.
     */
    fun build(): Single<Response<List<SearchModel>>> {
        return Single.zip(
            tvShowRepository.getPopular(),
            movieRepository.getPopular(),
            BiFunction { tvResponse, moviesResponse ->
                when {
                    tvResponse is Response.Success && moviesResponse is Response.Success -> {
                        val mostPopular = (tvResponse.data + moviesResponse.data).sortedByDescending { it.popularity }
                        val cached = tvResponse.cached && moviesResponse.cached
                        Response.Success(mostPopular, cached)
                    }
                    tvResponse is Response.Success -> tvResponse
                    moviesResponse is Response.Success -> moviesResponse
                    else -> tvResponse
                }
            })
    }
}
