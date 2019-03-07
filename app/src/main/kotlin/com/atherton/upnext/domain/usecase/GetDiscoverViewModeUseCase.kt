package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.SearchModelViewMode
import com.atherton.upnext.domain.repository.SettingsRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetDiscoverViewModeUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {

    operator fun invoke(): Observable<SearchModelViewMode> = Observable.fromCallable(this::execute)

    private fun execute(): SearchModelViewMode = settingsRepository.getDiscoverViewMode()
}
