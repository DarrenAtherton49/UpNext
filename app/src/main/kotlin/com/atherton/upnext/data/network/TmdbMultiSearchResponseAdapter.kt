package com.atherton.upnext.data.network

import com.atherton.upnext.data.model.*
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
                "movie" -> it.toTmdbMovie()
                "tv" -> it.toTmdbTvShow()
                "person" -> it.toTmdbPerson()
                else -> throw IllegalArgumentException("media_type should be either 'movie', 'tv' or 'person'.")
            }
        }
    }

    private fun TmdbMultiSearchResult.toTmdbMovie(): TmdbMovie {
        return TmdbMovie(
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
            voteCount,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }

    private fun TmdbMultiSearchResult.toTmdbTvShow(): TmdbTvShow {
        return TmdbTvShow(
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

    private fun TmdbMultiSearchResult.toTmdbPerson(): TmdbPerson =
        TmdbPerson(adultContent, id, knownFor.toSpecificEntities(), name, popularity, profilePath)

    @ToJson
    fun toJson(entity: List<TmdbMultiSearchModel>): List<TmdbMultiSearchResult> {
        throw UnsupportedOperationException()
    }
}
