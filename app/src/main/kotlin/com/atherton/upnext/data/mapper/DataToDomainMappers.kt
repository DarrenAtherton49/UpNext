package com.atherton.upnext.data.mapper

import com.atherton.upnext.data.local.AppSettings
import com.atherton.upnext.domain.model.GridViewMode

fun AppSettings.GridViewModeSetting.toDomainToggleMode(): GridViewMode {
    return when (this) {
        is AppSettings.GridViewModeSetting.Grid -> GridViewMode.Grid
        is AppSettings.GridViewModeSetting.List -> GridViewMode.List
    }
}
