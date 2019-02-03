package com.atherton.upnext.data.mapper

import com.atherton.upnext.data.api.*
import com.atherton.upnext.data.model.*

/*
 * A collection of extension functions to map from network models to app-level/domain models.
 */

fun TmdbApiError.toDomainApiError(): ApiError = ApiError(statusMessage, statusCode)

fun List<TmdbMultiSearchModel>?.toDomainSearchModels(): List<SearchModel> {
    return this?.map { searchResult ->
        when (searchResult) {
            is TmdbTvResult -> searchResult.toDomainTvShow()
            is TmdbMovieResult -> searchResult.toDomainMovie()
            is TmdbPersonResult -> searchResult.toDomainPerson()
        }
    } ?: emptyList()
}

private fun TmdbTvResult.toDomainTvShow(): TvShow {
    return TvShow(
        backdropPath,
        firstAirDate,
        genreIds,
        id,
        name,
        originCountry,
        originalLanguage,
        originalName,
        overview,
        posterPath,
        popularity,
        voteAverage,
        voteCount
    )
}

private fun TmdbMovieResult.toDomainMovie(): Movie {
    return Movie(
        adultContent,
        backdropPath,
        genreIds,
        id,
        originalLanguage,
        originalTitle,
        overview,
        popularity,
        posterPath,
        releaseDate,
        title,
        video,
        voteAverage,
        voteCount
    )
}

private fun TmdbPersonResult.toDomainPerson(): Person {
    return Person(
        adultContent,
        id,
        knownFor.toDomainSearchModels(),
        name,
        popularity,
        profilePath
    )
}
