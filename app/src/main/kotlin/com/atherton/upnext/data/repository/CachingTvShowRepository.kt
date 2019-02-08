package com.atherton.upnext.data.repository

import com.atherton.upnext.data.mapper.toDomainResponse
import com.atherton.upnext.data.mapper.toDomainTvShow
import com.atherton.upnext.data.network.TmdbTvShowService
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.TvShow
import com.atherton.upnext.domain.repository.TvShowRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingTvShowRepository @Inject constructor(
    //todo add in-memory cache
    //todo add database
    private val tvShowService: TmdbTvShowService
) : TvShowRepository {

    override fun popular(): Single<Response<List<TvShow>>> {
        return tvShowService.getPopular()
            .map {
                it.toDomainResponse(false) { response ->
                    response.results.map { tvShow ->
                        tvShow.toDomainTvShow()
                    }
                }
            }
    }
}
