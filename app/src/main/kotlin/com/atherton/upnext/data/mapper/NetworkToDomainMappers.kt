package com.atherton.upnext.data.mapper

import com.atherton.upnext.data.network.model.NetworkResponse
import com.atherton.upnext.data.network.model.TmdbApiError
import com.atherton.upnext.data.network.model.TmdbConfiguration
import com.atherton.upnext.domain.model.ApiError
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.model.LceResponse

/*
 * A collection of extension functions to map from network models to app-level/domain models.
 *
 * NOTE - When mapping from a network model to a domain model, we filter out any objects which have a null id.
 */

/**
 * Maps a NetworkResponse to a domain LceResponse.
 *
 * @param cachedData whether or not the data is old/cached
 * @param fallbackData fallback data to be emitted in the event of an error
 * @param dataMapper provides a way to map from data layer models to app-level/domain models
 *
 */
@Deprecated("Deprecated")
internal fun <NETWORK : Any, DOMAIN : Any> NetworkResponse<NETWORK, TmdbApiError>.toDomainLceResponse(
    fallbackData: DOMAIN? = null,
    dataMapper: (NETWORK) -> DOMAIN
): LceResponse<DOMAIN> {
    return when (this) {
        is NetworkResponse.Success -> LceResponse.Content(dataMapper(body))
        is NetworkResponse.ServerError<TmdbApiError> -> LceResponse.Error.ServerError(error?.toDomainApiError(), code, fallbackData)
        is NetworkResponse.NetworkError -> LceResponse.Error.NetworkError(error, fallbackData)
    }
}

internal fun <NETWORK : Any, DOMAIN : Any> NetworkResponse<NETWORK, TmdbApiError>.toDomainLceResponse(
    data: DOMAIN?,
    fallbackData: DOMAIN? = data
): LceResponse<DOMAIN> {
    return when {
        this is NetworkResponse.Success && data != null -> LceResponse.Content(data)
        this is NetworkResponse.ServerError<TmdbApiError> -> LceResponse.Error.ServerError(error?.toDomainApiError(), code, fallbackData)
        this is NetworkResponse.NetworkError -> LceResponse.Error.NetworkError(error, fallbackData)
        else -> throw IllegalStateException("Data should not be null if network response was a success")
    }
}

private fun TmdbApiError.toDomainApiError(): ApiError = ApiError(statusMessage, statusCode)

internal fun TmdbConfiguration.toDomainConfig(): Config {
    with(images) {
        return Config(
            backdropSizes = backdropSizes,
            baseUrl = baseUrl,
            logoSizes = logoSizes,
            posterSizes = posterSizes,
            profileSizes = profileSizes,
            secureBaseUrl = secureBaseUrl,
            stillSizes = stillSizes
        )
    }
}
