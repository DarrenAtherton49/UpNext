package com.atherton.upnext.data.repository

import com.atherton.upnext.data.local.AppSettings
import com.atherton.upnext.data.mapper.toDomainToggleMode
import com.atherton.upnext.domain.model.DiscoverViewMode
import com.atherton.upnext.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingSettingsRepository @Inject constructor(
    //todo add in-memory cache
    private val settings: AppSettings
) : SettingsRepository {

    override fun getDiscoverViewMode(): DiscoverViewMode {
        return settings.getDiscoverViewSetting().toDomainToggleMode()
    }

    override fun toggleDiscoverViewMode() {
        settings.toggleDiscoverViewSetting()
    }
}
