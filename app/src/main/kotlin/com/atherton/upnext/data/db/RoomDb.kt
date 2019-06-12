package com.atherton.upnext.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.atherton.upnext.data.db.dao.MovieDao
import com.atherton.upnext.data.db.dao.SearchResultDao
import com.atherton.upnext.data.db.model.movie.*
import com.atherton.upnext.data.db.model.search.RoomSearchKnownFor
import com.atherton.upnext.data.db.model.search.RoomSearchResult
import com.atherton.upnext.data.db.model.search.RoomSearchTerm
import com.atherton.upnext.util.extensions.ioThread

@Database(
    entities = [
        RoomSearchResult::class,
        RoomSearchTerm::class,
        RoomSearchKnownFor::class,
        RoomMovie::class,
        RoomMovieGenre::class,
        RoomProductionCompany::class,
        RoomProductionCountry::class,
        RoomSpokenLanguage::class,
        RoomCastMember::class,
        RoomCrewMember::class,
        RoomVideo::class,
        RoomMovieRecommendationJoin::class,
        RoomMoviePlaylist::class,
        RoomMoviePlaylistJoin::class
    ],
    version = 1
)
abstract class RoomDb : RoomDatabase() {

    abstract fun getSearchResultDao(): SearchResultDao
    abstract fun getMovieDao(): MovieDao

    companion object {
        private const val ROOM_DB_NAME = "tv_movie_database"

        @Volatile private var INSTANCE: RoomDb? = null

        fun getInstance(context: Context, useInMemory: Boolean): RoomDb {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context, useInMemory).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context, useInMemory: Boolean): RoomDb {
            val databaseBuilder = if(useInMemory) {
                Room.inMemoryDatabaseBuilder(context, RoomDb::class.java)
            } else {
                Room.databaseBuilder(context, RoomDb::class.java, ROOM_DB_NAME)
            }
            return databaseBuilder
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        ioThread {
                            prePopulateDatabase(getInstance(context, useInMemory))
                        }
                    }
                })
                .build()
        }

        private fun prePopulateDatabase(roomDb: RoomDb) {
            roomDb.getMovieDao().insertAllPlaylists(PREPOPULATE_MOVIE_PLAYLISTS)
        }

        private val PREPOPULATE_MOVIE_PLAYLISTS = listOf(
            RoomMoviePlaylist(name = "Popular"),
            RoomMoviePlaylist(name = "Top Rated"),
            RoomMoviePlaylist(name = "Upcoming"),
            RoomMoviePlaylist(name = "Now Playing")
        )
    }
}
