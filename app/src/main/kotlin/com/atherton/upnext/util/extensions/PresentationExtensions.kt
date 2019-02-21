package com.atherton.upnext.util.extensions

import android.content.Context
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Response
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun Response.Failure.generateErrorMessage(context: Context): String {
    return when (this) {
        is Response.Failure.ServerError -> {
            val apiError = this.error
            if (apiError != null) {
                context.getString(R.string.error_message_server_with_error).format(
                    this.code,
                    apiError.statusCode
                )
            } else {
                context.getString(R.string.error_message_server).format(this.code)
            }
        }
        is Response.Failure.NetworkError -> context.getString(R.string.error_message_no_internet)
    }
}

fun <T> Observable<T>.preventMultipleClicks(): Observable<T> {
    return this.throttleFirst(300, TimeUnit.MILLISECONDS)
}
