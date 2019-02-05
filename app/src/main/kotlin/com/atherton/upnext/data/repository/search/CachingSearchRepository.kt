package com.atherton.upnext.data.repository.search

import com.atherton.upnext.data.api.TmdbSearchService
import com.atherton.upnext.data.mapper.toDomainSearchModels
import com.atherton.upnext.data.model.SearchModel
import com.atherton.upnext.data.repository.Response
import com.atherton.upnext.data.repository.toDomainResponse
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingSearchRepository @Inject constructor(
    //todo add in-memory cache
    //todo add database
    private val searchService: TmdbSearchService
) : SearchRepository {

    override fun searchMulti(query: String): Single<Response<List<SearchModel>>> {
        return searchService.searchMulti(query)
            .map {
                it.toDomainResponse(false) { response -> response.results.toDomainSearchModels() }
            }
    }
}
