package com.atherton.upnext.data.repository

import com.atherton.upnext.data.db.dao.SearchResultDao
import com.atherton.upnext.data.db.model.search.RoomSearchResult
import com.atherton.upnext.data.db.model.search.RoomSearchTerm
import com.atherton.upnext.data.mapper.toDomainLceResponse
import com.atherton.upnext.data.mapper.toDomainSearchables
import com.atherton.upnext.data.mapper.toRoomSearchResults
import com.atherton.upnext.data.network.model.NetworkResponse
import com.atherton.upnext.data.network.model.TmdbMultiSearchResult
import com.atherton.upnext.data.network.service.TmdbSearchService
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Searchable
import com.atherton.upnext.domain.repository.SearchRepository
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingSearchRepository @Inject constructor(
    private val searchResultDao: SearchResultDao,
    private val searchService: TmdbSearchService
) : SearchRepository {

    /*
     * 1) Saves search term into database
     * 2) Fetches the search results from the network
     * 3) Stores search term in database
     * 4) Stores search results in database
     * 5) Stores search results 'known for' in database
     * 6) Stores search term and results join id's in database
     * 6) Retrieves search results from database (db is source of truth) and maps then to domain objects
     */
    override fun searchMulti(query: String): Observable<LceResponse<List<Searchable>>> {
        return searchService.searchMulti(query)
            .toObservable()
            .doOnNext { networkResponse ->
                if (networkResponse is NetworkResponse.Success) {
                    val networkSearchResults: List<TmdbMultiSearchResult> = networkResponse.body.results
                    searchResultDao.insertSearchResults(
                        searchTerm = RoomSearchTerm(searchTerm = query),
                        searchResults = networkSearchResults.toRoomSearchResults()
                    )
                }
            }
            .map { networkResponse ->
                val dbResults: List<RoomSearchResult> = searchResultDao.getSearchResultsForSearchTerm(query)
                val domainResults: List<Searchable> = dbResults.toDomainSearchables()
                networkResponse.toDomainLceResponse(data = domainResults)
            }
    }
}
