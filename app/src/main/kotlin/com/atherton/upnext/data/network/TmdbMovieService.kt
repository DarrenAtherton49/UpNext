package com.atherton.upnext.data.network

import com.atherton.upnext.data.model.NetworkResponse
import com.atherton.upnext.data.model.PagedResponse
import com.atherton.upnext.data.model.TmdbApiError
import com.atherton.upnext.data.model.TmdbMovie
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbMovieService {

    @GET("movie/popular")
    fun getPopular(
        @Query("page") page: Int = 1 //todo implement paging
    ): Single<NetworkResponse<PagedResponse<TmdbMovie>, TmdbApiError>>
}
