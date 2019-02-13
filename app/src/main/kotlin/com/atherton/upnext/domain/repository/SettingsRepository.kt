package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.DiscoverViewMode

interface SettingsRepository {

    fun getDiscoverViewMode(): DiscoverViewMode
    fun toggleDiscoverViewMode()
}