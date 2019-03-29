package com.atherton.upnext.data.mapper

import com.atherton.upnext.data.local.AppSettings
import com.atherton.upnext.domain.model.SearchModelViewMode

fun AppSettings.DiscoverViewToggleSetting.toDomainToggleMode(): SearchModelViewMode {
    return when (this) {
        is AppSettings.DiscoverViewToggleSetting.Grid -> SearchModelViewMode.Grid
        is AppSettings.DiscoverViewToggleSetting.List -> SearchModelViewMode.List
    }
}
