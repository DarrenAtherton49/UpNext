package com.atherton.upnext.data.local

import android.content.Context
import com.atherton.upnext.R
import com.atherton.upnext.data.network.model.TmdbConfiguration
import com.atherton.upnext.util.extension.adapt
import com.atherton.upnext.util.extension.readFileFromAssets
import com.atherton.upnext.util.injection.ApplicationContext
import com.squareup.moshi.Moshi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FallbackConfigStore @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val moshi: Moshi
) {

    fun getConfig(): TmdbConfiguration {
        val configJson: String = context.readFileFromAssets(R.raw.fallback_config)

        return moshi.adapt<TmdbConfiguration>(configJson)
            ?: throw IllegalStateException("Could not find fallback config file")
    }
}
