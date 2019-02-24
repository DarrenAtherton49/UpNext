package com.atherton.upnext.presentation.util

interface AppStringProvider {

    fun getSimilarMoviesHeader(): String
    fun getRuntimeString(runtime: Int): String
}
