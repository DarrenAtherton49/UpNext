package com.atherton.upnext.data.repository

import com.atherton.upnext.data.mapper.toDomainResponse
import com.atherton.upnext.data.mapper.toDomainTvShow
import com.atherton.upnext.data.model.NetworkResponse
import com.atherton.upnext.data.model.TmdbApiError
import com.atherton.upnext.data.model.TmdbPagedResponse
import com.atherton.upnext.data.model.TmdbTvShow
import com.atherton.upnext.data.network.service.TmdbTvShowService
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

    override fun getTvShow(id: Int): Single<Response<TvShow>> {
        return tvShowService.getTvDetails(id).map {
            it.toDomainResponse(false) { tvShow -> tvShow.toDomainTvShow() }
        }
    }

    override fun getPopular(): Single<Response<List<TvShow>>> {
        return tvShowService.getPopular().toDomainTvShows()
    }

    override fun getTopRated(): Single<Response<List<TvShow>>> {
        return tvShowService.getTopRated().toDomainTvShows()
    }

    override fun getAiringToday(): Single<Response<List<TvShow>>> {
        return tvShowService.getAiringToday().toDomainTvShows()
    }

    override fun getOnTheAir(): Single<Response<List<TvShow>>> {
        return tvShowService.getOnTheAir().toDomainTvShows()
    }

    private fun Single<NetworkResponse<TmdbPagedResponse<TmdbTvShow>, TmdbApiError>>.toDomainTvShows()
        : Single<Response<List<TvShow>>> {
        return this.map {
            it.toDomainResponse(false) { response ->
                response.results.map { tvShow -> tvShow.toDomainTvShow() }
            }
        }
    }
}
