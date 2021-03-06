package com.atherton.upnext.domain.model

import android.os.Parcelable
import com.atherton.upnext.R
import kotlinx.android.parcel.Parcelize

/*
 * App-level/domain models. All network/data models should be mapped to the below models before use.
 */

@Parcelize
data class ApiError(val statusMessage: String, val statusCode: Int): Parcelable

/**
 * Wrapper to unify the movie and tv results below into one 'type' - useful for 'when' statements etc.
 */
sealed class Watchable(
    open val backdropPath: String?,
    open val id: Long,
    open val posterPath: String?,
    open val state: State,
    open val title: String?
) : Parcelable {

    @Parcelize
    data class State(
        val inWatchlist: Boolean = false,
        val isWatched: Boolean = false
    ) : Parcelable
}

/**
 * Wrapper to unify the movie, tv and person results below into one 'type' for discover and search
 */
interface Searchable : Parcelable {
    val id: Long
    val popularity: Float?
}

@Parcelize
data class TvShow(
    override val backdropPath: String?,
    val detail: Detail?,
    val firstAirDate: String?,
    override val id: Long,
    val name: String?,
    val originalLanguage: String?,
    val originalName: String?,
    val overview: String?,
    override val posterPath: String?,
    override val popularity: Float?,
    override val state: State,
    val voteAverage: Float?,
    val voteCount: Int?
) : Watchable(
    backdropPath = backdropPath,
    id = id,
    posterPath = posterPath,
    state = state,
    title = name
), Searchable {

    @Parcelize
    data class Detail(
        val cast: List<CastMember>?,
        val crew: List<CrewMember>?,
        val createdBy: List<TvShowCreatedBy>?,
        val genres: List<Genre>?,
        val homepage: String?,
        val inProduction: Boolean?,
        val languages: List<String>?,
        val lastAirDate: String?,
        val lastEpisodeToAir: TvShowLastEpisodeToAir?,
        val networks: List<TvNetwork>?,
        val numberOfEpisodes: Int?,
        val numberOfSeasons: Int?,
        val productionCompanies: List<ProductionCompany>?,
        val recommendations: List<Watchable>?,
        val runTimes: List<Int>?,
        val seasons: List<TvSeason>?,
        val status: String?,
        val type: String?,
        val videos: List<Video>?
    ) : Parcelable
}

@Parcelize
data class Movie(
    val adultContent: Boolean?,
    override val backdropPath: String?,
    val detail: Detail?,
    override val id: Long,
    val originalLanguage: String?,
    val originalTitle: String?,
    val overview: String?,
    override val popularity: Float?,
    override val posterPath: String?,
    val releaseDate: String?,
    override val state: State,
    override val title: String?,
    val video: Boolean?,
    val voteAverage: Float?,
    val voteCount: Int?
) : Watchable(
    backdropPath = backdropPath,
    id = id,
    posterPath = posterPath,
    state = state,
    title = title
), Searchable {

    @Parcelize
    data class Detail(
        val belongsToCollection: Collection?,
        val budget: Int?,
        val cast: List<CastMember>?,
        val crew: List<CrewMember>?,
        val genres: List<Genre>?,
        val homepage: String?,
        val imdbId: String?,
        val productionCompanies: List<ProductionCompany>?,
        val productionCountries: List<ProductionCountry>?,
        val revenue: Int?,
        val runtime: Int?,
        val recommendations: List<Watchable>?,
        val spokenLanguages: List<SpokenLanguage>?,
        val status: String?,
        val tagline: String?,
        val videos: List<Video>?
    ) : Parcelable
}

@Parcelize
data class Person(
    val adultContent: Boolean?,
    val detail: Detail?,
    override val id: Long,
    val name: String?,
    override val popularity: Float?,
    val profilePath: String?
) : Searchable {

    @Parcelize
    data class Detail(
        val birthday: String?,
        val knownForDepartment: String?,
        val deathDay: String?,
        val alsoKnownAs: String?,
        val gender: Gender,
        val biography: String?,
        val placeOfBirth: String?,
        val imdbId: String?,
        val homepage: String?,
        val movieCastCredits: List<PersonCredit>,
        val movieCrewCredits: List<PersonCredit>,
        val tvCastCredits: List<PersonCredit>,
        val tvCrewCredits: List<PersonCredit>
    ) : Parcelable
}

@Parcelize
data class PersonCredit(
    val id: Long,
    val posterPath: String,
    val title: String
) : Parcelable

