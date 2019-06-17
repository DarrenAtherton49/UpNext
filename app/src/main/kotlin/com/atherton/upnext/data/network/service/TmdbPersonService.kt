package com.atherton.upnext.data.network.service

import com.atherton.upnext.data.network.model.NetworkResponse
import com.atherton.upnext.data.network.model.TmdbApiError
import com.atherton.upnext.data.network.model.TmdbPerson
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface TmdbPersonService {

    @GET("/person/{person_id}")
    fun getPersonDetails(
        @Path("person_id") id: Long
    ): Single<NetworkResponse<TmdbPerson, TmdbApiError>>
}
