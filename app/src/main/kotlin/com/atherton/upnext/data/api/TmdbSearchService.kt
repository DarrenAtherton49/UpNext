package com.atherton.upnext.data.api

import com.atherton.upnext.data.model.NetworkResponse
import com.atherton.upnext.data.model.TmdbApiError
import com.atherton.upnext.data.model.TmdbMultiSearchResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbSearchService {

    @GET("search/multi")
    fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int = 1, //todo implement paging
        @Query("include_adult") includeAdultContent: Boolean = false //todo use local user setting
    ): Single<NetworkResponse<TmdbMultiSearchResponse, TmdbApiError>>
}
