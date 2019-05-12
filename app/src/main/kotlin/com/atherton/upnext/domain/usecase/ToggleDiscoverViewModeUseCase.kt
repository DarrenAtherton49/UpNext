package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.repository.SettingsRepository
import io.reactivex.Observable
import javax.inject.Inject

class ToggleDiscoverViewModeUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {

    operator fun invoke(): Observable<Unit> = Observable.fromCallable(settingsRepository::toggleGridViewMode)
}
