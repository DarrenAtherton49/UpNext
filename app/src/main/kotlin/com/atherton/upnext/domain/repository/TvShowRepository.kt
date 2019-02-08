package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.TvShow
import io.reactivex.Single

interface TvShowRepository {

    fun popular(): Single<Response<List<TvShow>>>
}