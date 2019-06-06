package com.atherton.upnext.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    companion object {
        private const val ROOM_DB_NAME = "tv_movie_database"

        fun create(context: Context, useInMemory : Boolean): RoomDb {
            val databaseBuilder = if(useInMemory) {
                Room.inMemoryDatabaseBuilder(context, RoomDb::class.java)
            } else {
                Room.databaseBuilder(context, RoomDb::class.java, ROOM_DB_NAME)
            }
            return databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
