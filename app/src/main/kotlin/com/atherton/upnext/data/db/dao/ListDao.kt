package com.atherton.upnext.data.db.dao

import androidx.room.*
import com.atherton.upnext.data.db.model.list.RoomMovieList
import com.atherton.upnext.data.db.model.list.RoomTvShowList
import com.atherton.upnext.data.db.model.movie.RoomMovieAllData
import com.atherton.upnext.data.db.model.tv.RoomTvShowAllData
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface ListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieList(movieList: RoomMovieList): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTvShowList(tvShowList: RoomTvShowList): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMovieLists(movieLists: List<RoomMovieList>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTvShowLists(tvShowLists: List<RoomTvShowList>)

    @Query("SELECT MAX(sort_order) FROM movie_list")
    fun getHighestMovieListOrderSingle(): Single<Int>

    @Query("SELECT MAX(sort_order) FROM tv_show_list")
    fun getHighestTvShowListOrderSingle(): Single<Int>

    @Query("SELECT * FROM movie_list ORDER BY sort_order")
    fun getMovieLists(): List<RoomMovieList>

    @Query("SELECT * FROM movie_list ORDER BY sort_order")
    fun getMovieListsObservable(): Observable<List<RoomMovieList>>

    @Query("SELECT * FROM tv_show_list ORDER BY sort_order")
    fun getTvShowListsObservable(): Observable<List<RoomTvShowList>>

    @Query("SELECT l.* from movie_list_join mlj INNER JOIN movie m on mlj.movie_id = m.id INNER JOIN movie_list l ON mlj.list_id = l.id WHERE m.id = :movieId")
    fun getListsForMovie(movieId: Long): List<RoomMovieList>

    @Query("SELECT l.* FROM tv_show_list_join tvlj INNER JOIN tv_show s ON tvlj.show_id = s.id INNER JOIN tv_show_list l ON tvlj.list_id = l.id WHERE s.id = :showId")
    fun getListsForTvShow(showId: Long): List<RoomTvShowList>

    @Transaction
    @Query("SELECT m.* FROM movie_list_join mlj INNER JOIN movie m ON mlj.movie_id = m.id INNER JOIN movie_list l ON mlj.list_id = l.id WHERE l.id = :listId")
    fun getMoviesForListObservable(listId: Long): Observable<List<RoomMovieAllData>>

    @Transaction
    @Query("SELECT s.* FROM tv_show_list_join tvlj INNER JOIN tv_show s ON tvlj.show_id = s.id INNER JOIN tv_show_list l ON tvlj.list_id = l.id WHERE l.id = :listId")
    fun getTvShowsForListObservable(listId: Long): Observable<List<RoomTvShowAllData>>

    companion object {
        const val LIST_ID_MOVIE_WATCHLIST = 1L
        const val LIST_ID_MOVIE_WATCHED = 2L
        const val LIST_NAME_MOVIE_WATCHLIST = "Watchlist"
        const val LIST_NAME_MOVIE_WATCHED = "Watched"

        const val LIST_ID_TV_WATCHLIST = 1L
        const val LIST_ID_TV_WATCHED = 2L
        const val LIST_ID_TV_HISTORY = 3L
        const val LIST_NAME_TV_WATCHLIST = "Watchlist"
        const val LIST_NAME_TV_WATCHED = "Watched"
        const val LIST_NAME_TV_HISTORY = "History"
    }
}
