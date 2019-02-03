package com.atherton.upnext.data.repository

import com.atherton.upnext.data.api.TmdbApiError
import com.atherton.upnext.data.mapper.toDomainApiError
import com.atherton.upnext.data.model.ApiError
import com.atherton.upnext.util.network.retrofit.NetworkResponse
import java.io.IOException

/**
 * Represents the result of making a repository request.
 *
 * @param T success data type for  response.
 */
sealed class Response<out T : Any> {

    // A request with a 2XX response that's guaranteed to have a data of type 'T'
    data class Success<T : Any>(val data: T) : Response<T>()

    sealed class Failure : Response<Nothing>() {

        // A non-2XX response that may have an Error as its error data.
        data class ServerError(val error: ApiError?, val code: Int) : Failure()
        // A request that didn't result in a response from the server.
        data class NetworkError(val error: IOException) : Failure()

        // A request that resulted in a non-network failure (e.g. because of no resource found or some business logic).
        sealed class AppError : Failure() {
            object Generic : AppError()
        }
    }
}

/**
 * Maps a NetworkResponse to a domain Result.
 *
 * @param dataMapper provides a way to map from data layer models to app-level/domain models
 *
 */
internal fun <DATA : Any, DOMAIN : Any> NetworkResponse<DATA, TmdbApiError>.toDomainResponse(
    dataMapper: (DATA) -> DOMAIN
): Response<DOMAIN> {
    return when (this) {
        is NetworkResponse.Success -> {
            Response.Success(dataMapper(body))
        }
        is NetworkResponse.ServerError<TmdbApiError
            > -> {
            Response.Failure.ServerError(error?.toDomainApiError(), code)
        }
        is NetworkResponse.NetworkError -> {
            Response.Failure.NetworkError(error)
        }
    }
}
