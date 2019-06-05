package com.atherton.upnext.data.db.model.search

import androidx.room.*
import androidx.room.ForeignKey.CASCADE


class RoomSearchResultWithKnownFor {

    @Embedded
    lateinit var searchResult: RoomSearchResult

    @Relation(parentColumn = "id", entityColumn = "search_result_id", entity = RoomSearchKnownFor::class)
    lateinit var knownFor: List<RoomSearchKnownFor>
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
        Index(value = ["id"], unique = true),
        Index(value = ["search_term_id", "media_type", "tmdb_id"], unique = true)
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
    @ColumnInfo(name = "adult") var adultContent: Boolean?,
    @ColumnInfo(name = "backdrop_path") var backdropPath: String?,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0,
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
    @ColumnInfo(name = "search_term_id") var searchTermId: Long = -1,
    @ColumnInfo(name = "search_result_order") var order: Int = -1
)

@Entity(
    tableName = "search_known_for",
    indices = [Index(value = ["search_result_id"], unique = true)]
)
data class RoomSearchKnownFor(

    // common fields
    @ColumnInfo(name = "adult") var adultContent: Boolean?,
    @ColumnInfo(name = "backdrop_path") var backdropPath: String?,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
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
