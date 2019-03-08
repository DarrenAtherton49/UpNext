package com.atherton.upnext.data.repository

import com.atherton.upnext.data.mapper.toDomainLceResponse
import com.atherton.upnext.data.mapper.toDomainTvShow
import com.atherton.upnext.data.model.NetworkResponse
import com.atherton.upnext.data.model.TmdbApiError
import com.atherton.upnext.data.model.TmdbPagedResponse
import com.atherton.upnext.data.model.TmdbTvShow
import com.atherton.upnext.data.network.service.TmdbTvShowService
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.TvShow
import com.atherton.upnext.domain.repository.TvShowRepository
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingTvShowRepository @Inject constructor(
    //todo add in-memory cache
    //todo add database
    private val tvShowService: TmdbTvShowService
) : TvShowRepository {

    override fun getTvShow(id: Int): Observable<LceResponse<TvShow>> {
        return tvShowService.getTvDetails(id)
            .toObservable()
            .map { it.toDomainLceResponse(false) { tvShow -> tvShow.toDomainTvShow() }
        }
    }

    override fun getPopular(): Observable<LceResponse<List<TvShow>>> {
        return tvShowService.getPopular().toDomainTvShows()
    }

    override fun getTopRated(): Observable<LceResponse<List<TvShow>>> {
        return tvShowService.getTopRated().toDomainTvShows()
    }

    override fun getAiringToday(): Observable<LceResponse<List<TvShow>>> {
        return tvShowService.getAiringToday().toDomainTvShows()
    }

    override fun getOnTheAir(): Observable<LceResponse<List<TvShow>>> {
        return tvShowService.getOnTheAir().toDomainTvShows()
    }

    private fun Single<NetworkResponse<TmdbPagedResponse<TmdbTvShow>, TmdbApiError>>.toDomainTvShows()
        : Observable<LceResponse<List<TvShow>>> {
        return this.toObservable().map {
            it.toDomainLceResponse(false) { response ->
                response.results.map { tvShow -> tvShow.toDomainTvShow() }
            }
        }
    }
}
