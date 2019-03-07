package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.Response
import io.reactivex.Observable

interface MovieRepository {

    fun getMovie(id: Int): Observable<Response<Movie>>
    fun getPopular(): Observable<Response<List<Movie>>>
    fun getUpcoming(): Observable<Response<List<Movie>>>
    fun getTopRated(): Observable<Response<List<Movie>>>
    fun getNowPlaying(): Observable<Response<List<Movie>>>
}
