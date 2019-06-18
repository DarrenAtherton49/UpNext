package com.atherton.upnext.data.db.model.list

import androidx.room.*
import com.atherton.upnext.data.db.model.movie.RoomMovie
import com.atherton.upnext.data.db.model.tv.RoomTvShow

private const val ID = "id"
private const val MOVIE_ID = "movie_id"
private const val SHOW_ID = "show_id"
private const val LIST_ID = "list_id"

@Entity(
    tableName = "movie_list"
)
data class RoomMovieList(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) var id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "sort_order") val sortOrder: Int
)

@Entity(
    tableName = "movie_list_join",
    primaryKeys = [
        MOVIE_ID,
        LIST_ID
    ],
    indices = [
        Index(value = [LIST_ID], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomMovie::class,
            parentColumns = [ID],
            childColumns = [MOVIE_ID]
        ),
        ForeignKey(
            entity = RoomMovieList::class,
            parentColumns = [ID],
            childColumns = [LIST_ID]
        )
    ]
)
data class RoomMovieListJoin(
    @ColumnInfo(name = MOVIE_ID) val movieId: Long,
    @ColumnInfo(name = LIST_ID) val listId: Long
)

@Entity(
    tableName = "tv_show_list"
)
data class RoomTvShowList(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) var id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "sort_order") val sortOrder: Int
)

@Entity(
    tableName = "tv_show_list_join",
    primaryKeys = [
        SHOW_ID,
        LIST_ID
    ],
    indices = [
        Index(value = [LIST_ID], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomTvShow::class,
            parentColumns = [ID],
            childColumns = [SHOW_ID]
        ),
        ForeignKey(
            entity = RoomTvShowList::class,
            parentColumns = [ID],
            childColumns = [LIST_ID]
        )
    ]
)
data class RoomTvShowListJoin(
    @ColumnInfo(name = SHOW_ID) val showId: Long,
    @ColumnInfo(name = LIST_ID) val listId: Long
)
