package com.atherton.upnext.util.network.retrofit

import java.io.IOException

/**
 * Represents the result of making a network request.
 *
 * @param T success data type for 2xx response.
 * @param E error data type for non-2xx response.
 */
sealed class NetworkResponse<out T : Any, out E : Any> {

    /**
     * A request that resulted in a response with a 2xx status code that has a data.
     */
    data class Success<T : Any>(val body: T) : NetworkResponse<T, Nothing>()

    /**
     * A request that resulted in a response with a non-2xx status code.
     */
    data class ServerError<U : Any>(val error: U?, val code: Int) : NetworkResponse<Nothing, U>()

    /**
     * A request that didn't result in a response.
     */
    data class NetworkError(val error: IOException) : NetworkResponse<Nothing, Nothing>()
}
