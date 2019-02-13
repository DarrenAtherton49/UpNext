package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.DiscoverViewMode
import com.atherton.upnext.domain.repository.SettingsRepository
import io.reactivex.Single
import javax.inject.Inject

class GetDiscoverViewModeUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {

    fun build(): Single<DiscoverViewMode> = Single.fromCallable(this::execute)

    fun execute(): DiscoverViewMode = settingsRepository.getDiscoverViewMode()
}
