package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.repository.SettingsRepository
import io.reactivex.Single
import javax.inject.Inject

class ToggleDiscoverViewModeUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {

    fun build(): Single<Unit> = Single.fromCallable(settingsRepository::toggleDiscoverViewMode)
}
