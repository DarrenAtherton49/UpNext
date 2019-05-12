package com.atherton.upnext.data.db.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE



@Entity(
    tableName = "search_term",
    indices = [Index(value = ["id"], unique = true)]
)
data class RoomSearchTerm(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = -1,
    @ColumnInfo(name = "term") var searchTerm: String
)

@Entity(
    tableName = "search_term_result_join",
    primaryKeys = ["search_term_id", "search_result_id"],
    foreignKeys = [
        ForeignKey(entity = RoomSearchTerm::class, parentColumns = ["id"], childColumns = ["search_term_id"]),
        ForeignKey(entity = RoomSearchResult::class, parentColumns = ["id"], childColumns = ["search_result_id"])
    ],
    indices = [
        Index(value = ["search_result_id"], unique = true),
        Index(value = ["search_term_id"], unique = true)
    ]
)
data class RoomSearchTermResultJoin(
    @ColumnInfo(name = "search_result_id") val searchResultId: Long,
    @ColumnInfo(name = "search_term_id") val searchTermId: Long
)

@Entity(
    tableName = "search_result",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class RoomSearchResult(

    // common fields
    @ColumnInfo(name = "adult") var adultContent: Boolean?,
    @ColumnInfo(name = "backdrop_path") var backdropPath: String?,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = -1,
    @ColumnInfo(name = "media_type") var mediaType: String, // either 'tv', 'movie' or 'person'
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "original_language") var originalLanguage: String?,
    @ColumnInfo(name = "overview") var overview: String?,
    @ColumnInfo(name = "popularity") var popularity: Float?,
    @ColumnInfo(name = "poster_path") var posterPath: String?,
    @ColumnInfo(name = "release_date") var releaseDate: String?,
    @ColumnInfo(name = "tmdb_id") var tmdbId: Int,
    @ColumnInfo(name = "vote_average") var voteAverage: Float?,
    @ColumnInfo(name = "vote_count") var voteCount: Int?,

    // TV Show specific fields
    @ColumnInfo(name = "first_air_date") var firstAirDate: String?,
    @ColumnInfo(name = "original_name") var originalName: String?,

    // Movie specific fields
    @ColumnInfo(name = "original_title") var originalTitle: String?,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "video") var video: Boolean?,

    // Person specific fields
    @ColumnInfo(name = "profile_path") var profilePath: String?,

    // Join/utility fields
    @ColumnInfo(name = "search_result_order") var order: Int = -1
)

@Entity(
    tableName = "search_known_for",
    foreignKeys = [ForeignKey(
        entity = RoomSearchResult::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("search_result_id"),
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index(value = ["search_result_id"], unique = true)]
)
data class RoomSearchKnownFor(

    // common fields
    @ColumnInfo(name = "adult") var adultContent: Boolean?,
    @ColumnInfo(name = "backdrop_path") var backdropPath: String?,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = -1,
    @ColumnInfo(name = "media_type") var mediaType: String, // either 'tv', 'movie' or 'person'
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "original_language") var originalLanguage: String?,
    @ColumnInfo(name = "overview") var overview: String?,
    @ColumnInfo(name = "popularity") var popularity: Float?,
    @ColumnInfo(name = "poster_path") var posterPath: String?,
    @ColumnInfo(name = "release_date") var releaseDate: String?,
    @ColumnInfo(name = "tmdb_id") var tmdbId: Int,
    @ColumnInfo(name = "vote_average") var voteAverage: Float?,
    @ColumnInfo(name = "vote_count") var voteCount: Int?,

    // TV Show specific fields
    @ColumnInfo(name = "first_air_date") var firstAirDate: String?,
    @ColumnInfo(name = "original_name") var originalName: String?,

    // Movie specific fields
    @ColumnInfo(name = "original_title") var originalTitle: String?,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "video") var video: Boolean?,

    // join fields
    @ColumnInfo(name = "search_result_id") var searchResultId: Long = -1
)
