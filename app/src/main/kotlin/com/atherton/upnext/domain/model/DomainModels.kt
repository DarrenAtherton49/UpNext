package com.atherton.upnext.domain.model

import android.os.Parcelable
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
) : SearchModel(id, popularity)

@Parcelize
data class Person(
    val adultContent: Boolean?,
    override val id: Int?,
    val knownFor: List<SearchModel>?, // can be movies or tv shows
    val name: String?,
    override val popularity: Float?,
    val profilePath: String?
) : SearchModel(id, popularity)

data class Config(
    val baseUrl: String,
    val secureBaseUrl: String,
    val backdropSizes: List<String>,
    val logoSizes: List<String>,
    val posterSizes: List<String>,
    val profileSizes: List<String>,
    val stillSizes: List<String> // for episode still images
)
