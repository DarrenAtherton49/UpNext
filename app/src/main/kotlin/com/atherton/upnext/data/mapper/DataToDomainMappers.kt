package com.atherton.upnext.data.mapper

import com.atherton.upnext.data.local.AppSettings
import com.atherton.upnext.data.model.*
import com.atherton.upnext.domain.model.*
import com.atherton.upnext.domain.model.Collection

/*
 * A collection of extension functions to map from network models to app-level/domain models.
 */

/**
 * Maps a NetworkResponse to a domain Result.
 *
 * @param cachedData whether or not the data is old/cached
 * @param dataMapper provides a way to map from data layer models to app-level/domain models
 *
 */
internal fun <DATA : Any, DOMAIN : Any> NetworkResponse<DATA, TmdbApiError>.toDomainResponse(
    cachedData: Boolean,
    dataMapper: (DATA) -> DOMAIN
): Response<DOMAIN> {
    return when (this) {
        is NetworkResponse.Success -> Response.Success(dataMapper(body), cachedData)
        is NetworkResponse.ServerError<TmdbApiError> -> Response.Failure.ServerError(error?.toDomainApiError(), code)
        is NetworkResponse.NetworkError -> Response.Failure.NetworkError(error)
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
        TvShow.Detail(
            credits?.cast?.toDomainCast(),
            credits?.crew?.toDomainCrew(),
            createdBy?.toDomainTvCreatedBy(),
            runTimes,
            genres?.toDomainGenres(),
            homepage,
            inProduction,
            languages,
            lastAirDate,
            lastEpisodeToAir?.toDomainLastEpisodeToAir(),
            networks?.toDomainNetworks(),
            numberOfEpisodes,
            numberOfSeasons,
            productionCompanies?.toDomainProductionCompanies(),
            seasons?.toDomainSeasons(),
            status,
            type,
            videos?.results?.toDomainVideos()
        ),
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
        Movie.Detail(
            belongsToCollection?.toDomainCollection(),
            budget,
            credits?.cast?.toDomainCast(),
            credits?.crew?.toDomainCrew(),
            genres?.toDomainGenres(),
            homepage,
            imdbId,
            productionCompanies?.toDomainProductionCompanies(),
            productionCountries?.toDomainProductionCountries(),
            revenue,
            runtime,
            similar?.results?.map { it.toDomainMovie() },
            spokenLanguages?.toDomainSpokenLanguages(),
            status,
            tagline,
            videos?.results?.toDomainVideos()
        ),
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
        Person.Detail(
            birthday,
            knownForDepartment,
            deathDay,
            alsoKnownAs,
            gender.toDomainGender(),
            biography,
            placeOfBirth,
            imdbId,
            homepage
        ),
        id,
        knownFor.toDomainSearchModels(),
        name,
        popularity,
        profilePath
    )
}

private fun List<TmdbCastMember>.toDomainCast(): List<CastMember> {
    return this.map {
        CastMember(it.castId, it.character, it.creditId, it.gender.toDomainGender(), it.id, it.name, it.order, it.profilePath)
    }
}

private fun List<TmdbCrewMember>.toDomainCrew(): List<CrewMember> {
    return this.map {
        CrewMember(it.creditId, it.department, it.gender.toDomainGender(), it.id, it.job, it.name, it.profilePath)
    }
}

private fun TmdbCollection.toDomainCollection(): Collection = Collection(backdropPath, id, name, posterPath)

private fun List<TmdbGenre>.toDomainGenres(): List<Genre> = this.map { Genre(it.id, it.name) }

private fun List<TmdbProductionCompany>.toDomainProductionCompanies(): List<ProductionCompany> {
    return this.map { ProductionCompany(it.id, it.logoPath, it.name, it.originCountry) }
}

private fun List<TmdbProductionCountry>.toDomainProductionCountries(): List<ProductionCountry> {
    return this.map { ProductionCountry(it.iso31661, it.name) }
}

private fun List<TmdbSpokenLanguage>.toDomainSpokenLanguages(): List<SpokenLanguage> {
    return this.map { SpokenLanguage(it.iso6391, it.name) }
}

private fun List<TmdbVideo>.toDomainVideos(): List<Video> {
    return this
        .filter { it.site == "YouTube" }
        .sortedByDescending { it.type == "Trailer" }
        .map { Video(it.id, it.key, it.name, it.site, it.size.toVideoSize(), null, it.type) }
}

private fun TmdbTvCreatedBy.toDomainTvCreatedBy(): TvCreatedBy {
    return TvCreatedBy(id, creditId, name, gender.toDomainGender(), profilePath)
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

private fun TmdbTvLastEpisodeToAir.toDomainLastEpisodeToAir(): TvLastEpisodeToAir {
    return TvLastEpisodeToAir(
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
}

private fun List<TmdbTvNetwork>.toDomainNetworks(): List<TvNetwork> {
    return this.map { TvNetwork(it.headquarters, it.homepage, it.id, it.name, it.originCountry) }
}

private fun List<TmdbSeason>.toDomainSeasons(): List<Season> {
    return this.map { Season(it.airDate, it.episodeCount, it.id, it.name, it.overview, it.posterPath, it.seasonNumber) }
}

fun AppSettings.DiscoverViewToggleSetting.toDomainToggleMode(): SearchModelViewMode {
    return when (this) {
        is AppSettings.DiscoverViewToggleSetting.Grid -> SearchModelViewMode.Grid
        is AppSettings.DiscoverViewToggleSetting.List -> SearchModelViewMode.List
    }
}
