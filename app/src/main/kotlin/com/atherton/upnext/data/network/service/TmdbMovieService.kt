package com.atherton.upnext.data.network.service

import com.atherton.upnext.data.model.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbMovieService {

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") id: Int,
        @Query("append_to_response") appendToResponse: String = "credits,reviews"
    ): Single<NetworkResponse<TmdbMovie, TmdbApiError>>

    @GET("movie/popular")
    fun getPopular(
        @Query("page") page: Int = 1 //todo implement paging
    ): Single<NetworkResponse<TmdbPagedResponse<TmdbMovie>, TmdbApiError>>

    @GET("movie/upcoming")
    fun getUpcoming(
        @Query("page") page: Int = 1 //todo implement paging
    ): Single<NetworkResponse<TmdbPagedResponse<TmdbMovie>, TmdbApiError>>

    @GET("movie/top_rated")
    fun getTopRated(
        @Query("page") page: Int = 1 //todo implement paging
    ): Single<NetworkResponse<TmdbPagedResponse<TmdbMovie>, TmdbApiError>>

    @GET("movie/now_playing")
    fun getNowPlaying(
        @Query("page") page: Int = 1 //todo implement paging
    ): Single<NetworkResponse<TmdbNowPlayingMoviesResponse<TmdbMovie>, TmdbApiError>>
}
