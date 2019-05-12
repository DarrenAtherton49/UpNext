package com.atherton.upnext.data.network.service

import com.atherton.upnext.data.network.model.NetworkResponse
import com.atherton.upnext.data.network.model.TmdbApiError
import com.atherton.upnext.data.network.model.TmdbMultiSearchResult
import com.atherton.upnext.data.network.model.TmdbPagedResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbSearchService {

    @GET("search/multi")
    fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int = 1, //todo implement paging
        @Query("include_adult") includeAdultContent: Boolean = false //todo use local user setting
    ): Single<NetworkResponse<TmdbPagedResponse<TmdbMultiSearchResult>, TmdbApiError>>
}
