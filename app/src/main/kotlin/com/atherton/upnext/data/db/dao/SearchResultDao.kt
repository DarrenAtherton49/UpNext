package com.atherton.upnext.data.db.dao

import androidx.room.*
import com.atherton.upnext.data.db.model.search.RoomSearchKnownFor
import com.atherton.upnext.data.db.model.search.RoomSearchResult
import com.atherton.upnext.data.db.model.search.RoomSearchResultWithKnownFor
import com.atherton.upnext.data.db.model.search.RoomSearchTerm
import io.reactivex.Observable

@Dao
interface SearchResultDao {

    @Transaction
    fun insertSearchResults(searchTerm: RoomSearchTerm, searchResults: List<Pair<RoomSearchResult, List<RoomSearchKnownFor>?>>) {

        val searchTermId: Long = insertSearchTerm(searchTerm)

        searchResults.forEachIndexed { index, searchResultPair ->

            val (searchResult, knownForList) = searchResultPair

            searchResult.searchTermId = searchTermId
            searchResult.order = index

            val searchResultId: Long = insertSearchResult(searchResult)

            knownForList?.forEach { knownFor ->
                knownFor.searchResultId = searchResultId // defines data needed for join
                insertKnownFor(knownFor)
            }
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchTerm(searchTerm: RoomSearchTerm): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchResult(searchResult: RoomSearchResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKnownFor(knownFor: RoomSearchKnownFor): Long

    @Transaction
    @Query("SELECT sr.* FROM search_result sr WHERE sr.search_term_id = (SELECT id FROM search_term WHERE term = :searchTerm) ORDER BY sr.search_result_order ASC")
    fun getSearchResultsForSearchTerm(searchTerm: String): List<RoomSearchResultWithKnownFor>

    @Transaction
    @Query("SELECT sr.* FROM search_result sr WHERE sr.search_term_id = (SELECT id FROM search_term WHERE term = :searchTerm) ORDER BY sr.search_result_order ASC")
    fun getSearchResultsForSearchTermStream(searchTerm: String): Observable<List<RoomSearchResultWithKnownFor>>
}
