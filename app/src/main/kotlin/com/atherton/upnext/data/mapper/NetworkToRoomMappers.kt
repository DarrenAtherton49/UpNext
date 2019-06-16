package com.atherton.upnext.data.mapper

import com.atherton.upnext.data.db.model.config.RoomConfig
import com.atherton.upnext.data.db.model.movie.*
import com.atherton.upnext.data.db.model.search.RoomSearchKnownFor
import com.atherton.upnext.data.db.model.search.RoomSearchResult
import com.atherton.upnext.data.db.model.tv.*
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
            id = id.toLong(),
            mediaType = mediaType,
            name = name,
            originalLanguage = originalLanguage,
            overview = overview,
            popularity = popularity,
            posterPath = posterPath,
            releaseDate = releaseDate,
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
            id = id.toLong(),
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

fun List<TmdbTvShow>.toRoomTvShows(isModelComplete: Boolean): List<RoomTvShow> {
    return this.map { it.toRoomTvShow(isModelComplete) }
}

fun TmdbTvShow.toRoomTvShow(isModelComplete: Boolean): RoomTvShow {
    return RoomTvShow(
        backdropPath = backdropPath,
        firstAirDate = firstAirDate,
        id = id.toLong(),
        name = name,
        originCountries = originCountries,
        originalLanguage = originalLanguage,
        originalName = originalName,
        overview = overview,
        posterPath = posterPath,
        popularity = popularity,
        voteAverage = voteAverage,
        voteCount = voteCount,
        runTimes = runTimes,
        homepage = homepage,
        inProduction = inProduction,
        languages = languages,
        lastAirDate = lastAirDate,
        lastEpisodeToAir = lastEpisodeToAir?.toRoomLastEpisodeToAir(),
        numberOfEpisodes = numberOfEpisodes,
        numberOfSeasons = numberOfSeasons,
        status = status,
        type = type,
        isModelComplete = isModelComplete
    )
}

