package com.atherton.upnext.data.network

import com.atherton.upnext.data.model.NetworkResponse
import com.atherton.upnext.data.model.TmdbApiError
import com.atherton.upnext.data.model.TmdbConfiguration
import io.reactivex.Single
import retrofit2.http.GET

interface TmdbConfigService {

    @GET("configuration")
    fun getConfig(): Single<NetworkResponse<TmdbConfiguration, TmdbApiError>>
}
