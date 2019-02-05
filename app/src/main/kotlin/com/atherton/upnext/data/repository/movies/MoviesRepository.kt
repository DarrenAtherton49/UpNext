package com.atherton.upnext.data.repository.movies

import com.atherton.upnext.data.model.Movie
import com.atherton.upnext.data.repository.Response
import io.reactivex.Single

interface MoviesRepository {

    fun popular(): Single<Response<List<Movie>>>
}