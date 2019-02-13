package com.atherton.upnext.data.local

import android.content.SharedPreferences
import com.atherton.upnext.util.extensions.get
import com.atherton.upnext.util.extensions.set
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesStorage @Inject constructor(private val sharedPreferences: SharedPreferences) : AppSettings {

    override fun getDiscoverViewSetting(): AppSettings.DiscoverViewToggleSetting {
        val settingString: String? = sharedPreferences[KEY_DISCOVER_VIEW_TOGGLE]
        return when (settingString) {
            VALUE_DISCOVER_VIEW_TOGGLE_GRID -> AppSettings.DiscoverViewToggleSetting.Grid
            VALUE_DISCOVER_VIEW_TOGGLE_CAROUSEL -> AppSettings.DiscoverViewToggleSetting.Carousel
            else -> AppSettings.DiscoverViewToggleSetting.Carousel
        }
    }

    override fun toggleDiscoverViewSetting() {
        val settingString: String? = sharedPreferences[KEY_DISCOVER_VIEW_TOGGLE]
        when (settingString) {
            VALUE_DISCOVER_VIEW_TOGGLE_GRID -> {
                sharedPreferences[KEY_DISCOVER_VIEW_TOGGLE] = VALUE_DISCOVER_VIEW_TOGGLE_CAROUSEL
            }
            VALUE_DISCOVER_VIEW_TOGGLE_CAROUSEL -> {
                sharedPreferences[KEY_DISCOVER_VIEW_TOGGLE] = VALUE_DISCOVER_VIEW_TOGGLE_GRID
            }
            else -> sharedPreferences[KEY_DISCOVER_VIEW_TOGGLE] = VALUE_DISCOVER_VIEW_TOGGLE_CAROUSEL
        }
    }

    companion object {
        private const val KEY_DISCOVER_VIEW_TOGGLE = "key_discover_view_toggle"
        private const val VALUE_DISCOVER_VIEW_TOGGLE_GRID = "value_discover_view_toggle_grid"
        private const val VALUE_DISCOVER_VIEW_TOGGLE_CAROUSEL = "value_discover_view_toggle_carousel"
    }
}
