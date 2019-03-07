package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.TvShow
import io.reactivex.Observable

interface TvShowRepository {

    fun getTvShow(id: Int): Observable<Response<TvShow>>
    fun getPopular(): Observable<Response<List<TvShow>>>
    fun getTopRated(): Observable<Response<List<TvShow>>>
    fun getAiringToday(): Observable<Response<List<TvShow>>>
    fun getOnTheAir(): Observable<Response<List<TvShow>>>
}