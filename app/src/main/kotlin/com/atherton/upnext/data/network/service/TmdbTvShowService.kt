package com.atherton.upnext.data.network.service

import com.atherton.upnext.data.model.NetworkResponse
import com.atherton.upnext.data.model.TmdbApiError
import com.atherton.upnext.data.model.TmdbPagedResponse
import com.atherton.upnext.data.model.TmdbTvShow
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbTvShowService {

    @GET("tv/{tv_id}")
    fun getTvDetails(
        @Path("tv_id") id: Int,
        @Query("append_to_response") appendToResponse: String = "recommendations"
    ): Single<NetworkResponse<TmdbTvShow, TmdbApiError>>

    @GET("tv/popular")
    fun getPopular(
        @Query("page") page: Int = 1 //todo implement paging
    ): Single<NetworkResponse<TmdbPagedResponse<TmdbTvShow>, TmdbApiError>>

    @GET("tv/top_rated")
    fun getTopRated(
        @Query("page") page: Int = 1 //todo implement paging
    ): Single<NetworkResponse<TmdbPagedResponse<TmdbTvShow>, TmdbApiError>>

    //todo implement timezone parameter
    @GET("tv/airing_today")
    fun getAiringToday(
        @Query("page") page: Int = 1 //todo implement paging
    ): Single<NetworkResponse<TmdbPagedResponse<TmdbTvShow>, TmdbApiError>>

    @GET("tv/on_the_air")
    fun getOnTheAir(
        @Query("page") page: Int = 1 //todo implement paging
    ): Single<NetworkResponse<TmdbPagedResponse<TmdbTvShow>, TmdbApiError>>
}
