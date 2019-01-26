package com.atherton.tmdb.data.api

import okhttp3.Interceptor
import okhttp3.Response

class TmdbApiKeyInterceptor(private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url()

        val url = originalUrl.newBuilder()
                .addQueryParameter("api_key", apiKey)
                .build()

        val requestBuilder = originalRequest.newBuilder().url(url)
        return chain.proceed(requestBuilder.build())
    }
}

