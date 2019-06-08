package com.atherton.upnext.data.mapper

import com.atherton.upnext.data.network.model.*
import com.atherton.upnext.domain.model.*
import com.atherton.upnext.domain.model.Collection

/*
 * A collection of extension functions to map from network models to app-level/domain models.
 *
 * NOTE - When mapping from a network model to a domain model, we filter out any objects which have a null id.
 */

/**
 * Maps a NetworkResponse to a domain LceResponse.
 *
 * @param cachedData whether or not the data is old/cached
 * @param fallbackData fallback data to be emitted in the event of an error
 * @param dataMapper provides a way to map from data layer models to app-level/domain models
 *
 */
@Deprecated("Deprecated")
internal fun <NETWORK : Any, DOMAIN : Any> NetworkResponse<NETWORK, TmdbApiError>.toDomainLceResponse(
    cachedData: Boolean = false,
    fallbackData: DOMAIN? = null,
    dataMapper: (NETWORK) -> DOMAIN
): LceResponse<DOMAIN> {
    return when (this) {
        is NetworkResponse.Success -> LceResponse.Content(dataMapper(body), cachedData)
        is NetworkResponse.ServerError<TmdbApiError> -> LceResponse.Error.ServerError(error?.toDomainApiError(), code, fallbackData)
        is NetworkResponse.NetworkError -> LceResponse.Error.NetworkError(error, fallbackData)
    }
}

