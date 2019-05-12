package com.atherton.upnext.data.local

import android.content.SharedPreferences
import com.atherton.upnext.util.extensions.get
import com.atherton.upnext.util.extensions.set
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesStorage @Inject constructor(private val sharedPreferences: SharedPreferences) : AppSettings {

    override fun getGridViewModeSetting(): AppSettings.GridViewModeSetting {
        val settingString: String? = sharedPreferences[KEY_GRID_VIEW_MODE_TOGGLE]
        return when (settingString) {
            VALUE_GRID_VIEW_MODE_TOGGLE_GRID -> AppSettings.GridViewModeSetting.Grid
            VALUE_GRID_VIEW_MODE_TOGGLE_LIST -> AppSettings.GridViewModeSetting.List
            else -> AppSettings.GridViewModeSetting.Grid
        }
    }

    override fun toggleGridViewModeSetting() {
        val settingString: String? = sharedPreferences[KEY_GRID_VIEW_MODE_TOGGLE]
        when (settingString) {
            VALUE_GRID_VIEW_MODE_TOGGLE_GRID -> {
                sharedPreferences[KEY_GRID_VIEW_MODE_TOGGLE] = VALUE_GRID_VIEW_MODE_TOGGLE_LIST
            }
            VALUE_GRID_VIEW_MODE_TOGGLE_LIST -> {
                sharedPreferences[KEY_GRID_VIEW_MODE_TOGGLE] = VALUE_GRID_VIEW_MODE_TOGGLE_GRID
            }
            else -> sharedPreferences[KEY_GRID_VIEW_MODE_TOGGLE] = VALUE_GRID_VIEW_MODE_TOGGLE_LIST
        }
    }

    companion object {
        private const val KEY_GRID_VIEW_MODE_TOGGLE = "key_grid_view_mode_toggle"
        private const val VALUE_GRID_VIEW_MODE_TOGGLE_GRID = "value_grid_view_mode_toggle_grid"
        private const val VALUE_GRID_VIEW_MODE_TOGGLE_LIST = "value_grid_view_mode_toggle_carousel"
    }
}
