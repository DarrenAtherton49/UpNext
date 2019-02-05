package com.atherton.upnext.data.repository

import android.os.Parcelable
import com.atherton.upnext.data.api.TmdbApiError
import com.atherton.upnext.data.mapper.toDomainApiError
import com.atherton.upnext.data.model.ApiError
import com.atherton.upnext.util.network.retrofit.NetworkResponse
import kotlinx.android.parcel.Parcelize
import java.io.IOException

/**
 * Represents the result of making a repository request.
 *
 * @param T success data type for  response.
 */
sealed class Response<out T : Any> {

    // A request with a 2XX response that's guaranteed to have a data of type 'T'
    data class Success<T : Any>(val data: T, val cached: Boolean) : Response<T>()

    sealed class Failure : Response<Nothing>(), Parcelable {

        // A non-2XX response that may have an Error as its error data.
        @Parcelize
        data class ServerError(val error: ApiError?, val code: Int) : Failure()

        // A request that didn't result in a response from the server.
        @Parcelize
        data class NetworkError(val error: IOException) : Failure()

        // A request that resulted in a non-network failure (e.g. because of no resource found or some business logic).
        sealed class AppError : Failure() {
            @Parcelize object Generic : AppError()
            @Parcelize object NoResourcesFound : AppError()
        }
    }
}

/**
 * Maps a NetworkResponse to a domain Result.
 *
 * @param dataMapper provides a way to map from data layer models to app-level/domain models
 * @param cachedData whether or not the data is old/cached
 *
 */
internal fun <DATA : Any, DOMAIN : Any> NetworkResponse<DATA, TmdbApiError>.toDomainResponse(
    cachedData: Boolean,
    dataMapper: (DATA) -> DOMAIN
): Response<DOMAIN> {
    return when (this) {
        is NetworkResponse.Success -> {
            Response.Success(dataMapper(body), cachedData)
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
