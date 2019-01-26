package com.atherton.tmdb.data.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbSearchService {

    @GET("search/multi")
    fun multiSearch(
            @Query("query") searchTerm: String,
            @Query("page") page: Int = 1,
            @Query("include_adult") includeAdultContent: Boolean = false //todo use local user setting
    ): Single<TmdbMultiSearchResponse>
}