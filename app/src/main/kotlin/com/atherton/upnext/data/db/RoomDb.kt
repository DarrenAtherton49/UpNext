package com.atherton.upnext.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.atherton.upnext.data.db.dao.SearchResultDao
import com.atherton.upnext.data.db.model.RoomSearchKnownFor
import com.atherton.upnext.data.db.model.RoomSearchResult
import com.atherton.upnext.data.db.model.RoomSearchTerm
import com.atherton.upnext.data.db.model.RoomSearchTermResultJoin

@Database(
    entities = [RoomSearchResult::class, RoomSearchTerm::class, RoomSearchKnownFor::class, RoomSearchTermResultJoin::class],
    version = 1
)
abstract class RoomDb : RoomDatabase() {

    abstract fun getSearchResultDao(): SearchResultDao
}
