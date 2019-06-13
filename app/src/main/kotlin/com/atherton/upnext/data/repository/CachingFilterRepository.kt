package com.atherton.upnext.data.repository

import com.atherton.upnext.domain.model.DiscoverFilter
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.repository.FilterRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingFilterRepository @Inject constructor() : FilterRepository {

    override fun getFiltersObservable(): Single<LceResponse<List<DiscoverFilter>>> {
        return Single.fromCallable(this::tempFilters)
    }

    //todo replace this with user filters from database combined with presets
    private fun tempFilters(): LceResponse<List<DiscoverFilter>> {
        return LceResponse.Content(listOf(
            DiscoverFilter.Preset.NowPlayingMovies,
            DiscoverFilter.Preset.AiringTodayTv,
            DiscoverFilter.Preset.OnTheAirTv,
            DiscoverFilter.Preset.PopularTvMovies,
            DiscoverFilter.Preset.TopRatedTvMovies,
            DiscoverFilter.Preset.UpcomingMovies
        ))
    }
}
