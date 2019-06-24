package com.atherton.upnext.presentation.common.searchmodel

import com.atherton.upnext.domain.model.*
import com.atherton.upnext.presentation.features.content.formatReleaseYear
import com.atherton.upnext.presentation.features.content.formatVoteAverage
import com.atherton.upnext.presentation.features.movies.content.MovieListItem
import com.atherton.upnext.presentation.util.AppStringProvider

//todo write function to generate path based on device screen size?
fun buildBackdropPath(backdropPath: String?, config: Config): String? =
    backdropPath?.let { "${config.secureBaseUrl}${config.backdropSizes[1]}$backdropPath" }

//todo write function to generate path based on device screen size?
fun buildPosterPath(posterPath: String?, config: Config): String? =
    posterPath?.let { "${config.secureBaseUrl}${config.posterSizes[2]}$posterPath" }

//todo write function to generate path based on device screen size?
fun buildProfilePath(profilePath: String?, config: Config): String? =
    profilePath?.let { "${config.secureBaseUrl}${config.profileSizes[1]}$profilePath" }

// generate image urls for any screen where the item is a thumbnail - e.g. discover, search, watchlist etc.
// combines the base url, size and path
internal fun List<Searchable>.withSearchModelListImageUrls(config: Config): List<Searchable> {
    return this.map {
        when (it) {
            is TvShow -> {
                it.copy(
                    backdropPath = buildBackdropPath(it.backdropPath, config),
                    posterPath = buildPosterPath(it.posterPath, config)
                )
            }
            is Movie -> {
                it.copy(
                    backdropPath = buildBackdropPath(it.backdropPath, config),
                    posterPath = buildPosterPath(it.posterPath, config)
                )
            }
            is Person -> it.copy(profilePath = buildProfilePath(it.profilePath, config))
            else -> null
        }
    }.filterIsInstance(Searchable::class.java)
}

internal fun List<Movie>.formattedForMovieList(config: Config, appStringProvider: AppStringProvider): List<MovieListItem> {
    return this.map { movie ->
        val releaseYear: String? = formatReleaseYear(movie.releaseDate)
        val runtimeMins: String? = movie.detail?.runtime?.toString()?.let { appStringProvider.getRuntimeString(it) }
        val rating: String? = formatVoteAverage(movie.voteAverage)

        val titleAndReleaseDateString: String? = when {
            movie.title != null && releaseYear != null -> "${movie.title} ($releaseYear)"
            movie.title != null -> movie.title
            else -> null
        }

        val genres: List<Genre>? = movie.detail?.genres
        val genresString: String? = if (genres != null && genres.isNotEmpty()) {
            genres.mapNotNull { it.name }.joinToString(separator = ", ")
        } else null

        MovieListItem(
            genresString = genresString,
            movieId = movie.id,
            posterPath = buildPosterPath(movie.posterPath, config),
            runtime = runtimeMins,
            titleAndReleaseDate = titleAndReleaseDateString,
            voteAverage = rating
//            watchlistButtonText = if (movie.state.inWatchlist) {
//                appStringProvider.getRemoveFromWatchlistString()
//            } else {
//                appStringProvider.getAddToWatchlistString()
//            }
        )
    }
}
