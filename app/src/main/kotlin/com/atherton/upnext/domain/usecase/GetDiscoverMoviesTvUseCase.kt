package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModel
import com.atherton.upnext.domain.repository.MovieRepository
import com.atherton.upnext.domain.repository.TvShowRepository
import io.reactivex.Single
import io.reactivex.functions.Function5
import javax.inject.Inject

class GetDiscoverMoviesTvUseCase @Inject constructor(
    private val tvShowRepository: TvShowRepository,
    private val movieRepository: MovieRepository
) {

    /**
     * Gets a list of featured tv shows and movies combined.
     *
     * If none of the responses are successful, we propagate the error the first failed response
     * as all of the responses will likely have the same error reason.
     */
    fun build(): Single<Response<List<SearchModel>>> {
        return Single.zip(
            tvShowRepository.getPopular(),
            movieRepository.getPopular(),
            movieRepository.getNowPlaying(),
            tvShowRepository.getTopRated(),
            movieRepository.getTopRated(),
            Function5<Response<List<SearchModel>>,
                Response<List<SearchModel>>,
                Response<List<SearchModel>>,
                Response<List<SearchModel>>,
                Response<List<SearchModel>>,
                Response<List<SearchModel>>> { popularTvResponse,
                                               popularMoviesResponse,
                                               nowPlayingMoviesResponse,
                                               topRatedTvResponse,
                                               topRatedMoviesResponse ->

                //todo add upcoming movies
                //todo add airing tv today
                //todo add on the air tv (this week)
                //todo look for more
                //todo look for specific channels to show?

                //todo don't forget to add the above responses into this check too
                val failedResponse: Response.Failure? = allResponsesFailed(
                    listOf(
                        popularTvResponse,
                        popularMoviesResponse,
                        nowPlayingMoviesResponse,
                        topRatedTvResponse,
                        topRatedMoviesResponse
                    )
                )
                if (failedResponse != null) { // all responses failed, return first failed response found
                    failedResponse
                } else { // there was at least 1 successful response
                    val popularTv: List<SearchModel>? = popularTvResponse.dataOrNull()
                    val popularMovies: List<SearchModel>? = popularMoviesResponse.dataOrNull()
                    val nowPlayingMovies: List<SearchModel>? = nowPlayingMoviesResponse.dataOrNull()
                    val topRatedTv: List<SearchModel>? = topRatedTvResponse.dataOrNull()
                    val topRatedMovies: List<SearchModel>? = topRatedMoviesResponse.dataOrNull()
                    //todo add more

                    //todo  populate response
                    val all = listOfNotNull(popularTv, popularMovies, nowPlayingMovies, topRatedTv, topRatedMovies)
                        .flatMap { it.asIterable() }
                        .distinct()
                        .sortedByDescending { it.popularity }

                    Response.Success(all, false) //todo check cached
                }
            })
    }

    /**
     * Iterates through all responses and if it finds that all responses contain a failure, then it returns
     * the first one it finds as all failures will be for the same reason.
     *
     * @param responses the list of responses to check for failures
     *
     * @return the first failed response or null if no responses failed
     */
    private fun allResponsesFailed(responses: List<Response<List<SearchModel>>>): Response.Failure? {
        val allFailed = responses.all { it is Response.Failure }
        return if (allFailed) {
            responses.first { it is Response.Failure } as Response.Failure
        } else null
    }
}