package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.TvShow
import io.reactivex.Observable

interface TvShowRepository {

    fun getTvShow(id: Long): Observable<LceResponse<TvShow>>
    fun getPopular(): Observable<LceResponse<List<TvShow>>>
    fun getTopRated(): Observable<LceResponse<List<TvShow>>>
    fun getAiringToday(): Observable<LceResponse<List<TvShow>>>
    fun getOnTheAir(): Observable<LceResponse<List<TvShow>>>
}