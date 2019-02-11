package com.atherton.upnext.util.extensions

import android.content.Context
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Response

fun Response.Failure.generateErrorMessage(context: Context): String {
    return when (this) {
        is Response.Failure.ServerError -> {
            val apiError = this.error
            if (apiError != null) {
                context.getString(R.string.search_results_error_server_with_error).format(
                    this.code,
                    apiError.statusCode
                )
            } else {
                context.getString(R.string.search_results_error_server).format(this.code)
            }
        }
        is Response.Failure.NetworkError -> context.getString(R.string.search_results_error_network)
    }
}
