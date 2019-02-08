package com.atherton.upnext.data.network

import com.atherton.upnext.data.model.NetworkResponse
import com.atherton.upnext.data.model.PagedResponse
import com.atherton.upnext.data.model.TmdbApiError
import com.atherton.upnext.data.model.TmdbMultiSearchModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbSearchService {

    @GET("search/multi")
    fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int = 1, //todo implement paging
        @Query("include_adult") includeAdultContent: Boolean = false //todo use local user setting
    ): Single<NetworkResponse<PagedResponse<TmdbMultiSearchModel>, TmdbApiError>>
}
