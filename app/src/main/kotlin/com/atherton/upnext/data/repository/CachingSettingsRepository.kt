package com.atherton.upnext.data.repository

import com.atherton.upnext.data.local.AppSettings
import com.atherton.upnext.data.mapper.toDomainToggleMode
import com.atherton.upnext.domain.model.GridViewMode
import com.atherton.upnext.domain.repository.SettingsRepository
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingSettingsRepository @Inject constructor(
    //todo add in-memory cache
    private val settings: AppSettings
) : SettingsRepository {

    override fun getGridViewMode(): GridViewMode {
        return settings.getGridViewModeSetting().toDomainToggleMode()
    }

    override fun getGridViewModeObservable(): Observable<GridViewMode> = Observable.fromCallable(this::getGridViewMode)

    override fun toggleGridViewMode() {
        settings.toggleGridViewModeSetting()
    }

    override fun toggleGridViewModeObservable(): Observable<Unit> = Observable.fromCallable(this::toggleGridViewMode)
}
