package com.atherton.upnext.data.model

import com.squareup.moshi.Json

/*
 * Network/data models. The below models should always be mapped to app-level/domain models before use.
 */

// Retrofit/Moshi deserialize to this class when there is an API error.
data class TmdbApiError(
    @Json(name = "status_message") val statusMessage: String,
    @Json(name = "status_code") val statusCode: Int
)

data class TmdbConfiguration(
    @Json(name = "change_keys") val changeKeys: List<String>,
    @Json(name = "images") val images: Images
) {
    data class Images(
        @Json(name = "backdrop_sizes") val backdropSizes: List<String>,
        @Json(name = "base_url") val baseUrl: String,
        @Json(name = "logo_sizes") val logoSizes: List<String>,
        @Json(name = "poster_sizes") val posterSizes: List<String>,
        @Json(name = "profile_sizes") val profileSizes: List<String>,
        @Json(name = "secure_base_url") val secureBaseUrl: String,
        @Json(name = "still_sizes") val stillSizes: List<String>
    )
}

open class TmdbPagedResponse<T> (
    @Json(name = "page") val page: Int,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int, //todo should we show this in UI?
    @Json(name = "results") val results: List<T>
)

class TmdbNowPlayingMoviesResponse<T>(
    @Json(name = "page") page: Int,
    @Json(name = "total_pages") totalPages: Int,
    @Json(name = "total_results") totalResults: Int, //todo should we show this in UI?
    @Json(name = "results") results: List<T>,
    @Json(name = "dates") val dates: Dates
) : TmdbPagedResponse<T>(page, totalPages, totalResults, results) {
    data class Dates(
        @Json(name = "minimum") val minimum: String,
        @Json(name = "maximum") val maximum: String
    )
}

data class TmdbMultiSearchResult(
    // common fields
    @Json(name = "adult") val adultContent: Boolean?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "genre_ids") val genreIds: List<Int>?,
    @Json(name = "id") val id: Int?,
    @Json(name = "media_type") val mediaType: String, // either 'tv', 'movie' or 'person'
    @Json(name = "name") val name: String?,
    @Json(name = "original_language") val originalLanguage: String?,
    @Json(name = "overview") val overview: String?,
    @Json(name = "popularity") val popularity: Float?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "vote_average") val voteAverage: Float?,
    @Json(name = "vote_count") val voteCount: Int?,

    // Show specific fields
    @Json(name = "first_air_date") val firstAirDate: String?,
    @Json(name = "origin_country") val originCountry: List<String>?,
    @Json(name = "original_name") val originalName: String?,

    // Movie specific fields
    @Json(name = "original_title") val originalTitle: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "video") val video: Boolean?,

    // Person specific fields
    @Json(name = "known_for") val knownFor: List<TmdbMultiSearchResult>?,
    @Json(name = "profile_path") val profilePath: String?
)

/**
 * Wrapper to unify the tv, movie and person results below into one 'type'
 */
sealed class TmdbMultiSearchModel

data class TmdbTvShow(
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
) : TmdbMultiSearchModel()

data class TmdbMovie(
    // base/search fields
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
    @Json(name = "vote_count") val voteCount: Int?,

    // detail fields
    @Json(name = "belongs_to_collection") val belongsToCollection: TmdbCollection?,
    @Json(name = "budget") val budget: Int?,
    @Json(name = "genres") val genres: List<TmdbGenre>?,
    @Json(name = "homepage") val homepage: String?,
    @Json(name = "imdb_id") val imdbId: String?,
    @Json(name = "production_companies") val productionCompanies: List<TmdbProductionCompany>?,
    @Json(name = "production_countries") val productionCountries: List<TmdbProductionCountry>?,
    @Json(name = "revenue") val revenue: Int?,
    @Json(name = "runtime") val runtime: Int?,
    @Json(name = "spoken_languages") val spokenLanguages: List<TmdbSpokenLanguage>?,
    @Json(name = "status") val status: String?,
    @Json(name = "tagline") val tagline: String?
) : TmdbMultiSearchModel()

data class TmdbPerson(
    @Json(name = "adult") val adultContent: Boolean?,
    @Json(name = "id") val id: Int?,
    @Json(name = "known_for") val knownFor: List<TmdbMultiSearchModel>?, // can be tv shows or movies
    @Json(name = "name") val name: String?,
    @Json(name = "popularity") val popularity: Float?,
    @Json(name = "profile_path") val profilePath: String?
) : TmdbMultiSearchModel()

data class TmdbCollection(
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "id") val id: Int?,
    @Json(name = "name") val name: String?,
    @Json(name = "poster_path") val posterPath: String?
)

data class TmdbGenre(
    @Json(name = "id") val id: Int?,
    @Json(name = "name") val name: String?
)

data class TmdbProductionCompany(
    @Json(name = "id") val id: Int?,
    @Json(name = "logo_path") val logoPath: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "origin_country") val originCountry: String?
)

data class TmdbProductionCountry(
    @Json(name = "iso_3166_1") val iso31661: String?,
    @Json(name = "name") val name: String?
)

data class TmdbSpokenLanguage(
    @Json(name = "iso_639_1") val iso6391: String?,
    @Json(name = "name") val name: String?
)
