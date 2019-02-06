package com.atherton.upnext.data.repository

import com.atherton.upnext.data.api.TmdbSearchService
import com.atherton.upnext.data.mapper.toDomainResponse
import com.atherton.upnext.data.mapper.toDomainSearchModels
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModel
import com.atherton.upnext.domain.repository.SearchRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingSearchRepository @Inject constructor(
    //todo add in-memory cache
    private val searchService: TmdbSearchService
) : SearchRepository {

    override fun searchMulti(query: String): Single<Response<List<SearchModel>>> {
        return searchService.searchMulti(query)
            .map {
                it.toDomainResponse(false) { response -> response.results.toDomainSearchModels() }
            }
    }
}
