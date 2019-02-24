package com.atherton.upnext.presentation.util

import android.content.res.Resources
import com.atherton.upnext.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAppStringProvider @Inject constructor(private val resources: Resources) : AppStringProvider {

    override fun getSimilarMoviesHeader(): String = resources.getString(R.string.movie_detail_similar_movies_header)

    override fun getRuntimeString(runtime: Int): String {
        return resources.getQuantityString(R.plurals.movie_tv_detail_minutes_plural, runtime, runtime)
    }
}
