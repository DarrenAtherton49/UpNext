package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.repository.ConfigRepository
import io.reactivex.Single
import javax.inject.Inject

class GetConfigUseCase @Inject constructor(
    private val configRepository: ConfigRepository
) {

    fun build(): Single<Config> = configRepository.getConfig()
}
