package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModel
import com.atherton.upnext.domain.repository.MovieRepository
import com.atherton.upnext.domain.repository.TvShowRepository
import io.reactivex.Single
import io.reactivex.functions.Function5
import javax.inject.Inject

class GetFeaturedMoviesTvUseCase @Inject constructor(
    private val tvShowRepository: TvShowRepository,
    private val movieRepository: MovieRepository
) {

    /**
     * Gets a list of featured tv shows and movies combined.
     *
     * If none of the responses are successful, we propagate the error the first failed response
     * as all of the responses will likely have the same error reason.
     */
    fun build(): Single<Response<DiscoverFeaturedResponse>> {
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
                Response<DiscoverFeaturedResponse>> { popularTvResponse,
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
                        nowPlayingMoviesResponse
                    )
                )
                if (failedResponse != null) { // all responses failed, return first failed response found
                    failedResponse
                } else { // there was at least 1 successful response
                    var popularTv: List<SearchModel>? = null
                    var popularMovies: List<SearchModel>? = null
                    var nowPlayingMovies: List<SearchModel>? = null
                    var topRatedTv: List<SearchModel>? = null
                    var topRatedMovies: List<SearchModel>? = null
                    //todo add more

                    if (popularTvResponse is Response.Success) {
                        popularTv = popularTvResponse.data
                    }
                    if (popularMoviesResponse is Response.Success) {
                        popularMovies = popularMoviesResponse.data
                    }
                    if (nowPlayingMoviesResponse is Response.Success) {
                        nowPlayingMovies = nowPlayingMoviesResponse.data
                    }

                    Response.Success(
                        DiscoverFeaturedResponse(
                            popularTvMovies = generatePopular(popularTv, popularMovies),
                            nowPlayingMovies = nowPlayingMovies
                        ),
                        cached = false
                    )

                }
            })
    }

    /**
     * Takes the lists of popular tv shows and movies, checks if they are null and adds them to a combined list.
     * Then we sort the combined list by popularity.
     *
     * @param popularTv a list of popular tv shows
     * @param popularMovies a list of popular movies
     *
     * @return a combined list of popular tv shows and movies or null if none exist
     */
    private fun generatePopular(popularTv: List<SearchModel>?, popularMovies: List<SearchModel>?): List<SearchModel>? {
        val popular: MutableList<SearchModel> = ArrayList()
        popularTv?.let { popular.addAll(it) }
        popularMovies?.let { popular.addAll(it) }
        return if (popular.isNotEmpty()) {
            popular.toList().sortedByDescending { it.popularity }
        } else null
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

data class DiscoverFeaturedResponse(
    val popularTvMovies: List<SearchModel>?,
    val nowPlayingMovies: List<SearchModel>?
    //val topRatedTvMovies: List<SearchModel>?
)
