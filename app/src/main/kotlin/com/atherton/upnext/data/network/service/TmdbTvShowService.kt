package com.atherton.upnext.data.network.service

import com.atherton.upnext.data.model.NetworkResponse
import com.atherton.upnext.data.model.TmdbApiError
import com.atherton.upnext.data.model.TmdbPagedResponse
import com.atherton.upnext.data.model.TmdbTvShow
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbTvShowService {

    @GET("tv/popular")
    fun getPopular(
        @Query("page") page: Int = 1 //todo implement paging
    ): Single<NetworkResponse<TmdbPagedResponse<TmdbTvShow>, TmdbApiError>>

    @GET("tv/top_rated")
    fun getTopRated(
        @Query("page") page: Int = 1 //todo implement paging
    ): Single<NetworkResponse<TmdbPagedResponse<TmdbTvShow>, TmdbApiError>>
}
