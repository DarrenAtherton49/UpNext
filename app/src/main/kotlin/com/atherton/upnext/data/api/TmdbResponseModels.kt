package com.atherton.upnext.data.api

import com.squareup.moshi.Json

data class TmdbMultiSearchResponse(
    @Json(name = "page") val page: Int,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int,
    @Json(name = "results") val results: List<TmdbMultiSearchEntity>
)

data class TmdbMultiSearchResult(
    // common fields
    @Json(name = "adult") val adultContent: Boolean?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "genre_ids") val genreIds: List<Int>?,
    @Json(name = "id") val id: Int?,
    @Json(name = "media_type") val mediaType: String, // either 'movie', 'tv' or 'person'
    @Json(name = "name") val name: String?,
    @Json(name = "original_language") val originalLanguage: String?,
    @Json(name = "overview") val overview: String?,
    @Json(name = "popularity") val popularity: Float?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "vote_average") val voteAverage: Float?,
    @Json(name = "vote_count") val voteCount: Int?,

    // Movie specific fields
    @Json(name = "original_title") val originalTitle: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "video") val video: Boolean?,

    // Show specific fields
    @Json(name = "first_air_date") val firstAirDate: String?,
    @Json(name = "origin_country") val originCountry: List<String>?,
    @Json(name = "original_name") val originalName: String?,

    // Person specific fields
    @Json(name = "known_for") val knownFor: List<TmdbMultiSearchResult>?,
    @Json(name = "profile_path") val profilePath: String?
)

/**
 * Wrapper to unify the movie, tv and person results below into one 'type'
 */
sealed class TmdbMultiSearchEntity

data class TmdbMovieResult(
    @Json(name = "adult") val adultContent: Boolean?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "genre_ids") val genreIds: List<Int>?,
    @Json(name = "id") val id: Int?,
    @Json(name = "original_language") val originalLanguage: String?,
    @Json(name = "original_title") val originalTitle: String?,
    @Json(name = "overview") val overview: String?,
    @Json(name = "popularity") val popularity: Float?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "video") val video: Boolean?,
    @Json(name = "vote_average") val voteAverage: Float?,
    @Json(name = "vote_count") val voteCount: Int?
) : TmdbMultiSearchEntity()

data class TmdbTvResult(
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "first_air_date") val firstAirDate: String?,
    @Json(name = "genre_ids") val genreIds: List<Int>?,
    @Json(name = "id") val id: Int?,
    @Json(name = "name") val name: String?,
    @Json(name = "origin_country") val originCountry: List<String>?,
    @Json(name = "original_language") val originalLanguage: String?,
    @Json(name = "original_name") val originalName: String?,
    @Json(name = "overview") val overview: String?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "popularity") val popularity: Float?,
    @Json(name = "vote_average") val voteAverage: Float?,
    @Json(name = "vote_count") val voteCount: Int?
) : TmdbMultiSearchEntity()

data class TmdbPersonResult(
    @Json(name = "adult") val adultContent: Boolean?,
    @Json(name = "id") val id: Int?,
    @Json(name = "known_for") val knownFor: List<TmdbMultiSearchEntity>?, // can be movies or tv shows
    @Json(name = "name") val name: String?,
    @Json(name = "popularity") val popularity: Float?,
    @Json(name = "profile_path") val profilePath: String?
) : TmdbMultiSearchEntity()