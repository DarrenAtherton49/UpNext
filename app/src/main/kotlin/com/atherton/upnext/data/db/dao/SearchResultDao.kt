package com.atherton.upnext.data.db.dao

import androidx.room.*
import com.atherton.upnext.data.db.model.RoomSearchKnownFor
import com.atherton.upnext.data.db.model.RoomSearchResult
import com.atherton.upnext.data.db.model.RoomSearchTerm
import com.atherton.upnext.data.db.model.RoomSearchTermResultJoin

@Dao
interface SearchResultDao {

    @Transaction
    fun insertSearchResults(searchTerm: RoomSearchTerm, searchResults: List<Pair<RoomSearchResult, List<RoomSearchKnownFor>>>) {

        val searchTermId: Long = insertSearchTerm(searchTerm)

        searchResults.forEachIndexed { index, searchResultPair ->

            val (searchResult, knownForList) = searchResultPair

            searchResult.order = index

            val searchResultId: Long = insertSearchResult(searchResult)

            knownForList.forEach { knownFor ->
                knownFor.searchResultId = searchResultId // defines data needed for join
                insertKnownFor(knownFor)
            }

            val searchTermResultJoin = RoomSearchTermResultJoin(
                searchResultId = searchResultId,
                searchTermId = searchTermId
            )
            insertSearchTermResultJoin(searchTermResultJoin)
        }
    }

    @Transaction
    fun getSearchResultsAndKnownFor(searchTerm: String): List<Pair<RoomSearchResult, List<RoomSearchKnownFor>>> {
        val resultsAndKnownFor = mutableListOf<Pair<RoomSearchResult, List<RoomSearchKnownFor>>>()
        val results: List<RoomSearchResult> = getSearchResults(searchTerm)
        results.forEach { result ->
            resultsAndKnownFor.add(Pair(result, getKnownForList(result.id)))
        }
        return resultsAndKnownFor.toList()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchTerm(searchTerm: RoomSearchTerm): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchResult(searchResult: RoomSearchResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKnownFor(knownFor: RoomSearchKnownFor): Long

    @Insert
    fun insertSearchTermResultJoin(searchTermResultJoin: RoomSearchTermResultJoin)

    @Query("SELECT search_result.* " +
                  "FROM search_result " +
                  "INNER JOIN search_term_result_join on search_result_id = " +
                  "(SELECT id FROM search_term WHERE term = :searchTerm) " +
                  "ORDER BY search_result.search_result_order ASC")
    fun getSearchResults(searchTerm: String): List<RoomSearchResult>

    @Query("SELECT * " +
                 "FROM search_known_for " +
                 "WHERE search_result_id = :searchResultId")
    fun getKnownForList(searchResultId: Long): List<RoomSearchKnownFor>
}
