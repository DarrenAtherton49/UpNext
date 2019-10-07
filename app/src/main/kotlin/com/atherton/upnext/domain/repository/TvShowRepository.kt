package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.ContentList
import com.atherton.upnext.domain.model.ContentListStatus
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.TvShow
import io.reactivex.Observable

interface TvShowRepository {

    fun getTvShow(id: Long): Observable<LceResponse<TvShow>>
    fun getPopular(): Observable<LceResponse<List<TvShow>>>
    fun getTopRated(): Observable<LceResponse<List<TvShow>>>
    fun getAiringToday(): Observable<LceResponse<List<TvShow>>>
    fun getOnTheAir(): Observable<LceResponse<List<TvShow>>>
    fun getTvShowListsForTvShow(showId: Long): Observable<LceResponse<List<ContentListStatus>>>
    fun getTvShowLists(): Observable<LceResponse<List<ContentList>>>
    fun getTvShowsForList(listId: Long): Observable<LceResponse<List<TvShow>>>
    fun toggleTvShowWatchlistStatus(tvShowId: Long): Observable<LceResponse<TvShow>>
    fun toggleTvShowWatchedStatus(tvShowId: Long): Observable<LceResponse<TvShow>>
    fun toggleTvShowListStatus(tvShowId: Long, listId: Long): Observable<LceResponse<TvShow>>
    fun createTvShowList(tvShowId: Long?, listTitle: String): Observable<LceResponse<Long>>
}
