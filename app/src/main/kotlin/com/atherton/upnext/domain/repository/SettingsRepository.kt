package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.GridViewMode
import io.reactivex.Observable

interface SettingsRepository {

    fun getGridViewMode(): GridViewMode
    fun getGridViewModeObservable(): Observable<GridViewMode>
    fun toggleGridViewMode()
    fun toggleGridViewModeObservable(): Observable<Unit>
}