package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.SearchModelViewMode

interface SettingsRepository {

    fun getDiscoverViewMode(): SearchModelViewMode
    fun toggleDiscoverViewMode()
}