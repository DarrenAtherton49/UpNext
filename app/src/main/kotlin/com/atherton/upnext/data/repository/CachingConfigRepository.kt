package com.atherton.upnext.data.repository

import com.atherton.upnext.data.local.LocalConfigStore
import com.atherton.upnext.data.mapper.toDomainConfig
import com.atherton.upnext.data.model.NetworkResponse
import com.atherton.upnext.data.network.TmdbConfigService
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.repository.ConfigRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingConfigRepository @Inject constructor(
    //todo add in-memory cache
    //todo add database
    private val configService: TmdbConfigService,
    private val localConfigStore: LocalConfigStore
) : ConfigRepository {

    //todo check for config from network every 3 days. Pass an 'isStale' function into the cacher to check.
    override fun getConfig(): Single<Config> {
        return configService.getConfig()
            .map { response ->
                when (response) {
                    is NetworkResponse.Success -> response.body.toDomainConfig()
                    else -> localConfigStore.getConfig().toDomainConfig()
                }
            }
    }
    //todo fall back on stored json config file using cacher
}
