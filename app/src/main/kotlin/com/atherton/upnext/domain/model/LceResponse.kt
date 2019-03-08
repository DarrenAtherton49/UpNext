package com.atherton.upnext.domain.model

import android.os.Parcelable
import com.atherton.upnext.util.parcel.IOExceptionParceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import java.io.IOException

/**
 * Represents the result of making a repository request.
 */
sealed class LceResponse<out T : Any> {

    // can assume that any data used in the Loading state is cached
    data class Loading<T : Any>(val data: T) : LceResponse<T>()

    data class Content<T : Any>(val data: T, val cached: Boolean) : LceResponse<T>()

    sealed class Error : LceResponse<Nothing>(), Parcelable {

        // A non-2XX response that may have an ApiError as its error data.
        @Parcelize
        data class ServerError(val error: ApiError?, val code: Int) : Error()

        // A request that didn't result in a response from the server.
        @Parcelize
        @TypeParceler<IOException, IOExceptionParceler>()
        data class NetworkError(val error: IOException) : Error()
    }

    fun dataOrNull(): T? {
        return if (this is LceResponse.Content) {
            this.data
        } else null
    }
}
