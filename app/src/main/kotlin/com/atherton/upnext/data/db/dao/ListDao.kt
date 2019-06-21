package com.atherton.upnext.data.db.dao

import androidx.room.*
import com.atherton.upnext.data.db.model.list.RoomMovieList
import com.atherton.upnext.data.db.model.list.RoomTvShowList
import com.atherton.upnext.data.db.model.movie.RoomMovieAllData
import io.reactivex.Single

@Dao
interface ListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieList(movieList: RoomMovieList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTvShowList(tvShowList: RoomTvShowList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMovieLists(movieLists: List<RoomMovieList>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTvShowLists(tvShowLists: List<RoomTvShowList>)

    @Query("SELECT id from movie_list WHERE name = :listName")
    fun getListIdForName(listName: String): Long

    @Query("SELECT * from movie_list ORDER BY sort_order")
    fun getMovieListsSingle(): Single<List<RoomMovieList>>

    @Query("SELECT * from tv_show_list ORDER BY sort_order")
    fun getTvShowListsSingle(): Single<List<RoomTvShowList>>

    @Transaction
    @Query("SELECT m.* FROM movie_list_join mlj INNER JOIN movie m on mlj.movie_id = m.id INNER JOIN movie_list l ON mlj.list_id = l.id WHERE l.id = :listId")
    fun getMoviesForListSingle(listId: Long): Single<List<RoomMovieAllData>>

    companion object {
        const val LIST_MOVIE_WATCHLIST = "Watchlist"
        const val LIST_MOVIE_WATCHED = "Watched"

        const val LIST_TV_WATCHLIST = "Watchlist"
        const val LIST_TV_WATCHED = "Watched"
        const val LIST_TV_HISTORY = "History"
    }
}
