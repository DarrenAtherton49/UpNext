package com.atherton.upnext.data.mapper

import com.atherton.upnext.data.model.*
import com.atherton.upnext.domain.model.*

/*
 * A collection of extension functions to map from network models to app-level/domain models.
 */

/**
 * Maps a NetworkResponse to a domain Result.
 *
 * @param dataMapper provides a way to map from data layer models to app-level/domain models
 * @param cachedData whether or not the data is old/cached
 *
 */
internal fun <DATA : Any, DOMAIN : Any> NetworkResponse<DATA, TmdbApiError>.toDomainResponse(
    cachedData: Boolean,
    dataMapper: (DATA) -> DOMAIN
): Response<DOMAIN> {
    return when (this) {
        is NetworkResponse.Success -> {
            Response.Success(dataMapper(body), cachedData)
        }
        is NetworkResponse.ServerError<TmdbApiError
            > -> {
            Response.Failure.ServerError(error?.toDomainApiError(), code)
        }
        is NetworkResponse.NetworkError -> {
            Response.Failure.NetworkError(error)
        }
    }
}

private fun TmdbApiError.toDomainApiError(): ApiError = ApiError(statusMessage, statusCode)

internal fun TmdbConfiguration.toDomainConfig(): Config {
    with(images) {
        return Config(
            baseUrl,
            secureBaseUrl,
            backdropSizes,
            logoSizes,
            posterSizes,
            profileSizes,
            stillSizes
        )
    }
}


internal fun List<TmdbMultiSearchModel>?.toDomainSearchModels(): List<SearchModel> {
    return this?.map { searchResult ->
        when (searchResult) {
            is TmdbTvShow -> searchResult.toDomainTvShow()
            is TmdbMovie -> searchResult.toDomainMovie()
            is TmdbPerson -> searchResult.toDomainPerson()
        }
    } ?: emptyList()
}

fun TmdbTvShow.toDomainTvShow(): TvShow {
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

fun TmdbMovie.toDomainMovie(): Movie {
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

fun TmdbPerson.toDomainPerson(): Person {
    return Person(
        adultContent,
        id,
        knownFor.toDomainSearchModels(),
        name,
        popularity,
        profilePath
    )
}
