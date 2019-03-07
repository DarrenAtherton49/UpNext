package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.Searchable
import io.reactivex.Observable

interface SearchRepository {

    /**
     * Search for tv shows, movies and people in one call.
     *
     * @param query the query to search for
     */
    fun searchMulti(query: String): Observable<Response<List<Searchable>>>
}