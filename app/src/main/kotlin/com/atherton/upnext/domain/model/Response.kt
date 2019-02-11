package com.atherton.upnext.domain.model

import android.os.Parcelable
import com.atherton.upnext.util.parcel.IOExceptionParceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import java.io.IOException

/**
 * Represents the result of making a repository request.
 *
 * @param T success data type for response
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
        @TypeParceler<IOException, IOExceptionParceler>()
        data class NetworkError(val error: IOException) : Failure()
    }

    fun dataOrNull(): T? {
        return if (this is Response.Success) {
            this.data
        } else null
    }
}
