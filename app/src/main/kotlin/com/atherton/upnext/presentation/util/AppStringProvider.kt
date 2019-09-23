package com.atherton.upnext.presentation.util

import com.atherton.upnext.domain.model.LceResponse

interface AppStringProvider {

    fun getSeasonsHeader(): String
    fun getVideosHeader(): String
    fun getCastHeader(): String
    fun getCrewHeader(): String
    fun getRecommendedContentHeader(): String
    fun getRuntimeString(runtime: String): String
    fun getVoteAverageString(voteAverage: String): String
    fun generateListCreatedMessage(listTitle: String): String
    fun generateMovieRemovedFromListMessage(movieTitle: String?, listTitle: String): String
    fun <T : Any> generateErrorMessage(error: LceResponse.Error<T>): String
}