fun List<TmdbGenre>.toRoomMovieGenres(movieId: Long): List<RoomMovieGenre> {
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

fun List<TmdbCastMember>.toRoomMovieCast(movieId: Long): List<RoomMovieCastMember> {
    return this.mapNotNull { castMember ->
        if (castMember.id != null) {
            RoomMovieCastMember(
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

fun List<TmdbCrewMember>.toRoomMovieCrew(movieId: Long): List<RoomMovieCrewMember> {
    return this.mapNotNull { crewMember ->
        if (crewMember.id != null) {
            RoomMovieCrewMember(
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

fun List<TmdbProductionCompany>.toRoomProductionCompanies(movieId: Long): List<RoomMovieProductionCompany> {
    return this.mapNotNull { productionCompany ->
        if (productionCompany.id != null) {
            RoomMovieProductionCompany(
                id = productionCompany.id.toLong(),
                logoPath = productionCompany.logoPath,
                name = productionCompany.name,
                originCountry = productionCompany.originCountry,
                movieId = movieId
            )
        } else null
    }
}

fun List<TmdbProductionCountry>.toRoomMovieProductionCountries(movieId: Long): List<RoomProductionCountry> {
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
fun List<TmdbVideo>.toRoomMovieVideos(movieId: Long): List<RoomMovieVideo> {
    return this
        .filter { it.site == "YouTube"}
        .sortedByDescending { it.type == "Trailer" }
        .mapNotNull { video ->
            if (video.id != null && video.key != null) {
                RoomMovieVideo(
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

// filter out any videos that don't have an id and a key as we won't be able to play them anyway
fun List<TmdbVideo>.toRoomTvShowVideos(showId: Long): List<RoomTvShowVideo> {
    return this
        .filter { it.site == "YouTube"}
        .sortedByDescending { it.type == "Trailer" }
        .mapNotNull { video ->
            if (video.id != null && video.key != null) {
                RoomTvShowVideo(
                    id = video.id,
                    key = video.key,
                    name = video.name,
                    site = video.site,
                    size = video.size,
                    type = video.type,
                    showId = showId
                )
            } else null
        }
}

fun List<TmdbSeason>.toRoomTvSeasons(showId: Long): List<RoomTvShowSeason> {
    return this.mapNotNull { season ->
        if (season.id != null) {
            RoomTvShowSeason(
                airDate = season.airDate,
                episodeCount = season.episodeCount,
                id = season.id.toLong(),
                name = season.name,
                overview = season.overview,
                posterPath = season.posterPath,
                seasonNumber = season.seasonNumber,
                showId = showId
            )
        } else null
    }
}

fun List<TmdbTvNetwork>.toRoomTvNetworks(showId: Long): List<RoomTvShowNetwork> {
    return this.mapNotNull { network ->
        if (network.id != null) {
            RoomTvShowNetwork(
                headquarters = network.headquarters,
                homepage = network.homepage,
                id = network.id.toLong(),
                name = network.name,
                originCountry = network.originCountry,
                showId = showId
            )
        } else null
    }
}

fun List<TmdbCastMember>.toRoomTvShowCast(showId: Long): List<RoomTvShowCastMember> {
    return this.mapNotNull { castMember ->
        if (castMember.id != null) {
            RoomTvShowCastMember(
                castId = castMember.castId,
                character = castMember.character,
                creditId = castMember.creditId,
                gender = castMember.gender,
                id = castMember.id.toLong(),
                name = castMember.name,
                order = castMember.order,
                profilePath = castMember.profilePath,
                showId = showId
            )
        } else null
    }
}

fun List<TmdbCrewMember>.toRoomTvShowCrew(showId: Long): List<RoomTvShowCrewMember> {
    return this.mapNotNull { crewMember ->
        if (crewMember.id != null) {
            RoomTvShowCrewMember(
                creditId = crewMember.creditId,
                department = crewMember.department,
                gender = crewMember.gender,
                id = crewMember.id.toLong(),
                job = crewMember.job,
                name = crewMember.name,
                profilePath = crewMember.profilePath,
                showId = showId
            )
        } else null
    }
}

fun List<TmdbProductionCompany>.toRoomTvProductionCompanies(showId: Long): List<RoomTvShowProductionCompany> {
    return this.mapNotNull { productionCompany ->
        if (productionCompany.id != null) {
            RoomTvShowProductionCompany(
                id = productionCompany.id.toLong(),
                logoPath = productionCompany.logoPath,
                name = productionCompany.name,
                originCountry = productionCompany.originCountry,
                showId = showId
            )
        } else null
    }
}

fun List<TmdbGenre>.toRoomTvShowGenres(showId: Long): List<RoomTvShowGenre> {
    return this.mapNotNull { genre ->
        if (genre.id != null) {
            RoomTvShowGenre(
                id = genre.id.toLong(),
                name = genre.name,
                showId = showId
            )
        } else null
    }
}

private fun TmdbTvLastEpisodeToAir.toRoomLastEpisodeToAir(): RoomTvShowLastEpisodeToAir? {
    return if (id != null) {
        RoomTvShowLastEpisodeToAir(
            airDate = airDate,
            episodeNumber = episodeNumber,
            id = id.toLong(),
            name = name,
            overview = overview,
            productionCode = productionCode,
            seasonNumber = seasonNumber,
            showId = showId,
            stillPath = stillPath,
            voteAverage = voteAverage,
            voteCount = voteCount
        )
    } else null
}

fun List<TmdbTvCreatedBy>.toRoomTvShowCreatedBy(showId: Long): List<RoomTvShowCreatedBy> {
    return this.mapNotNull { createdBy ->
        if (createdBy.id != null) {
            RoomTvShowCreatedBy(
                id = createdBy.id.toLong(),
                creditId = createdBy.creditId,
                name = createdBy.name,
                gender = createdBy.gender,
                profilePath = createdBy.profilePath,
                showId = showId
            )
        } else null
    }
}

fun TmdbConfiguration.toRoomConfig(): RoomConfig {
    with(images) {
        return RoomConfig(
            backdropSizes = backdropSizes,
            baseUrl = baseUrl,
            logoSizes = logoSizes,
            posterSizes = posterSizes,
            profileSizes = profileSizes,
            secureBaseUrl = secureBaseUrl,
            stillSizes = stillSizes
        )
    }
}
