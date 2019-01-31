package com.atherton.upnext.data.mapper

import com.atherton.upnext.data.api.TmdbMovieResult
import com.atherton.upnext.data.api.TmdbMultiSearchModel
import com.atherton.upnext.data.api.TmdbPersonResult
import com.atherton.upnext.data.api.TmdbTvResult
import com.atherton.upnext.data.model.Movie
import com.atherton.upnext.data.model.Person
import com.atherton.upnext.data.model.SearchModel
import com.atherton.upnext.data.model.TvShow

/*
 * A collection of extension functions to map from network models to app-level/domain models.
 */

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
