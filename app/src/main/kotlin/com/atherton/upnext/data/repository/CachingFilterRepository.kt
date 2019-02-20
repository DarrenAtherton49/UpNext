package com.atherton.upnext.data.repository

import com.atherton.upnext.domain.model.DiscoverFilter
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.repository.FilterRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingFilterRepository @Inject constructor() : FilterRepository {

    override fun getFilters(): Single<Response<List<DiscoverFilter>>> {
        return Single.fromCallable(this::tempFilters)
    }

    private fun tempFilters(): Response<List<DiscoverFilter>> {
        return Response.Success(listOf(
            DiscoverFilter.Preset.NowPlayingMovies,
            DiscoverFilter.Preset.AiringTodayTv,
            DiscoverFilter.Preset.OnTheAirTv,
            DiscoverFilter.Preset.PopularTvMovies,
            DiscoverFilter.Preset.TopRatedTvMovies,
            DiscoverFilter.Preset.UpcomingMovies
        ), true)
    }
}
