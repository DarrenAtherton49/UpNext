package com.atherton.upnext.data.repository

import com.atherton.upnext.data.db.dao.ConfigDao
import com.atherton.upnext.data.db.model.config.RoomConfig
import com.atherton.upnext.data.local.FallbackConfigStore
import com.atherton.upnext.data.mapper.toDomainConfig
import com.atherton.upnext.data.mapper.toRoomConfig
import com.atherton.upnext.data.network.model.NetworkResponse
import com.atherton.upnext.data.network.model.TmdbApiError
import com.atherton.upnext.data.network.model.TmdbConfiguration
import com.atherton.upnext.data.network.service.TmdbConfigService
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.repository.ConfigRepository
import com.atherton.upnext.util.extensions.ioThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingConfigRepository @Inject constructor(
    private val configDao: ConfigDao,
    private val configService: TmdbConfigService,
    private val fallbackConfigStore: FallbackConfigStore
) : ConfigRepository {

    private var cachedConfig: Config? = null

    override fun getConfig(): Config {
        val cached = cachedConfig
        if (cached != null) {
            return cached
        } else {
            val dbConfig: RoomConfig? = configDao.getConfig()
            return if (dbConfig != null) {
                val config = dbConfig.toDomainConfig()
                cachedConfig = config
                config
            } else {
                val fallbackConfig = fallbackConfigStore.getConfig()
                val config = fallbackConfig.toDomainConfig()
                ioThread {
                    configDao.insertConfig(fallbackConfig.toRoomConfig())
                }
                config
            }
        }
    }

    //todo call this every 3 days in the background
    override fun refreshConfig() {
        configService.getConfig().enqueue(object : Callback<NetworkResponse<TmdbConfiguration, TmdbApiError>> {

            override fun onFailure(call: Call<NetworkResponse<TmdbConfiguration, TmdbApiError>>, throwable: Throwable) {}

            override fun onResponse(
                call: Call<NetworkResponse<TmdbConfiguration, TmdbApiError>>,
                response: Response<NetworkResponse<TmdbConfiguration, TmdbApiError>>
            ) {
                val responseBody = response.body()
                if (responseBody is NetworkResponse.Success<TmdbConfiguration>) {
                    ioThread {
                        val networkConfig: TmdbConfiguration = responseBody.body
                        val roomConfig: RoomConfig = networkConfig.toRoomConfig()
                        configDao.insertConfig(roomConfig)
                    }
                }
            }
        })
    }
}
