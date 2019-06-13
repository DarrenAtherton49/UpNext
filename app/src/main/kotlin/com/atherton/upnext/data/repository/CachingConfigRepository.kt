package com.atherton.upnext.data.repository

import com.atherton.upnext.data.local.LocalConfigStore
import com.atherton.upnext.data.mapper.toDomainConfig
import com.atherton.upnext.data.network.model.NetworkResponse
import com.atherton.upnext.data.network.model.TmdbApiError
import com.atherton.upnext.data.network.model.TmdbConfiguration
import com.atherton.upnext.data.network.service.TmdbConfigService
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.repository.ConfigRepository
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingConfigRepository @Inject constructor(
    //todo add in-memory cache
    //todo add database
    private val configService: TmdbConfigService,
    private val localConfigStore: LocalConfigStore
) : ConfigRepository {

    private var cachedConfig: Config? = null

    //todo check for config from network every 3 days
    override fun getConfigObservable(): Observable<Config> {
        return if (cachedConfig != null) {
            Observable.fromCallable { cachedConfig }
        } else {
            configService.getConfigObservable()
                .toObservable()
                .map { response ->
                    when (response) {
                        is NetworkResponse.Success -> response.body.toDomainConfig()
                        else -> localConfigStore.getConfig().toDomainConfig()
                    }
                }
                .doOnError { localConfigStore.getConfig().toDomainConfig() }
                .doOnNext { cachedConfig = it }
        }
    }

    override fun getConfig(): Config {

        cachedConfig?.let { return it }

        configService.getConfig().enqueue(object : Callback<NetworkResponse<TmdbConfiguration, TmdbApiError>> {
            override fun onFailure(call: Call<NetworkResponse<TmdbConfiguration, TmdbApiError>>, t: Throwable) {}

            override fun onResponse(
                call: Call<NetworkResponse<TmdbConfiguration, TmdbApiError>>,
                response: Response<NetworkResponse<TmdbConfiguration, TmdbApiError>>
            ) {
                val responseBody = response.body()
                if (responseBody is NetworkResponse.Success<TmdbConfiguration>) {
                    val config = responseBody.body
                    cachedConfig = config.toDomainConfig()
                }
            }
        })

        val config = cachedConfig
        return if (config != null) {
            config
        } else {
            val fallbackConfig = localConfigStore.getConfig().toDomainConfig()
            cachedConfig = fallbackConfig
            fallbackConfig
        }
    }
}
