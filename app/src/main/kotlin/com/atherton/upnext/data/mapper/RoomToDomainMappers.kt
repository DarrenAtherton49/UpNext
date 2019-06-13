package com.atherton.upnext.data.mapper

import com.atherton.upnext.data.db.model.movie.*
import com.atherton.upnext.data.db.model.search.RoomSearchKnownFor
import com.atherton.upnext.data.db.model.search.RoomSearchResult
import com.atherton.upnext.data.db.model.search.RoomSearchResultWithKnownFor
import com.atherton.upnext.data.db.model.tv.*
import com.atherton.upnext.domain.model.*
import com.atherton.upnext.domain.model.Collection

private const val MOVIE = "movie"
private const val TV = "tv"
private const val PERSON = "person"

fun List<RoomSearchResultWithKnownFor>.toDomainSearchables(): List<Searchable> {
    return this.mapNotNull { searchResultAndKnownFor ->
        val searchResult = searchResultAndKnownFor.searchResult
        val knownFor = searchResultAndKnownFor.knownFor
        searchResult?.let {
            when (searchResult.mediaType) {
                TV -> searchResult.toDomainTvShow()
                MOVIE -> searchResult.toDomainMovie()
                PERSON -> searchResult.toDomainPerson(knownFor)
                else -> throw IllegalArgumentException("media_type should be either '$MOVIE', '$TV' or '$PERSON'.")
            }
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
        id = id,
        name = name,
        originalLanguage = originalLanguage,
        originalName = originalName,
        overview = overview,
        posterPath = posterPath,
        popularity = popularity,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

private fun RoomSearchResult.toDomainMovie(): Movie {
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
        video = video,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

private fun RoomSearchResult.toDomainPerson(knownForList: List<RoomSearchKnownFor>?): Person {
    return Person(
        adultContent = adultContent,
        detail = null,
        id = id,
        knownFor = knownForList.toDomainWatchables(),
        name = name,
        popularity = popularity,
        profilePath = profilePath
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
        video = video,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

fun RoomMovieAllData.toDomainMovie(recommendations: List<RoomMovie>): Movie {
    return this.movie.toDomainMovie(
        cast = cast,
        crew = crew,
        genres = genres,
        productionCompanies = productionCompanies,
        productionCountries = productionCountries,
        spokenLanguages = spokenLanguages,
        recommendations = recommendations,
        videos = videos
    )
}

fun RoomMovie.toDomainMovie(
    cast: List<RoomMovieCastMember>? = null,
    crew: List<RoomMovieCrewMember>? = null,
    genres: List<RoomMovieGenre>? = null,
    productionCompanies: List<RoomMovieProductionCompany>? = null,
    productionCountries: List<RoomProductionCountry>? = null,
    spokenLanguages: List<RoomSpokenLanguage>? = null,
    recommendations: List<RoomMovie>? = null,
    videos: List<RoomMovieVideo>? = null
): Movie {
    return Movie(
        adultContent = adultContent,
        backdropPath = backdropPath,
        detail = Movie.Detail(
            belongsToCollection = belongsToCollection?.toDomainCollection(),
            budget = budget,
            cast = cast?.toDomainMovieCast(),
            crew = crew?.toDomainMovieCrew(),
            genres = genres?.toDomainMovieGenres(),
            homepage = homepage,
            imdbId = imdbId,
            productionCompanies = productionCompanies?.toDomainMovieProductionCompanies(),
            productionCountries = productionCountries?.toDomainProductionCountries(),
            revenue = revenue,
            runtime = runtime,
            recommendations = recommendations?.map { recommendedMovie ->
                recommendedMovie.toDomainMovie(null, null, null, null, null, null, null, null)
            },
            spokenLanguages = spokenLanguages?.toDomainSpokenLanguages(),
            status = status,
            tagline = tagline,
            videos = videos?.toDomainMovieVideos()
        ),
        id = id,
        originalLanguage = originalLanguage,
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
}

private fun RoomMovieCollection.toDomainCollection(): Collection {
    return Collection(
        backdropPath = backdropPath,
        id = id,
        name = name,
        posterPath = posterPath
    )
}

private fun List<RoomMovieCastMember>.toDomainMovieCast(): List<CastMember> {
    return this.map { castMember ->
        CastMember(
            castId = castMember.castId,
            character = castMember.character,
            creditId = castMember.creditId,
            gender = castMember.gender.toDomainGender(),
            id = castMember.id,
            name = castMember.name,
            order = castMember.order,
            profilePath = castMember.profilePath
        )
    }
}

private fun List<RoomMovieCrewMember>.toDomainMovieCrew(): List<CrewMember> {
    return this.map { crewMember ->
        CrewMember(
            creditId = crewMember.creditId,
            department = crewMember.department,
            gender = crewMember.gender.toDomainGender(),
            id = crewMember.id,
            job = crewMember.job,
            name = crewMember.name,
            profilePath = crewMember.profilePath
        )
    }
}

private fun Int?.toDomainGender(): Gender {
    return when (this) {
        1 -> Gender.Female
        2 -> Gender.Male
        else -> Gender.Unknown
    }
}

private fun List<RoomMovieGenre>.toDomainMovieGenres(): List<Genre> {
    return this.map { genre ->
        Genre(
            id = genre.id,
            name = genre.name
        )
    }
}

private fun List<RoomMovieProductionCompany>.toDomainMovieProductionCompanies(): List<ProductionCompany> {
    return this.map { productionCompany ->
        ProductionCompany(
            id = productionCompany.id,
            logoPath = productionCompany.logoPath,
            name = productionCompany.name,
            originCountry = productionCompany.originCountry
        )
    }
}

private fun List<RoomProductionCountry>.toDomainProductionCountries(): List<ProductionCountry> {
    return this.map { productionCountry ->
        ProductionCountry(
            iso31661 = productionCountry.iso31661,
            name = productionCountry.name
        )
    }
}

private fun List<RoomSpokenLanguage>.toDomainSpokenLanguages(): List<SpokenLanguage> {
    return this.map { spokenLanguage ->
        SpokenLanguage(
            iso6391 = spokenLanguage.iso6391,
            name = spokenLanguage.name
        )
    }
}

private fun List<RoomMovieVideo>.toDomainMovieVideos(): List<Video> {
    return this.map { video ->
        Video(
            id = video.id,
            key = video.key,
            name = video.name,
            site = video.site,
            size = video.size.toVideoSize(),
            thumbnail = null,
            type = video.type
        )
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

fun RoomTvShowAllData.toDomainTvShow(recommendations: List<RoomTvShow>): TvShow {
    return this.tvShow.toDomainTvShow(
        cast = cast,
        createdBy = createdBy,
        crew = crew,
        genres = genres,
        productionCompanies = productionCompanies,
        networks = networks,
        seasons = seasons,
        recommendations = recommendations,
        videos = videos
    )
}

fun RoomTvShow.toDomainTvShow(
    cast: List<RoomTvShowCastMember>? = null,
    createdBy: List<RoomTvShowCreatedBy>? = null,
    crew: List<RoomTvShowCrewMember>? = null,
    genres: List<RoomTvShowGenre>? = null,
    productionCompanies: List<RoomTvShowProductionCompany>? = null,
    networks: List<RoomTvShowNetwork>? = null,
    seasons: List<RoomTvShowSeason>? = null,
    recommendations: List<RoomTvShow>? = null,
    videos: List<RoomTvShowVideo>? = null
): TvShow {
    return TvShow(
        backdropPath = backdropPath,
        detail = TvShow.Detail(
            cast = cast?.toDomainTvShowCast(),
            createdBy = createdBy?.toDomainTvShowCreatedBy(),
            crew = crew?.toDomainTvShowCrew(),
            genres = genres?.toDomainTvShowGenres(),
            homepage = homepage,
            inProduction = inProduction,
            languages = languages,
            lastAirDate = lastAirDate,
            lastEpisodeToAir = lastEpisodeToAir?.toDomainLastEpisodeToAir(),
            networks = networks?.toDomainTvNetworks(),
            numberOfEpisodes = numberOfEpisodes,
            numberOfSeasons = numberOfSeasons,
            productionCompanies = productionCompanies?.toDomainTvProductionCompanies(),
            recommendations = recommendations?.map { recommendedTvShow ->
                recommendedTvShow.toDomainTvShow(null, null, null, null, null, null, null , null, null)
            },
            runTimes = runTimes,
            seasons = seasons?.toDomainTvSeasons(),
            status = status,
            type = type,
            videos = videos?.toDomainTvShowVideos()
        ),
        firstAirDate = firstAirDate,
        id = id,
        name = name,
        originalLanguage = originalLanguage,
        originalName = originalName,
        overview = overview,
        posterPath = posterPath,
        popularity = popularity,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

private fun List<RoomTvShowCastMember>.toDomainTvShowCast(): List<CastMember> {
    return this.map { castMember ->
        CastMember(
            castId = castMember.castId,
            character = castMember.character,
            creditId = castMember.creditId,
            gender = castMember.gender.toDomainGender(),
            id = castMember.id,
            name = castMember.name,
            order = castMember.order,
            profilePath = castMember.profilePath
        )
    }
}

private fun List<RoomTvShowCrewMember>.toDomainTvShowCrew(): List<CrewMember> {
    return this.map { crewMember ->
        CrewMember(
            creditId = crewMember.creditId,
            department = crewMember.department,
            gender = crewMember.gender.toDomainGender(),
            id = crewMember.id,
            job = crewMember.job,
            name = crewMember.name,
            profilePath = crewMember.profilePath
        )
    }
}

private fun List<RoomTvShowGenre>.toDomainTvShowGenres(): List<Genre> {
    return this.map { genre ->
        Genre(
            id = genre.id,
            name = genre.name
        )
    }
}

private fun List<RoomTvShowVideo>.toDomainTvShowVideos(): List<Video> {
    return this.map { video ->
        Video(
            id = video.id,
            key = video.key,
            name = video.name,
            site = video.site,
            size = video.size.toVideoSize(),
            thumbnail = null,
            type = video.type
        )
    }
}

private fun List<RoomTvShowCreatedBy>.toDomainTvShowCreatedBy(): List<TvShowCreatedBy> {
    return this.map { createdBy ->
        TvShowCreatedBy(
            id = createdBy.id,
            creditId = createdBy.creditId,
            name = createdBy.name,
            gender = createdBy.gender.toDomainGender(),
            profilePath = createdBy.profilePath
        )
    }
}

private fun RoomTvShowLastEpisodeToAir.toDomainLastEpisodeToAir(): TvShowLastEpisodeToAir {
    return TvShowLastEpisodeToAir(
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
}

private fun List<RoomTvShowNetwork>.toDomainTvNetworks(): List<TvNetwork> {
    return this.map { network ->
        TvNetwork(
            headquarters = network.headquarters,
            homepage = network.homepage,
            id = network.id,
            name = network.name,
            originCountry = network.originCountry
        )
    }
}

private fun List<RoomTvShowProductionCompany>.toDomainTvProductionCompanies(): List<ProductionCompany> {
    return this.map { productionCompany ->
        ProductionCompany(
            id = productionCompany.id,
            logoPath = productionCompany.logoPath,
            name = productionCompany.name,
            originCountry = productionCompany.originCountry
        )
    }
}

private fun List<RoomTvShowSeason>.toDomainTvSeasons(): List<TvSeason> {
    return this.map { season ->
        TvSeason(
            airDate = season.airDate,
            episodeCount = season.episodeCount,
            id = season.id,
            name = season.name,
            overview = season.overview,
            posterPath = season.posterPath,
            seasonNumber = season.seasonNumber
        )
    }
}
