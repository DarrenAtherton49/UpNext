package com.atherton.upnext.data.mapper

import com.atherton.upnext.data.db.model.movie.*
import com.atherton.upnext.data.db.model.search.RoomSearchKnownFor
import com.atherton.upnext.data.db.model.search.RoomSearchResult
import com.atherton.upnext.data.network.model.*


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

fun List<TmdbMovie>.toRoomMovies(isModelComplete: Boolean): List<RoomMovie> {
    return this.map { it.toRoomMovie(isModelComplete) }
}

fun TmdbMovie.toRoomMovie(isModelComplete: Boolean): RoomMovie {
    return RoomMovie(
        adultContent = adultContent,
        backdropPath = backdropPath,
        id = id.toLong(),
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = title,
        video = video,
        voteAverage = voteAverage,
        voteCount = voteCount,
        belongsToCollection = belongsToCollection?.toRoomCollection(),
        budget = budget,
        homepage = homepage,
        imdbId = imdbId,
        revenue = revenue,
        runtime = runtime,
        status = status,
        tagline = tagline,
        isModelComplete = isModelComplete
    )
}

fun List<TmdbGenre>.toRoomGenres(movieId: Long): List<RoomMovieGenre> {
    return this.mapNotNull { genre ->
        if (genre.id != null) {
            RoomMovieGenre(
                id = genre.id.toLong(),
                name = genre.name,
                movieId = movieId
            )
        } else null
    }
}

private fun TmdbCollection.toRoomCollection(): RoomMovieCollection? {
    return if (id != null) {
        RoomMovieCollection(
            backdropPath = backdropPath,
            id = id.toLong(),
            name = name,
            posterPath = posterPath
        )
    } else null
}

fun List<TmdbCastMember>.toRoomCast(movieId: Long): List<RoomCastMember> {
    return this.mapNotNull { castMember ->
        if (castMember.id != null) {
            RoomCastMember(
                castId = castMember.castId,
                character = castMember.character,
                creditId = castMember.creditId,
                gender = castMember.gender,
                id = castMember.id.toLong(),
                name = castMember.name,
                order = castMember.order,
                profilePath = castMember.profilePath,
                movieId = movieId
            )
        } else null
    }
}

fun List<TmdbCrewMember>.toRoomCrew(movieId: Long): List<RoomCrewMember> {
    return this.mapNotNull { crewMember ->
        if (crewMember.id != null) {
            RoomCrewMember(
                creditId = crewMember.creditId,
                department = crewMember.department,
                gender = crewMember.gender,
                id = crewMember.id.toLong(),
                job = crewMember.job,
                name = crewMember.name,
                profilePath = crewMember.profilePath,
                movieId = movieId
            )
        } else null
    }
}

fun List<TmdbProductionCompany>.toRoomProductionCompanies(movieId: Long): List<RoomProductionCompany> {
    return this.mapNotNull { productionCompany ->
        if (productionCompany.id != null) {
            RoomProductionCompany(
                id = productionCompany.id.toLong(),
                logoPath = productionCompany.logoPath,
                name = productionCompany.name,
                originCountry = productionCompany.originCountry,
                movieId = movieId
            )
        } else null
    }
}

fun List<TmdbProductionCountry>.toRoomProductionCountries(movieId: Long): List<RoomProductionCountry> {
    return this.map { productionCountry ->
        RoomProductionCountry(
            iso31661 = productionCountry.iso31661,
            name = productionCountry.name,
            movieId = movieId
        )
    }
}

fun List<TmdbSpokenLanguage>.toRoomSpokenLanguages(movieId: Long): List<RoomSpokenLanguage> {
    return this.map { spokenLanguage ->
        RoomSpokenLanguage(
            iso6391 = spokenLanguage.iso6391,
            name = spokenLanguage.name,
            movieId = movieId
        )
    }
}

// filter out any videos that don't have an id and a key as we won't be able to play them anyway
fun List<TmdbVideo>.toRoomVideos(movieId: Long): List<RoomVideo> {
    return this
        .filter { it.site == "YouTube"}
        .sortedByDescending { it.type == "Trailer" }
        .mapNotNull { video ->
            if (video.id != null && video.key != null) {
                RoomVideo(
                    id = video.id,
                    key = video.key,
                    name = video.name,
                    site = video.site,
                    size = video.size,
                    type = video.type,
                    movieId = movieId
                )
            } else null
        }
}
