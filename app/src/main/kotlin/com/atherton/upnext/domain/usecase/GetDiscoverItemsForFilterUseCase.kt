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

    fun build(filter: DiscoverFilter): Single<Response<List<Searchable>>> {
        return when (filter) {
            //todo add trending
            //is DiscoverFilter.Preset.TrendingAll -> getTrendingAll()
            is DiscoverFilter.Preset.TopRatedTvMovies -> getTopRatedTvMovies()
            is DiscoverFilter.Preset.PopularTvMovies -> getPopularTvMovies()
            is DiscoverFilter.Preset.NowPlayingMovies -> getNowPlayingMovies()
            is DiscoverFilter.Preset.UpcomingMovies -> getUpcomingMovies()
            is DiscoverFilter.Preset.AiringTodayTv -> getAiringTodayTv()
            is DiscoverFilter.Preset.OnTheAirTv -> getOnTheAirTv()
        }
    }

    /**
     * Gets a list of the top rated tv shows and movies combined.
     *
     * If only one of the responses is successful, it just returns that response.
     * If neither of the responses are successful, we propagate the error inside the tv shows response
     * as the movies response will likely have the same error reason.
     */
    private fun getTopRatedTvMovies(): Single<Response<List<Searchable>>> {
        return zip(tvShowRepository.getTopRated(), movieRepository.getTopRated(), BiFunction { tvResponse, moviesResponse ->
            when {
                tvResponse is Response.Success && moviesResponse is Response.Success -> {
                    val topRated: List<Searchable> = tvResponse.data + moviesResponse.data
                    val sorted: List<Searchable> = topRated.sortedByDescending { searchModel ->
                        when (searchModel) {
                            is TvShow -> searchModel.voteAverage
                            is Movie -> searchModel.voteAverage
                            is Person -> searchModel.popularity
                            else -> searchModel.popularity
                        }
                    }
                    val cached = tvResponse.cached && moviesResponse.cached
                    Response.Success(sorted, cached)
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
    private fun getPopularTvMovies(): Single<Response<List<Searchable>>> {
        return zip(tvShowRepository.getPopular(), movieRepository.getPopular(), BiFunction { tvResponse, moviesResponse ->
            when {
                tvResponse is Response.Success && moviesResponse is Response.Success -> {
                    val mostPopular: List<Searchable> = tvResponse.data + moviesResponse.data
                    val sorted = mostPopular.sortedByDescending { it.popularity }
                    val cached = tvResponse.cached && moviesResponse.cached
                    Response.Success(sorted, cached)
                }
                tvResponse is Response.Success -> tvResponse
                moviesResponse is Response.Success -> moviesResponse
                else -> tvResponse
            }
        })
    }

    private fun getNowPlayingMovies(): Single<Response<List<Searchable>>> {
        return movieRepository.getNowPlaying().map { it.moviesToSearchModelResponse() }
    }

    private fun getUpcomingMovies(): Single<Response<List<Searchable>>> {
        return movieRepository.getUpcoming().map { it.moviesToSearchModelResponse() }
    }

    private fun getAiringTodayTv(): Single<Response<List<Searchable>>> {
        return tvShowRepository.getAiringToday().map { it.tvToSearchModelResponse() }
    }

    private fun getOnTheAirTv(): Single<Response<List<Searchable>>> {
        return tvShowRepository.getOnTheAir().map { it.tvToSearchModelResponse() }
    }

    private fun Response<List<Movie>>.moviesToSearchModelResponse(): Response<List<Searchable>> {
        return when (this) {
            is Response.Success -> Response.Success<List<Searchable>>(this.data, this.cached)
            else -> this
        }
    }

    private fun Response<List<TvShow>>.tvToSearchModelResponse(): Response<List<Searchable>> {
        return when (this) {
            is Response.Success -> Response.Success<List<Searchable>>(this.data, this.cached)
            else -> this
        }
    }
}
