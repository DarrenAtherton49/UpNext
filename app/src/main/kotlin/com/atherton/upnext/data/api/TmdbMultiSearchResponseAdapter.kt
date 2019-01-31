package com.atherton.upnext.data.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class TmdbMultiSearchResponseAdapter {

    /*
     * This signature tells Moshi that when an API call tries to create a List<TmdbMultiSearchEntity>,
     * it can use a List<TmdbMultiSearchResult> to do this. This takes the more generic search results
     * and maps them into the correct entities based on 'media_type'.
     */
    @FromJson
    fun fromJson(searchResults: List<TmdbMultiSearchResult>): List<TmdbMultiSearchModel>? =
        searchResults.toSpecificEntities()

    private fun List<TmdbMultiSearchResult>?.toSpecificEntities(): List<TmdbMultiSearchModel>? {
        return this?.map {
            when (it.mediaType) {
                "movie" -> it.toMovie()
                "tv" -> it.toTvShow()
                "person" -> it.toPerson()
                else -> throw IllegalArgumentException("media_type should be either 'movie', 'tv' or 'person'.")
            }
        }
    }

    private fun TmdbMultiSearchResult.toMovie(): TmdbMovieResult {
        return TmdbMovieResult(
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

    private fun TmdbMultiSearchResult.toTvShow(): TmdbTvResult {
        return TmdbTvResult(
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

    private fun TmdbMultiSearchResult.toPerson(): TmdbPersonResult =
        TmdbPersonResult(adultContent, id, knownFor.toSpecificEntities(), name, popularity, profilePath)

    @ToJson
    fun toJson(entity: List<TmdbMultiSearchModel>): List<TmdbMultiSearchResult> {
        throw UnsupportedOperationException()
    }
}
