package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.Response
import io.reactivex.Single

interface MovieRepository {

    fun getPopular(): Single<Response<List<Movie>>>
    fun getUpcoming(): Single<Response<List<Movie>>>
    fun getTopRated(): Single<Response<List<Movie>>>
    fun getNowPlaying(): Single<Response<List<Movie>>>
}
