package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.Searchable
import com.atherton.upnext.domain.model.TvShow
import com.atherton.upnext.domain.repository.MovieRepository
import com.atherton.upnext.domain.repository.TvShowRepository
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables.zip
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
    operator fun invoke(): Observable<LceResponse<List<Searchable>>> {
        return zip(tvShowRepository.getPopular(), movieRepository.getPopular()) { tvResponse, moviesResponse ->
            when {
                tvResponse is LceResponse.Content && moviesResponse is LceResponse.Content -> {
                    val sorted = sortByPopularity(tvResponse.data, moviesResponse.data)
                    val cached = tvResponse.cached && moviesResponse.cached
                    LceResponse.Content(sorted, cached)
                }
                // when one response has final content and other is loading, surface it as loading
                tvResponse is LceResponse.Content && moviesResponse is LceResponse.Loading -> {
                    val sorted = sortByPopularity(tvResponse.data, moviesResponse.data)
                    LceResponse.Loading(sorted)
                }
                tvResponse is LceResponse.Loading && moviesResponse is LceResponse.Content -> {
                    val sorted = sortByPopularity(tvResponse.data, moviesResponse.data)
                    LceResponse.Loading(sorted)
                }
                // when both responses are still loading, surface it as loading
                tvResponse is LceResponse.Loading && moviesResponse is LceResponse.Loading -> {
                    val sorted = sortByPopularity(tvResponse.data, moviesResponse.data)
                    LceResponse.Loading(sorted)
                }
                tvResponse is LceResponse.Content -> tvResponse
                moviesResponse is LceResponse.Content -> moviesResponse
                else -> tvResponse
            }
        }
    }

    private fun sortByPopularity(tvShows: List<TvShow>, movies: List<Movie>): List<Searchable> {
        val mostPopular: List<Searchable> = tvShows + movies
        return mostPopular.sortedByDescending { it.popularity }
    }
}
