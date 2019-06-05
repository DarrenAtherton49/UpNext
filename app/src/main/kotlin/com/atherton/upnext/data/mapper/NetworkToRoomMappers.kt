package com.atherton.upnext.data.mapper

import com.atherton.upnext.data.db.model.search.RoomSearchKnownFor
import com.atherton.upnext.data.db.model.search.RoomSearchResult
import com.atherton.upnext.data.network.model.TmdbMultiSearchResult


fun List<TmdbMultiSearchResult>.toRoomSearchResults(): List<Pair<RoomSearchResult, List<RoomSearchKnownFor>?>> {

    val list: MutableList<Pair<RoomSearchResult, List<RoomSearchKnownFor>?>> = mutableListOf()
    this.forEach { networkSearchResult ->
        val searchResult = networkSearchResult.toRoomSearchResult()
        val knownFor = networkSearchResult.knownFor?.toRoomKnownFor()

        if (searchResult != null) {
            list.add(Pair(searchResult, knownFor))
        }
    }
    return list
}

private fun TmdbMultiSearchResult.toRoomSearchResult(): RoomSearchResult? {
    return if (id != null && mediaType != null) {
        RoomSearchResult(
            adultContent = adultContent,
            backdropPath = backdropPath,
            mediaType = mediaType,
            name = name,
            originalLanguage = originalLanguage,
            overview = overview,
            popularity = popularity,
            posterPath = posterPath,
            releaseDate = releaseDate,
            tmdbId = id,
            voteAverage = voteAverage,
            voteCount = voteCount,

            // TV Show specific fields
            firstAirDate = firstAirDate,
            originalName = originalName,

            // Movie specific fields
            originalTitle = originalTitle,
            title = title,
            video = video,

            // Person specific fields
            profilePath = profilePath
        )
    } else null
}

fun List<TmdbMultiSearchResult>.toRoomKnownFor(): List<RoomSearchKnownFor> {
    return this.mapNotNull { it.toRoomKnownFor() }
}

private fun TmdbMultiSearchResult.toRoomKnownFor(): RoomSearchKnownFor? {
    return if (id != null && mediaType != null) {
        return RoomSearchKnownFor(
            adultContent = adultContent,
            backdropPath = backdropPath,
            firstAirDate = firstAirDate,
            mediaType = mediaType,
            name = name,
            originalLanguage = originalLanguage,
            originalName = originalName,
            originalTitle = originalTitle,
            overview = overview,
            popularity = popularity,
            posterPath = posterPath,
            releaseDate = releaseDate,
            title = title,
            tmdbId = id,
            video = video,
            voteAverage = voteAverage,
            voteCount = voteCount
        )
    } else null
}
