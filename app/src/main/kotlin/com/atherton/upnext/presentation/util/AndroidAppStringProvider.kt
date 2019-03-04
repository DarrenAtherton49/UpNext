package com.atherton.upnext.presentation.util

import android.content.res.Resources
import com.atherton.upnext.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAppStringProvider @Inject constructor(private val resources: Resources) : AppStringProvider {

    override fun getVideosHeader(): String = resources.getString(R.string.content_detail_videos_header)

    override fun getCastHeader(): String = resources.getString(R.string.content_detail_cast_header)

    override fun getCrewHeader(): String = resources.getString(R.string.content_detail_crew_header)

    override fun getRecommendedContentHeader(): String = resources.getString(R.string.content_detail_recommended_content_header)

    override fun getRuntimeString(runtime: String): String {
        return resources.getString(R.string.content_detail_runtime_mins).format(runtime)
    }
}
