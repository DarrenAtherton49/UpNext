package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Searchable
import io.reactivex.Observable

interface SearchRepository {

    /**
     * Search for tv shows, movies and people in one call.
     *
     * @param query the query to search for
     */
    fun searchMulti(query: String): Observable<LceResponse<List<Searchable>>>
}
