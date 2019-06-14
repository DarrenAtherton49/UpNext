package com.atherton.upnext.data.db.model.search

import androidx.room.*
import androidx.room.ForeignKey.CASCADE


class RoomSearchResultWithKnownFor {

    @Embedded
    var searchResult: RoomSearchResult? = null

    @Relation(
        parentColumn = "id",
        entityColumn = "search_result_id",
        entity = RoomSearchKnownFor::class
    )
    var knownFor: List<RoomSearchKnownFor> = ArrayList()
}

@Entity(
    tableName = "search_term",
    indices = [
        Index(value = ["term"], unique = true)
    ]
)
data class RoomSearchTerm(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0,
    @ColumnInfo(name = "term") var searchTerm: String
)

@Entity(
    tableName = "search_result",
    indices = [
        Index(value = ["search_term_id", "media_type", "id"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomSearchTerm::class,
            parentColumns = ["id"],
            childColumns = ["search_term_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ]
)
data class RoomSearchResult(

    // common fields
    @ColumnInfo(name = "adult") val adultContent: Boolean?,
    @ColumnInfo(name = "backdrop_path") val backdropPath: String?,
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "media_type") val mediaType: String, // either 'tv', 'movie' or 'person'
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "original_language") val originalLanguage: String?,
    @ColumnInfo(name = "overview") val overview: String?,
    @ColumnInfo(name = "popularity") val popularity: Float?,
    @ColumnInfo(name = "poster_path") val posterPath: String?,
    @ColumnInfo(name = "release_date") val releaseDate: String?,
    @ColumnInfo(name = "vote_average") val voteAverage: Float?,
    @ColumnInfo(name = "vote_count") val voteCount: Int?,

    // TV Show specific fields
    @ColumnInfo(name = "first_air_date") val firstAirDate: String?,
    @ColumnInfo(name = "original_name") val originalName: String?,

    // Movie specific fields
    @ColumnInfo(name = "original_title") val originalTitle: String?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "video") val video: Boolean?,

    // Person specific fields
    @ColumnInfo(name = "profile_path") val profilePath: String?,

    // Join/utility fields
    @ColumnInfo(name = "search_term_id") var searchTermId: Long = 0,
    @ColumnInfo(name = "search_result_order") var order: Int = -0
)

@Entity(
    tableName = "search_known_for",
    indices = [Index(value = ["search_result_id"], unique = true)]
)
data class RoomSearchKnownFor(

    // common fields
    @ColumnInfo(name = "adult") val adultContent: Boolean?,
    @ColumnInfo(name = "backdrop_path") val backdropPath: String?,
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "media_type") val mediaType: String, // either 'tv', 'movie' or 'person'
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "original_language") val originalLanguage: String?,
    @ColumnInfo(name = "overview") val overview: String?,
    @ColumnInfo(name = "popularity") val popularity: Float?,
    @ColumnInfo(name = "poster_path") val posterPath: String?,
    @ColumnInfo(name = "release_date") val releaseDate: String?,
    @ColumnInfo(name = "vote_average") val voteAverage: Float?,
    @ColumnInfo(name = "vote_count") val voteCount: Int?,

    // TV Show specific fields
    @ColumnInfo(name = "first_air_date") val firstAirDate: String?,
    @ColumnInfo(name = "original_name") val originalName: String?,

    // Movie specific fields
    @ColumnInfo(name = "original_title") val originalTitle: String?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "video") val video: Boolean?,

    // join fields
    @ColumnInfo(name = "search_result_id") var searchResultId: Long = 0
)
