package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.Searchable
import com.atherton.upnext.domain.model.Response
import io.reactivex.Single

interface SearchRepository {

    /**
     * Search for tv shows, movies and people in one call.
     *
     * @param query the query to search for
     */
    fun searchMulti(query: String): Single<Response<List<Searchable>>>
}