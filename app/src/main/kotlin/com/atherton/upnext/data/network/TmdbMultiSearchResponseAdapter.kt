package com.atherton.upnext.data.network

import com.atherton.upnext.data.model.*
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class TmdbMultiSearchResponseAdapter {

    /*
     * This signature tells Moshi that when an API call tries to create a List<TmdbMultiSearchModel>,
     * it can use a List<TmdbMultiSearchResult> to do this. This takes the more generic search results
     * and maps them into the correct entities based on the 'media_type' field.
     */
    @FromJson
    fun fromJson(searchResults: List<TmdbMultiSearchResult>): List<TmdbMultiSearchModel>? =
        searchResults.toSpecificEntities()

    @ToJson
    fun toJson(entity: List<TmdbMultiSearchModel>): List<TmdbMultiSearchResult> {
        throw UnsupportedOperationException()
    }

    // filter out any content that for some reason does not have an id. This simplifies the rest of the application
    // code by not having to check every if things have an id.
    private fun List<TmdbMultiSearchResult>?.toSpecificEntities(): List<TmdbMultiSearchModel>? {
        return this?.mapNotNull {
            when (it.mediaType) {
                "movie" -> it.toTmdbMovie()
                "tv" -> it.toTmdbTvShow()
                "person" -> it.toTmdbPerson()
                else -> throw IllegalArgumentException("media_type should be either 'movie', 'tv' or 'person'.")
            }
        }
    }

    private fun TmdbMultiSearchResult.toTmdbMovie(): TmdbMovie? {
        return if (id != null) {
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
                null,
                null,
                null,
                null
            )
        } else null
    }

    private fun TmdbMultiSearchResult.toTmdbTvShow(): TmdbTvShow? {
        return if (id != null) {
            TmdbTvShow(
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
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        } else null
    }

    private fun TmdbMultiSearchResult.toTmdbPerson(): TmdbPerson? {
        return if (id != null) {
            TmdbPerson(
                adultContent,
                id,
                knownFor.toSpecificEntities(),
                name,
                popularity,
                profilePath,
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
        } else null
    }
}
