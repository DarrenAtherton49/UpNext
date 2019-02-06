package com.atherton.upnext.data.repository

import com.atherton.upnext.data.api.TmdbConfigService
import com.atherton.upnext.data.mapper.toDomainConfig
import com.atherton.upnext.data.mapper.toDomainResponse
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.repository.ConfigRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingConfigRepository @Inject constructor(
    //todo add in-memory cache
    //todo add database
    private val configService: TmdbConfigService
) : ConfigRepository {

    //todo check for config from network every 3 days. Pass an 'isStale' function into the cacher to check.
    override fun getConfig(): Single<Response<Config>> {
        return configService.getConfiguration()
            .map {
                it.toDomainResponse(false) { config -> config.toDomainConfig() }
            }
    }
}
