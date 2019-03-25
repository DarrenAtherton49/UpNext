package com.atherton.upnext.presentation.features.content

import com.atherton.upnext.domain.model.*
import com.atherton.upnext.presentation.common.detail.ModelDetailSection
import com.atherton.upnext.presentation.common.searchmodel.buildBackdropPath
import com.atherton.upnext.presentation.common.searchmodel.buildPosterPath
import com.atherton.upnext.presentation.common.searchmodel.buildProfilePath
import com.atherton.upnext.presentation.util.AppStringProvider
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

internal fun buildContentDetailSections(watchable: Watchable, appStringProvider: AppStringProvider): List<ModelDetailSection> {
    return when (watchable) {
        is TvShow -> {
            buildContentDetailSections(
                appStringProvider = appStringProvider,
                cast = watchable.detail?.cast,
                crew = watchable.detail?.crew,
                genres = watchable.detail?.genres,
                overview = watchable.overview,
                recommendations = watchable.detail?.recommendations,
                releaseDate = watchable.firstAirDate,
                runtime = watchable.detail?.runTimes?.formatRunTimes(),
                seasons = watchable?.detail?.seasons,
                videos = watchable.detail?.videos,
                voteAverage = watchable.voteAverage
            )
        }
        is Movie -> {
            buildContentDetailSections(
                appStringProvider = appStringProvider,
                cast = watchable.detail?.cast,
                crew = watchable.detail?.crew,
                genres = watchable.detail?.genres,
                overview = watchable.overview,
                recommendations = watchable.detail?.recommendations,
                releaseDate = watchable.releaseDate,
                runtime = watchable.detail?.runtime.toString(),
                seasons = null,
                videos = watchable.detail?.videos,
                voteAverage = watchable.voteAverage
            )
        }
    }
}

private fun buildContentDetailSections(
    appStringProvider: AppStringProvider,
    cast: List<CastMember>?,
    crew: List<CrewMember>?,
    genres: List<Genre>?,
    overview: String?,
    recommendations: List<Watchable>?,
    releaseDate: String?,
    runtime: String?,
    seasons: List<Season>?,
    videos: List<Video>?,
    voteAverage: Float?
): List<ModelDetailSection> {
    val sectionList = mutableListOf<ModelDetailSection>()

    val showInfoPanel: Boolean = releaseDate != null && runtime != null && voteAverage != null
    if (showInfoPanel) {
        //todo Also factor in user region when calling API - think there are different 'releases' for each region
        //val releaseDateFormatted: String? = formatFullDateForDetailScreen(movie.releaseDate)
        val releaseYear: String? = formatYearForDetailScreen(releaseDate)
        val runtimeMins: String? = runtime?.let { appStringProvider.getRuntimeString(it) }
        val rating: String? = voteAverage?.let { appStringProvider.getVoteAverageString(it.toString()) }
        val showFirstDivider = runtime != null && releaseYear != null
        sectionList.add(
            ModelDetailSection.InfoPanel(
                releaseDate = releaseYear,
                runtime = runtimeMins,
                voteAverage = rating,
                showFirstDivider = showFirstDivider,
                showSecondDivider = showFirstDivider && rating != null
            )
        )
    }

    overview?.let { sectionList.add(ModelDetailSection.Overview(it)) }

    //todo add ratings

    if (seasons != null && seasons.isNotEmpty()) {
        sectionList.add(ModelDetailSection.Seasons(appStringProvider.getSeasonsHeader(), seasons))
    }
    if (genres != null && genres.isNotEmpty()) {
        sectionList.add(ModelDetailSection.Genres(genres.sortedBy { genre -> genre.name }))
    }
    if (videos != null && videos.isNotEmpty()) {
        sectionList.add(ModelDetailSection.Videos(appStringProvider.getVideosHeader(), videos))
    }
    //todo add photos

    if (cast != null && cast.isNotEmpty()) {
        sectionList.add(ModelDetailSection.Cast(appStringProvider.getCastHeader(), cast))
    }
    if (crew != null && crew.isNotEmpty()) {
        sectionList.add(ModelDetailSection.Crew(appStringProvider.getCrewHeader(), crew))
    }
    if (recommendations != null && recommendations.isNotEmpty()) {
        sectionList.add(ModelDetailSection.RecommendedItems(appStringProvider.getRecommendedContentHeader(), recommendations))
    }
    //todo add creator for tv shows? - pass null into this function for movies
    //todo add reviews
    //todo add comments
    //todo add external links

    return sectionList
}

private fun formatFullDateForDetailScreen(dateString: String?): String? {
    return if (dateString != null) {
        val oldFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val newFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        try {
            val date: Date = oldFormat.parse(dateString)
            newFormat.format(date)
        } catch (exception: ParseException) {
            dateString
        }
    } else dateString
}

private fun formatYearForDetailScreen(dateString: String?): String? {
    return if (dateString != null && dateString.length > 4) {
        dateString.take(4)
    } else dateString
}

private fun List<Int>.formatRunTimes(): String? {
    return when {
        this.isNotEmpty() -> {
            val endOfRunTimes = this.size - 1
            buildString {
                this@formatRunTimes.forEachIndexed { index, runtime ->
                    append(runtime)
                    if (index != endOfRunTimes) {
                        append(", ")
                    }
                }
            }
        }
        else -> null
    }
}

internal fun Watchable.withContentDetailImageUrls(config: Config): Watchable {
    return when (this) {
        is TvShow -> this.withTvDetailImageUrls(config)
        is Movie -> this.witMovieDetailImageUrls(config)
    }
}

private fun TvShow.withTvDetailImageUrls(config: Config): TvShow {
    // only perform copy if the image paths actually exist
    return if (backdropPath != null || posterPath != null) {
        val backdropPath = buildBackdropPath(backdropPath, config)
        this.copy(
            backdropPath = backdropPath,
            posterPath = buildPosterPath(posterPath, config),
            detail = detail?.copy(
                cast = detail.cast?.map { castMember ->
                    castMember.copy(profilePath = buildProfilePath(castMember.profilePath, config))
                },
                crew = detail.crew?.map { crewMember ->
                    crewMember.copy(profilePath = buildProfilePath(crewMember.profilePath, config))
                },
                recommendations = detail.recommendations?.map { watchable -> watchable.withPosterPath(config) },
                videos = detail.videos?.map { video -> video.copy(thumbnail = backdropPath) }
            )
        )
    } else this
}

private fun Movie.witMovieDetailImageUrls(config: Config): Movie {
    // only perform copy if the image paths actually exist
    return if (backdropPath != null || posterPath != null) {
        val backdropPath = buildBackdropPath(backdropPath, config)
        this.copy(
            backdropPath = backdropPath,
            posterPath = buildPosterPath(posterPath, config),
            detail = detail?.copy(
                cast = detail.cast?.map { castMember ->
                    castMember.copy(profilePath = buildProfilePath(castMember.profilePath, config))
                },
                crew = detail.crew?.map { crewMember ->
                    crewMember.copy(profilePath = buildProfilePath(crewMember.profilePath, config))
                },
                recommendations = detail.recommendations?.map { watchable -> watchable.withPosterPath(config) },
                videos = detail.videos?.map { video -> video.copy(thumbnail = backdropPath) }
            )
        )
    } else this
}

private fun Watchable.withPosterPath(config: Config): Watchable {
    return when (this) {
        is TvShow -> this.copy(posterPath = buildPosterPath(posterPath, config))
        is Movie -> this.copy(posterPath = buildPosterPath(posterPath, config))
    }
}
