package com.atherton.upnext.data.network.service

import com.atherton.upnext.data.network.model.NetworkResponse
import com.atherton.upnext.data.network.model.TmdbApiError
import com.atherton.upnext.data.network.model.TmdbConfiguration
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET

interface TmdbConfigService {

    @GET("configuration")
    fun getConfigObservable(): Single<NetworkResponse<TmdbConfiguration, TmdbApiError>>

    @GET("configuration")
    fun getConfig(): Call<NetworkResponse<TmdbConfiguration, TmdbApiError>>
}
