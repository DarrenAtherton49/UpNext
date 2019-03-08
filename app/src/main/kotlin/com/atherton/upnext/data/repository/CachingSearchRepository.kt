package com.atherton.upnext.data.repository

import com.atherton.upnext.data.mapper.toDomainLceResponse
import com.atherton.upnext.data.mapper.toDomainSearchables
import com.atherton.upnext.data.network.service.TmdbSearchService
import com.atherton.upnext.domain.model.LceResponse
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

    override fun searchMulti(query: String): Observable<LceResponse<List<Searchable>>> {
        return searchService.searchMulti(query)
            .toObservable()
            .map {
                it.toDomainLceResponse(false) { response -> response.results.toDomainSearchables() }
            }
    }
}