internal fun <NETWORK : Any, DOMAIN : Any> NetworkResponse<NETWORK, TmdbApiError>.toDomainLceResponse(
    data: DOMAIN,
    fallbackData: DOMAIN? = data
): LceResponse<DOMAIN> {
    return when (this) {
        is NetworkResponse.Success -> LceResponse.Content(data, false)
        is NetworkResponse.ServerError<TmdbApiError> -> LceResponse.Error.ServerError(error?.toDomainApiError(), code, fallbackData)
        is NetworkResponse.NetworkError -> LceResponse.Error.NetworkError(error, fallbackData)
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

internal fun List<TmdbMultiSearchModel>?.toDomainSearchables(): List<Searchable> {
    return this?.map { searchResult: TmdbMultiSearchModel ->
        when (searchResult) {
            is TmdbTvShow -> searchResult.toDomainTvShow()
            is TmdbMovie -> searchResult.toDomainMovie()
            is TmdbPerson -> searchResult.toDomainPerson()
        }
    } ?: emptyList()
}

internal fun List<TmdbMultiSearchModel>?.toDomainWatchables(): List<Watchable> {
    return this?.mapNotNull { searchResult ->
        when (searchResult) {
            is TmdbTvShow -> searchResult.toDomainTvShow()
            is TmdbMovie -> searchResult.toDomainMovie()
            is TmdbPerson -> null
        }
    }?.filterIsInstance(Watchable::class.java) ?: emptyList()
}

fun TmdbTvShow.toDomainTvShow(): TvShow {
    return TvShow(
        backdropPath = backdropPath,
        detail = TvShow.Detail(
            cast = credits?.cast?.toDomainCast(),
            crew = credits?.crew?.toDomainCrew(),
            createdBy = createdBy?.toDomainTvCreatedBy(),
            runTimes = runTimes,
            genres = genres?.toDomainGenres(),
            homepage = homepage,
            inProduction = inProduction,
            languages = languages,
            lastAirDate = lastAirDate,
            lastEpisodeToAir = lastEpisodeToAir?.toDomainLastEpisodeToAir(),
            networks = networks?.toDomainNetworks(),
            numberOfEpisodes = numberOfEpisodes,
            numberOfSeasons = numberOfSeasons,
            productionCompanies = productionCompanies?.toDomainProductionCompanies(),
            recommendations = recommendations?.results?.map { it.toDomainTvShow() },
            seasons = seasons?.toDomainSeasons(),
            status = status,
            type = type,
            videos = videos?.results?.toDomainVideos()
        ),
        firstAirDate = firstAirDate,
        id = id,
        name = name,
        originalLanguage = originalLanguage,
        originalName = originalName,
        overview = overview,
        posterPath = posterPath,
        popularity = popularity,
        tmdbId = id,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

fun TmdbMovie.toDomainMovie(): Movie {
    return Movie(
        adultContent = adultContent,
        backdropPath = backdropPath,
        detail = Movie.Detail(
            belongsToCollection = belongsToCollection?.toDomainCollection(),
            budget = budget,
            cast = credits?.cast?.toDomainCast(),
            crew = credits?.crew?.toDomainCrew(),
            genres = genres?.toDomainGenres(),
            homepage = homepage,
            imdbId = imdbId,
            productionCompanies = productionCompanies?.toDomainProductionCompanies(),
            productionCountries = productionCountries?.toDomainProductionCountries(),
            revenue = revenue,
            runtime = runtime,
            recommendations = recommendations?.results?.map { it.toDomainMovie() },
            spokenLanguages = spokenLanguages?.toDomainSpokenLanguages(),
            status = status,
            tagline = tagline,
            videos = videos?.results?.toDomainVideos()
        ),
        id = id,
        originalLanguage = originalLanguage,
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
}

fun TmdbPerson.toDomainPerson(): Person {
    return Person(
        adultContent = adultContent,
        detail = Person.Detail(
            birthday = birthday,
            knownForDepartment = knownForDepartment,
            deathDay = deathDay,
            alsoKnownAs = alsoKnownAs,
            gender = gender.toDomainGender(),
            biography = biography,
            placeOfBirth = placeOfBirth,
            imdbId = imdbId,
            homepage = homepage
        ),
        id = id,
        knownFor = knownFor.toDomainWatchables(),
        name = name,
        popularity = popularity,
        profilePath = profilePath,
        tmdbId = id
    )
}

private fun List<TmdbCastMember>.toDomainCast(): List<CastMember> {
    return this.mapNotNull {
        if (it.id != null) {
            CastMember(it.castId, it.character, it.creditId, it.gender.toDomainGender(), it.id, it.name, it.order, it.profilePath)
        } else null
    }
}

private fun List<TmdbCrewMember>.toDomainCrew(): List<CrewMember> {
    return this.mapNotNull {
        if (it.id != null) {
            CrewMember(it.creditId, it.department, it.gender.toDomainGender(), it.id, it.job, it.name, it.profilePath)
        } else null
    }
}

private fun TmdbCollection.toDomainCollection(): Collection? {
    return if (id != null) {
        Collection(backdropPath, id, name, posterPath)
    } else null
}

private fun List<TmdbGenre>.toDomainGenres(): List<Genre> {
    return this.mapNotNull {
        if (it.id != null) {
            Genre(it.id, it.name)
        } else null
    }
}

private fun List<TmdbProductionCompany>.toDomainProductionCompanies(): List<ProductionCompany> {
    return this.mapNotNull {
        if (it.id != null) {
            ProductionCompany(it.id, it.logoPath, it.name, it.originCountry)
        } else null
    }
}

private fun List<TmdbProductionCountry>.toDomainProductionCountries(): List<ProductionCountry> {
    return this.map { ProductionCountry(it.iso31661, it.name) }
}

private fun List<TmdbSpokenLanguage>.toDomainSpokenLanguages(): List<SpokenLanguage> {
    return this.map { SpokenLanguage(it.iso6391, it.name) }
}

// filter out any videos that don't have an id and a key as we won't be able to play them anyway
private fun List<TmdbVideo>.toDomainVideos(): List<Video> {
    return this
        .filter { it.site == "YouTube"}
        .sortedByDescending { it.type == "Trailer" }
        .mapNotNull {
            if (it.id != null && it.key != null) {
                Video(it.id, it.key, it.name, it.site, it.size.toVideoSize(), null, it.type)
            } else null
        }
}

private fun List<TmdbTvCreatedBy>.toDomainTvCreatedBy(): List<TvCreatedBy> {
    return this.mapNotNull {
        if (it.id != null) {
            TvCreatedBy(
                it.id,
                it.creditId,
                it.name,
                it.gender.toDomainGender(),
                it.profilePath
            )
        } else null
    }
}

private fun Int?.toDomainGender(): Gender {
    return when (this) {
        1 -> Gender.Female
        2 -> Gender.Male
        else -> Gender.Unknown
    }
}

private fun Int?.toVideoSize(): VideoSize {
    return when (this) {
        360 -> VideoSize.V360
        480 -> VideoSize.V480
        720 -> VideoSize.V720
        1080 -> VideoSize.V1080
        else -> VideoSize.Unknown
    }
}

private fun TmdbTvLastEpisodeToAir.toDomainLastEpisodeToAir(): TvLastEpisodeToAir? {
    return if (id != null) {
        TvLastEpisodeToAir(
            airDate,
            episodeNumber,
            id,
            name,
            overview,
            productionCode,
            seasonNumber,
            showId,
            stillPath,
            voteAverage,
            voteCount
        )
    } else null
}

private fun List<TmdbTvNetwork>.toDomainNetworks(): List<TvNetwork> {
    return this.mapNotNull {
        if (it.id != null) {
            TvNetwork(it.headquarters, it.homepage, it.id, it.name, it.originCountry)
        } else null
    }
}

private fun List<TmdbSeason>.toDomainSeasons(): List<Season> {
    return this.mapNotNull {
        if (it.id != null) {
            Season(it.airDate, it.episodeCount, it.id, it.name, it.overview, it.posterPath, it.seasonNumber)
        } else null
    }
}
