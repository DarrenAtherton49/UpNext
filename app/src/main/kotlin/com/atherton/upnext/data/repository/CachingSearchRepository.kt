package com.atherton.upnext.data.repository

import com.atherton.upnext.data.mapper.toDomainResponse
import com.atherton.upnext.data.mapper.toDomainSearchables
import com.atherton.upnext.data.network.service.TmdbSearchService
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.Searchable
import com.atherton.upnext.domain.repository.SearchRepository
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingSearchRepository @Inject constructor(
    //todo add in-memory cache
    private val searchService: TmdbSearchService
) : SearchRepository {

    override fun searchMulti(query: String): Observable<Response<List<Searchable>>> {
        return searchService.searchMulti(query)
            .toObservable()
            .map {
                it.toDomainResponse(false) { response -> response.results.toDomainSearchables() }
            }
    }
}
