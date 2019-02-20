package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.*
import com.atherton.upnext.domain.repository.MovieRepository
import com.atherton.upnext.domain.repository.TvShowRepository
import io.reactivex.Single
import io.reactivex.Single.zip
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class GetDiscoverItemsForFilterUseCase @Inject constructor(
    private val tvShowRepository: TvShowRepository,
    private val movieRepository: MovieRepository
) {

    fun build(filter: DiscoverFilter): Single<Response<List<SearchModel>>> {
        return when (filter) {
            is DiscoverFilter.Preset.TopRatedTvMovies -> getTopRatedTvMovies()
            is DiscoverFilter.Preset.PopularTvMovies -> getPopularTvMovies()
            is DiscoverFilter.Preset.NowPlayingMovies -> getNowPlayingMovies()
        }
    }

    /**
     * Gets a list of the top rated tv shows and movies combined.
     *
     * If only one of the responses is successful, it just returns that response.
     * If neither of the responses are successful, we propagate the error inside the tv shows response
     * as the movies response will likely have the same error reason.
     */
    private fun getTopRatedTvMovies(): Single<Response<List<SearchModel>>> {
        return zip(tvShowRepository.getTopRated(), movieRepository.getTopRated(), BiFunction { tvResponse, moviesResponse ->
            when {
                tvResponse is Response.Success && moviesResponse is Response.Success -> {
                    val topRated = (tvResponse.data + moviesResponse.data).sortedByDescending { searchModel ->
                        when (searchModel) {
                            is TvShow -> searchModel.voteAverage
                            is Movie -> searchModel.voteAverage
                            else -> searchModel.popularity
                        }
                    }
                    val cached = tvResponse.cached && moviesResponse.cached
                    Response.Success(topRated, cached)
                }
                tvResponse is Response.Success -> tvResponse
                moviesResponse is Response.Success -> moviesResponse
                else -> tvResponse
            }
        })
    }

    /**
     * Gets a list of the most popular tv shows and movies combined.
     *
     * If only one of the responses is successful, it just returns that response.
     * If neither of the responses are successful, we propagate the error inside the tv shows response
     * as the movies response will likely have the same error reason.
     */
    private fun getPopularTvMovies(): Single<Response<List<SearchModel>>> {
        return zip(tvShowRepository.getPopular(), movieRepository.getPopular(), BiFunction { tvResponse, moviesResponse ->
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

    private fun getNowPlayingMovies(): Single<Response<List<SearchModel>>> {
        return movieRepository.getNowPlaying().map {
            when (it) {
                is Response.Success -> Response.Success<List<SearchModel>>(it.data, it.cached)
                else -> it
            }
        }
    }
}
