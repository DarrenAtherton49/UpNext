package com.atherton.upnext.presentation.util

interface AppStringProvider {

    fun getVideosHeader(): String
    fun getCastHeader(): String
    fun getCrewHeader(): String
    fun getRecommendedContentHeader(): String
    fun getRuntimeString(runtime: String): String
}