@Parcelize
data class CastMember(
    val castId: Int?,
    val character: String?,
    val creditId: String?,
    val gender: Gender,
    val id: Long,
    val name: String?,
    val order: Int?,
    val profilePath: String?
) : Parcelable

@Parcelize
data class CrewMember(
    val creditId: String?,
    val department: String?,
    val gender: Gender,
    val id: Long,
    val job: String?,
    val name: String?,
    val profilePath: String?
) : Parcelable

@Parcelize
data class Collection(val backdropPath: String?, val id: Long, val name: String?, val posterPath: String?) : Parcelable

@Parcelize
data class Genre(val id: Long, val name: String?) : Parcelable

@Parcelize
data class ProductionCompany(val id: Long, val logoPath: String?, val name: String?, val originCountry: String?) : Parcelable

@Parcelize
data class ProductionCountry(val iso31661: String?, val name: String?) : Parcelable

@Parcelize
data class SpokenLanguage(val iso6391: String?, val name: String?) : Parcelable

@Parcelize
data class TvShowCreatedBy(
    val id: Long,
    val creditId: String?,
    val name: String?,
    val gender: Gender,
    val profilePath: String?
) : Parcelable

//todo refactor this into a proper 'Episode' type when we need a list of episodes?
@Parcelize
data class TvShowLastEpisodeToAir(
    val airDate: String?,
    val episodeNumber: Int?,
    val id: Long,
    val name: String?,
    val overview: String?,
    val productionCode: String?,
    val seasonNumber: Int?,
    val showId: Int?,
    val stillPath: String?,
    val voteAverage: Float?,
    val voteCount: Int?
) : Parcelable

@Parcelize
data class TvNetwork(
    val headquarters: String?,
    val homepage: String?,
    val id: Long,
    val name: String?,
    val originCountry: String?
) : Parcelable

@Parcelize
data class TvSeason(
    val airDate: String?,
    val episodeCount: Int?,
    val id: Long,
    val name: String?,
    val overview: String?,
    val posterPath: String?,
    val seasonNumber: Int?
) : Parcelable

@Parcelize
data class Video(
    val id: String,
    val key: String,
    val name: String?,
    val site: String?,
    val size: VideoSize,
    val thumbnail: String?,
    val type: String?
) : Parcelable

sealed class Gender : Parcelable {
    @Parcelize object Female : Gender()
    @Parcelize object Male : Gender()
    @Parcelize object Unknown : Gender()
}

sealed class VideoSize : Parcelable {
    @Parcelize object V360 : VideoSize()
    @Parcelize object V480 : VideoSize()
    @Parcelize object V720 : VideoSize()
    @Parcelize object V1080 : VideoSize()
    @Parcelize object Unknown : VideoSize()
}

data class Config(
    val backdropSizes: List<String>,
    val baseUrl: String,
    val logoSizes: List<String>,
    val posterSizes: List<String>,
    val profileSizes: List<String>,
    val secureBaseUrl: String,
    val stillSizes: List<String> // for episode still images
)

sealed class GridViewMode : Parcelable {
    @Parcelize object Grid : GridViewMode()
    @Parcelize object List : GridViewMode()
}

sealed class DiscoverFilter(open val id: Long) : Parcelable {

    sealed class Preset(id: Long, val nameResId: Int) : DiscoverFilter(id) {
        //todo add trending
//        @Parcelize
//        object TrendingAll : Preset(0, R.string.discover_filter_trending)

        @Parcelize
        object PopularTvMovies : Preset(1, R.string.discover_filter_popular)

        @Parcelize
        object NowPlayingMovies : Preset(2, R.string.discover_filter_now_playing)

        @Parcelize
        object AiringTodayTv : Preset(3, R.string.discover_filter_airing_today)

        @Parcelize
        object OnTheAirTv : Preset(4, R.string.discover_filter_on_the_air_tv)

        @Parcelize
        object TopRatedTvMovies : Preset(5, R.string.discover_filter_top_rated)

        @Parcelize
        object UpcomingMovies : Preset(6, R.string.discover_filter_upcoming_movies)
    }
    //todo add custom params below? Like search params used to define filter
    sealed class Custom(id: Long, val name: String) : DiscoverFilter(id)
}

// used to store both movie and tv show lists
@Parcelize
data class ContentList(val id: Long, val name: String, val sortOrder: Int) : Parcelable

// used to store a content list and it's status (whether a given movie or tv show has been added to the list)
@Parcelize
data class ContentListStatus(
    val listId: Long,
    val listName: String,
    val listSortOrder: Int,
    val contentId: Long, // either a movie id or tv show id
    val contentIsInList: Boolean
) : Parcelable
