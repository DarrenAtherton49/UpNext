package com.atherton.tmdb.data.api

import com.squareup.moshi.Json

data class TmdbMultiSearchResponse(
        val page: Int,
        @Json(name = "total_pages") val totalPages: Int,
        @Json(name = "total_results") val totalResults: Int
)