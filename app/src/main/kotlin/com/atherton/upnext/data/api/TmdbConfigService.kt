package com.atherton.upnext.data.api

import com.atherton.upnext.data.model.NetworkResponse
import com.atherton.upnext.data.model.TmdbApiError
import com.atherton.upnext.data.model.TmdbConfiguration
import io.reactivex.Single
import retrofit2.http.GET

interface TmdbConfigService {

    @GET("configuration")
    fun getConfiguration(): Single<NetworkResponse<TmdbConfiguration, TmdbApiError>>
}
