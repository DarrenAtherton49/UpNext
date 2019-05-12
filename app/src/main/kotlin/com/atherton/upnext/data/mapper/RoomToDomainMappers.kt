package com.atherton.upnext.data.mapper

import com.atherton.upnext.data.db.model.RoomSearchKnownFor
import com.atherton.upnext.data.db.model.RoomSearchResult
import com.atherton.upnext.domain.model.*

private const val MOVIE = "movie"
private const val TV = "tv"
private const val PERSON = "person"

fun List<Pair<RoomSearchResult, List<RoomSearchKnownFor>>>.toDomainSearchables(): List<Searchable> {
    return this.map { searchResultAndKnownFor ->
        val (searchResult, knownForList) = searchResultAndKnownFor
        when (searchResult.mediaType) {
            TV -> searchResult.toDomainTvShow()
            MOVIE -> searchResult.toDomainMovie()
            PERSON -> searchResult.toDomainPerson(knownForList)
            else -> throw IllegalArgumentException("media_type should be either 'movie', 'tv' or 'person'.")
        }
    }
}

private fun List<RoomSearchKnownFor>?.toDomainWatchables(): List<Watchable> {
    return this?.mapNotNull { searchResult ->
        when (searchResult.mediaType) {
            TV -> searchResult.toDomainTvShow()
            MOVIE -> searchResult.toDomainMovie()
            else -> null
        }
    }?.filterIsInstance(Watchable::class.java) ?: emptyList()
}

private fun RoomSearchResult.toDomainTvShow(): TvShow {
    return TvShow(
        backdropPath = backdropPath,
        detail = null,
        firstAirDate = firstAirDate,
        id = id.toInt(),
        name = name,
        originalLanguage = originalLanguage,
        originalName = originalName,
        overview = overview,
        posterPath = posterPath,
        popularity = popularity,
        tmdbId = tmdbId,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

private fun RoomSearchResult.toDomainMovie(): Movie {
    return Movie(
        adultContent = adultContent,
        backdropPath = backdropPath,
        detail = null,
        id = id.toInt(),
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = title,
        tmdbId = tmdbId,
        video = video,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

private fun RoomSearchResult.toDomainPerson(knownForList: List<RoomSearchKnownFor>?): Person {
    return Person(
        adultContent = adultContent,
        detail = null,
        id = id.toInt(),
        knownFor = knownForList.toDomainWatchables(),
        name = name,
        popularity = popularity,
        profilePath = profilePath,
        tmdbId = tmdbId
    )
}

private fun RoomSearchKnownFor.toDomainTvShow(): TvShow {
    return TvShow(
        backdropPath = backdropPath,
        detail = null,
        firstAirDate = firstAirDate,
        id = id,
        name = name,
        originalLanguage = originalLanguage,
        originalName = originalName,
        overview = overview,
        posterPath = posterPath,
        popularity = popularity,
        tmdbId = tmdbId,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

private fun RoomSearchKnownFor.toDomainMovie(): Movie {
    return Movie(
        adultContent = adultContent,
        backdropPath = backdropPath,
        detail = null,
        id = id,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = title,
        tmdbId = tmdbId,
        video = video,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}
