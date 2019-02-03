package com.atherton.upnext.data.model

/*
 * App-level/domain models. All network/data models should be mapped to the below models before use.
 */

data class ApiError(val statusMessage: String, val statusCode: Int)

/**
 * Wrapper to unify the movie, tv and person results below into one 'type' - useful for 'when' statements etc.
 */
sealed class SearchModel(open val id: Int?)

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
    val popularity: Float?,
    val voteAverage: Float?,
    val voteCount: Int?
) : SearchModel(id)

data class Movie(
    val adultContent: Boolean?,
    val backdropPath: String?,
    val genreIds: List<Int>?,
    override val id: Int?,
    val originalLanguage: String?,
    val originalTitle: String?,
    val overview: String?,
    val popularity: Float?,
    val posterPath: String?,
    val releaseDate: String?,
    val title: String?,
    val video: Boolean?,
    val voteAverage: Float?,
    val voteCount: Int?
) : SearchModel(id)

data class Person(
    val adultContent: Boolean?,
    override val id: Int?,
    val knownFor: List<SearchModel>?, // can be movies or tv shows
    val name: String?,
    val popularity: Float?,
    val profilePath: String?
) : SearchModel(id)