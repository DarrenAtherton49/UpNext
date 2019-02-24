package com.atherton.upnext.presentation.util

interface AppStringProvider {

    fun getCastHeader(): String
    fun getCrewHeader(): String
    fun getSimilarMoviesHeader(): String
    fun getRuntimeString(runtime: Int): String
}
