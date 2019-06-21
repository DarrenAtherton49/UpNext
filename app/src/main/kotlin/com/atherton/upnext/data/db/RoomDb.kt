package com.atherton.upnext.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.atherton.upnext.data.db.dao.*
import com.atherton.upnext.data.db.dao.ListDao.Companion.LIST_MOVIE_WATCHED
import com.atherton.upnext.data.db.dao.ListDao.Companion.LIST_MOVIE_WATCHLIST
import com.atherton.upnext.data.db.dao.ListDao.Companion.LIST_TV_HISTORY
import com.atherton.upnext.data.db.dao.ListDao.Companion.LIST_TV_WATCHED
import com.atherton.upnext.data.db.dao.ListDao.Companion.LIST_TV_WATCHLIST
import com.atherton.upnext.data.db.model.config.RoomConfig
import com.atherton.upnext.data.db.model.list.RoomMovieList
import com.atherton.upnext.data.db.model.list.RoomMovieListJoin
import com.atherton.upnext.data.db.model.list.RoomTvShowList
import com.atherton.upnext.data.db.model.list.RoomTvShowListJoin
import com.atherton.upnext.data.db.model.movie.*
import com.atherton.upnext.data.db.model.person.RoomPerson
import com.atherton.upnext.data.db.model.person.RoomPersonCredit
import com.atherton.upnext.data.db.model.search.RoomSearchResult
import com.atherton.upnext.data.db.model.search.RoomSearchTerm
import com.atherton.upnext.data.db.model.tv.*
import com.atherton.upnext.util.extensions.ioThread

@Database(
    entities = [
        RoomSearchResult::class,
        RoomSearchTerm::class,
        RoomMovie::class,
        RoomMovieGenre::class,
        RoomMovieProductionCompany::class,
        RoomProductionCountry::class,
        RoomSpokenLanguage::class,
        RoomMovieCastMember::class,
        RoomMovieCrewMember::class,
        RoomMovieVideo::class,
        RoomMovieRecommendationJoin::class,
        RoomMoviePlaylist::class,
        RoomMoviePlaylistJoin::class,
        RoomMovieList::class,
        RoomMovieListJoin::class,
        RoomTvShow::class,
        RoomTvShowGenre::class,
        RoomTvShowProductionCompany::class,
        RoomTvShowCastMember::class,
        RoomTvShowCrewMember::class,
        RoomTvShowCreatedBy::class,
        RoomTvShowNetwork::class,
        RoomTvShowSeason::class,
        RoomTvShowVideo::class,
        RoomTvShowRecommendationJoin::class,
        RoomTvShowPlaylist::class,
        RoomTvShowPlaylistJoin::class,
        RoomTvShowList::class,
        RoomTvShowListJoin::class,
        RoomPerson::class,
        RoomPersonCredit::class,
        RoomConfig::class
    ],
    version = 1
)
@TypeConverters(RoomTypeConverters::class)
abstract class RoomDb : RoomDatabase() {

    abstract fun getSearchResultDao(): SearchResultDao
    abstract fun getMovieDao(): MovieDao
    abstract fun getTvShowDao(): TvShowDao
    abstract fun getPersonDao(): PersonDao
    abstract fun getListDao(): ListDao
    abstract fun getConfigDao(): ConfigDao

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
            roomDb.getTvShowDao().insertAllPlaylists(PREPOPULATE_TV_SHOW_PLAYLISTS)
            roomDb.getListDao().insertAllMovieLists(PREPOPULATE_MOVIE_CUSTOM_LISTS)
            roomDb.getListDao().insertAllTvShowLists(PREPOPULATE_TV_SHOW_CUSTOM_LISTS)
        }

        private val PREPOPULATE_MOVIE_PLAYLISTS by lazy {
            listOf(
                RoomMoviePlaylist(name = MovieDao.PLAYLIST_POPULAR),
                RoomMoviePlaylist(name = MovieDao.PLAYLIST_TOP_RATED),
                RoomMoviePlaylist(name = MovieDao.PLAYLIST_UPCOMING),
                RoomMoviePlaylist(name = MovieDao.PLAYLIST_NOW_PLAYING)
            )
        }

        private val PREPOPULATE_TV_SHOW_PLAYLISTS by lazy {
            listOf(
                RoomTvShowPlaylist(name = TvShowDao.PLAYLIST_POPULAR),
                RoomTvShowPlaylist(name = TvShowDao.PLAYLIST_TOP_RATED),
                RoomTvShowPlaylist(name = TvShowDao.PLAYLIST_AIRING_TODAY),
                RoomTvShowPlaylist(name = TvShowDao.PLAYLIST_ON_THE_AIR)
            )
        }

        private val PREPOPULATE_MOVIE_CUSTOM_LISTS by lazy {
            listOf(
                RoomMovieList(name = LIST_MOVIE_WATCHLIST, sortOrder = 1),
                RoomMovieList(name = LIST_MOVIE_WATCHED, sortOrder = 2)
            )
        }

        private val PREPOPULATE_TV_SHOW_CUSTOM_LISTS by lazy {
            listOf(
                RoomTvShowList(name = LIST_TV_WATCHLIST, sortOrder = 1),
                RoomTvShowList(name = LIST_TV_WATCHED, sortOrder = 2),
                RoomTvShowList(name = LIST_TV_HISTORY, sortOrder = 3)
            )
        }
    }
}
