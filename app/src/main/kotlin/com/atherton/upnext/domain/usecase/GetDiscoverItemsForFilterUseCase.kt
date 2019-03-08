package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.*
import com.atherton.upnext.domain.repository.MovieRepository
import com.atherton.upnext.domain.repository.TvShowRepository
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables.zip
import javax.inject.Inject

class GetDiscoverItemsForFilterUseCase @Inject constructor(
    private val tvShowRepository: TvShowRepository,
    private val movieRepository: MovieRepository
) {

    operator fun invoke(filter: DiscoverFilter): Observable<LceResponse<List<Searchable>>> {
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
     * If one of the responses is successful and the other is loading, we still surface the loading state.
     * If neither of the responses are successful, we propagate the error inside the tv shows response
     * as the movies response will likely have the same error reason.
     */
    private fun getTopRatedTvMovies(): Observable<LceResponse<List<Searchable>>> {
        return zip(tvShowRepository.getTopRated(), movieRepository.getTopRated()) { tvResponse, moviesResponse ->
            when {
                // when both responses have final content (not loading), combine both
                tvResponse is LceResponse.Content && moviesResponse is LceResponse.Content -> {
                    val sorted = sortTopRated(tvResponse.data, moviesResponse.data)
                    val cached = tvResponse.cached && moviesResponse.cached
                    LceResponse.Content(sorted, cached)
                }
                // when one response has final content and other is loading, surface it as loading
                tvResponse is LceResponse.Content && moviesResponse is LceResponse.Loading -> {
                    val sorted = sortTopRated(tvResponse.data, moviesResponse.data)
                    LceResponse.Loading(sorted)
                }
                tvResponse is LceResponse.Loading && moviesResponse is LceResponse.Content -> {
                    val sorted = sortTopRated(tvResponse.data, moviesResponse.data)
                    LceResponse.Loading(sorted)
                }
                // when both responses are still loading, surface it as loading
                tvResponse is LceResponse.Loading && moviesResponse is LceResponse.Loading -> {
                    val sorted = sortTopRated(tvResponse.data, moviesResponse.data)
                    LceResponse.Loading(sorted)
                }
                // when only one of the responses have content and other has failed, surface the successful one
                tvResponse is LceResponse.Content -> tvResponse
                moviesResponse is LceResponse.Content -> moviesResponse
                // when both responses have failed, surface the tv response as an error
                else -> tvResponse
            }
        }
    }

    /**
     * Gets a list of the most popular tv shows and movies combined.
     *
     * If only one of the responses is successful, it just returns that response.
     * If one of the responses is successful and the other is loading, we still surface the loading state.
     * If neither of the responses are successful, we propagate the error inside the tv shows response
     * as the movies response will likely have the same error reason.
     */
    private fun getPopularTvMovies(): Observable<LceResponse<List<Searchable>>> {
        return zip(tvShowRepository.getPopular(), movieRepository.getPopular()) { tvResponse, moviesResponse ->
            when {
                // when both responses have final content (not loading), combine both
                tvResponse is LceResponse.Content && moviesResponse is LceResponse.Content -> {
                    val sorted = sortMostPopular(tvResponse.data, moviesResponse.data)
                    val cached = tvResponse.cached && moviesResponse.cached
                    LceResponse.Content(sorted, cached)
                }
                // when one response has final content and other is loading, surface it as loading
                tvResponse is LceResponse.Content && moviesResponse is LceResponse.Loading -> {
                    val sorted = sortMostPopular(tvResponse.data, moviesResponse.data)
                    LceResponse.Loading(sorted)
                }
                tvResponse is LceResponse.Loading && moviesResponse is LceResponse.Content -> {
                    val sorted = sortMostPopular(tvResponse.data, moviesResponse.data)
                    LceResponse.Loading(sorted)
                }
                // when both responses are still loading, surface it as loading
                tvResponse is LceResponse.Loading && moviesResponse is LceResponse.Loading -> {
                    val sorted = sortMostPopular(tvResponse.data, moviesResponse.data)
                    LceResponse.Loading(sorted)
                }
                // when only one of the responses have content and other has failed, surface the successful one
                tvResponse is LceResponse.Content -> tvResponse
                moviesResponse is LceResponse.Content -> moviesResponse
                // when both responses have failed, surface the tv response as an error
                else -> tvResponse
            }
        }
    }

    private fun getNowPlayingMovies(): Observable<LceResponse<List<Searchable>>> {
        return movieRepository.getNowPlaying().map { it.moviesToSearchModelsResponse() }
    }

    private fun getUpcomingMovies(): Observable<LceResponse<List<Searchable>>> {
        return movieRepository.getUpcoming().map { it.moviesToSearchModelsResponse() }
    }

    private fun getAiringTodayTv(): Observable<LceResponse<List<Searchable>>> {
        return tvShowRepository.getAiringToday().map { it.tvShowToSearchModelsResponse() }
    }

    private fun getOnTheAirTv(): Observable<LceResponse<List<Searchable>>> {
        return tvShowRepository.getOnTheAir().map { it.tvShowToSearchModelsResponse() }
    }

    private fun LceResponse<List<Movie>>.moviesToSearchModelsResponse(): LceResponse<List<Searchable>> {
        return when (this) {
            is LceResponse.Content -> LceResponse.Content<List<Searchable>>(this.data, this.cached)
            else -> this
        }
    }

    private fun LceResponse<List<TvShow>>.tvShowToSearchModelsResponse(): LceResponse<List<Searchable>> {
        return when (this) {
            is LceResponse.Content -> LceResponse.Content<List<Searchable>>(this.data, this.cached)
            else -> this
        }
    }

    private fun sortTopRated(tvShows: List<TvShow>, movies: List<Movie>): List<Searchable> {
        val topRated: List<Searchable> = tvShows + movies
        return topRated.sortedByDescending { searchModel ->
            when (searchModel) {
                is TvShow -> searchModel.voteAverage
                is Movie -> searchModel.voteAverage
                is Person -> searchModel.popularity
                else -> searchModel.popularity
            }
        }
    }

    private fun sortMostPopular(tvShows: List<TvShow>, movies: List<Movie>): List<Searchable> {
        val mostPopular: List<Searchable> = tvShows + movies
        return mostPopular.sortedByDescending { it.popularity }
    }
}
