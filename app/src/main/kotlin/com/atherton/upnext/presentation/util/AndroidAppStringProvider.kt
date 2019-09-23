package com.atherton.upnext.presentation.util

import android.content.res.Resources
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.LceResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAppStringProvider @Inject constructor(private val resources: Resources) : AppStringProvider {

    override fun getSeasonsHeader(): String = resources.getString(R.string.content_detail_seasons_header)

    override fun getVideosHeader(): String = resources.getString(R.string.content_detail_videos_header)

    override fun getCastHeader(): String = resources.getString(R.string.content_detail_cast_header)

    override fun getCrewHeader(): String = resources.getString(R.string.content_detail_crew_header)

    override fun getRecommendedContentHeader(): String = resources.getString(R.string.content_detail_recommended_content_header)

    override fun getRuntimeString(runtime: String): String {
        return resources.getString(R.string.content_detail_runtime_mins).format(runtime)
    }

    override fun getVoteAverageString(voteAverage: String): String {
        return resources.getString(R.string.content_detail_vote_average).format(voteAverage)
    }

    override fun generateListCreatedMessage(listTitle: String): String {
        return resources.getString(R.string.new_list_created).format(listTitle)
    }

    override fun generateMovieRemovedFromListMessage(movieTitle: String?, listTitle: String): String {
        return if (movieTitle != null) {
            resources.getString(R.string.movie_list_item_removed_from_list).format(movieTitle, listTitle)
        } else {
            resources.getString(R.string.movie_list_item_removed_from_list_no_movie_title)
        }
    }

    override fun <T : Any> generateErrorMessage(error: LceResponse.Error<T>): String {
        return when (error) {
            is LceResponse.Error.ServerError -> {
                val apiError = error.error
                if (apiError != null) {
                    resources.getString(R.string.error_message_server_with_error).format(
                        error.code,
                        apiError.statusCode
                    )
                } else {
                    resources.getString(R.string.error_message_server).format(error.code)
                }
            }
            is LceResponse.Error.NetworkError -> resources.getString(R.string.error_message_no_internet)
        }
    }
}
