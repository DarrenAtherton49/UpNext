package com.atherton.upnext.data.repository.search

import com.atherton.upnext.data.model.SearchModel
import com.atherton.upnext.data.repository.Response
import io.reactivex.Single

interface SearchRepository {

    /**
     * Search for tv shows, movies and people in one call.
     *
     * @param query the query to search for
     */
    fun searchMulti(query: String): Single<Response<List<SearchModel>>>
}