package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.Response
import io.reactivex.Single

interface MoviesRepository {

    fun popular(): Single<Response<List<Movie>>>
}