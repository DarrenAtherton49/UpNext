package com.atherton.upnext.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.atherton.upnext.data.db.dao.SearchResultDao
import com.atherton.upnext.data.db.model.search.RoomSearchKnownFor
import com.atherton.upnext.data.db.model.search.RoomSearchResult
import com.atherton.upnext.data.db.model.search.RoomSearchTerm

@Database(
    entities = [RoomSearchResult::class, RoomSearchTerm::class, RoomSearchKnownFor::class],
    version = 1
)
abstract class RoomDb : RoomDatabase() {

    abstract fun getSearchResultDao(): SearchResultDao
}
