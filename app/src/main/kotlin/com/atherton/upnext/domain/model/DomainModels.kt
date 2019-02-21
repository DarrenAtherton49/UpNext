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
 * Wrapper to unify the movie, tv and person results below into one 'type' - useful for 'when' statements etc.
 */
sealed class SearchModel(open val id: Int?, open val popularity: Float?): Parcelable

@Parcelize
data class TvShow(
    val backdropPath: String?,
    val firstAirDate: String?,
    val genreIds: List<Int>?,
    override val id: Int?,
    val name: String?,
    val originCountry: List<String>?,
    val originalLanguage: String?,
    val originalName: String?,
    val overview: String?,
    val posterPath: String?,
    override val popularity: Float?,
    val voteAverage: Float?,
    val voteCount: Int?
) : SearchModel(id, popularity)

@Parcelize
data class Movie(
    val adultContent: Boolean?,
    val backdropPath: String?,
    val detail: Detail?,
    val genreIds: List<Int>?,
    override val id: Int?,
    val originalLanguage: String?,
    val originalTitle: String?,
    val overview: String?,
    override val popularity: Float?,
    val posterPath: String?,
    val releaseDate: String?,
    val title: String?,
    val video: Boolean?,
    val voteAverage: Float?,
    val voteCount: Int?
) : SearchModel(id, popularity) {

    @Parcelize
    data class Detail(
        val belongsToCollection: Collection?,
        val budget: Int?,
        val genres: List<Genre>?,
        val homepage: String?,
        val imdbId: String?,
        val productionCompanies: List<ProductionCompany>?,
        val productionCountries: List<ProductionCountry>?,
        val revenue: Int?,
        val runtime: Int?,
        val spokenLanguages: List<SpokenLanguage>?,
        val status: String?,
        val tagline: String?
    ) : Parcelable
}

@Parcelize
data class Person(
    val adultContent: Boolean?,
    override val id: Int?,
    val knownFor: List<SearchModel>?, // can be movies or tv shows
    val name: String?,
    override val popularity: Float?,
    val profilePath: String?
) : SearchModel(id, popularity)

@Parcelize
data class Collection(val backdropPath: String?, val id: Int?, val name: String?, val posterPath: String?) : Parcelable

@Parcelize
data class Genre(val id: Int?, val name: String?) : Parcelable

@Parcelize
data class ProductionCompany(val id: Int?, val logoPath: String?, val name: String?, val originCountry: String?) : Parcelable

@Parcelize
data class ProductionCountry(val iso31661: String?, val name: String?) : Parcelable

@Parcelize
data class SpokenLanguage(val iso6391: String?, val name: String?) : Parcelable

data class Config(
    val baseUrl: String,
    val secureBaseUrl: String,
    val backdropSizes: List<String>,
    val logoSizes: List<String>,
    val posterSizes: List<String>,
    val profileSizes: List<String>,
    val stillSizes: List<String> // for episode still images
)

sealed class SearchModelViewMode : Parcelable {
    @Parcelize object Grid : SearchModelViewMode()
    @Parcelize object List : SearchModelViewMode()
}

sealed class DiscoverFilter(open val id: Long) : Parcelable {

    sealed class Preset(id: Long, val nameResId: Int) : DiscoverFilter(id) {
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
