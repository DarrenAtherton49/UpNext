package com.atherton.upnext.data.db.model.movie

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

class RoomMovieAllData {

    @Embedded
    lateinit var movie: RoomMovie

    @Relation(parentColumn = "id", entityColumn = "movie_id", entity = RoomMovieGenre::class)
    var genres: List<RoomMovieGenre> = ArrayList()

    @Relation(parentColumn = "id", entityColumn = "movie_id", entity = RoomProductionCompany::class)
    var productionCompanies: List<RoomProductionCompany> = ArrayList()

    @Relation(parentColumn = "id", entityColumn = "movie_id", entity = RoomProductionCountry::class)
    var productionCountries: List<RoomProductionCountry> = ArrayList()

    @Relation(parentColumn = "id", entityColumn = "movie_id", entity = RoomSpokenLanguage::class)
    var spokenLanguages: List<RoomSpokenLanguage> = ArrayList()

    @Relation(parentColumn = "id", entityColumn = "movie_id", entity = RoomCastMember::class)
    var cast: List<RoomCastMember> = ArrayList()

    @Relation(parentColumn = "id", entityColumn = "movie_id", entity = RoomCrewMember::class)
    var crew: List<RoomCrewMember> = ArrayList()

    @Relation(parentColumn = "id", entityColumn = "movie_id", entity = RoomVideo::class)
    var videos: List<RoomVideo> = ArrayList()

    // movie recommendations can be obtained using the RoomMovieRecommendationJoin table (many to many)
}

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Entity(
    tableName = "movie"
)
data class RoomMovie(

    // base/search fields
    @ColumnInfo(name = "adult") val adultContent: Boolean?,
    @ColumnInfo(name = "backdrop_path") val backdropPath: String?,
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "original_language") val originalLanguage: String?,
    @ColumnInfo(name = "original_title") val originalTitle: String?,
    @ColumnInfo(name = "overview") val overview: String?,
    @ColumnInfo(name = "popularity") val popularity: Float?,
    @ColumnInfo(name = "poster_path") val posterPath: String?,
    @ColumnInfo(name = "release_date") val releaseDate: String?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "video") val video: Boolean?,
    @ColumnInfo(name = "vote_average") val voteAverage: Float?,
    @ColumnInfo(name = "vote_count") val voteCount: Int?,

    // detail fields (more detail fields can be found in class RoomMovieAllData)
    @Embedded(prefix = "belongs_to_collection") val belongsToCollection: RoomMovieCollection?,
    @ColumnInfo(name = "budget") val budget: Int?,
    @ColumnInfo(name = "homepage") val homepage: String?,
    @ColumnInfo(name = "imdb_id") val imdbId: String?,
    @ColumnInfo(name = "revenue") val revenue: Int?,
    @ColumnInfo(name = "runtime") val runtime: Int?,
    @ColumnInfo(name = "status") val status: String?,
    @ColumnInfo(name = "tagline") val tagline: String?,

    @ColumnInfo(name = "is_model_complete") val isModelComplete: Boolean
)

data class RoomMovieCollection(

    @ColumnInfo(name = "backdrop_path") val backdropPath: String?,
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "poster_path") val posterPath: String?
)

@Entity(
    tableName = "movie_genre",
    indices = [
        Index(value = ["movie_id"], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomMovie::class,
            parentColumns = ["id"],
            childColumns = ["movie_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ]
)
data class RoomMovieGenre(

    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String?,

    // Join/utility field
    @ColumnInfo(name = "movie_id") val movieId: Long
)

@Entity(
    tableName = "production_company",
    indices = [
        Index(value = ["movie_id"], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomMovie::class,
            parentColumns = ["id"],
            childColumns = ["movie_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ]
)
data class RoomProductionCompany(
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "logo_path") val logoPath: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "origin_country") val originCountry: String?,

    // Join/utility field
    @ColumnInfo(name = "movie_id") val movieId: Long
)

@Entity(
    tableName = "production_country",
    indices = [
        Index(value = ["movie_id"], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomMovie::class,
            parentColumns = ["id"],
            childColumns = ["movie_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ]
)
data class RoomProductionCountry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0,
    @ColumnInfo(name = "iso_3166_1") val iso31661: String?,
    @ColumnInfo(name = "name") val name: String?,

    // Join/utility field
    @ColumnInfo(name = "movie_id") val movieId: Long
)

@Entity(
    tableName = "spoken_language",
    indices = [
        Index(value = ["movie_id"], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomMovie::class,
            parentColumns = ["id"],
            childColumns = ["movie_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ]
)
data class RoomSpokenLanguage(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0,
    @ColumnInfo(name = "iso_639_1") val iso6391: String?,
    @ColumnInfo(name = "name") val name: String?,

    // Join/utility field
    @ColumnInfo(name = "movie_id") val movieId: Long
)

@Entity(
    tableName = "cast_member",
    indices = [
        Index(value = ["movie_id"], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomMovie::class,
            parentColumns = ["id"],
            childColumns = ["movie_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ]
)
data class RoomCastMember(
    @ColumnInfo(name = "cast_id") val castId: Int?,
    @ColumnInfo(name = "character") val character: String?,
    @ColumnInfo(name = "credit_id") val creditId: String?,
    @ColumnInfo(name = "gender") val gender: Int?, // 1 = female, 2 = male
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "order") val order: Int?,
    @ColumnInfo(name = "profile_path") val profilePath: String?,

    // Join/utility field
    @ColumnInfo(name = "movie_id") val movieId: Long
)

@Entity(
    tableName = "crew_member",
    indices = [
        Index(value = ["movie_id"], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomMovie::class,
            parentColumns = ["id"],
            childColumns = ["movie_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ]
)
data class RoomCrewMember(
    @ColumnInfo(name = "credit_id") val creditId: String?,
    @ColumnInfo(name = "department") val department: String?,
    @ColumnInfo(name = "gender") val gender: Int?, // 1 = female, 2 = male
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "job") val job: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "profile_path") val profilePath: String?,

    // Join/utility field
    @ColumnInfo(name = "movie_id") val movieId: Long = 0
)

@Entity(
    tableName = "movie_recommendation_join",
    primaryKeys = [
        "movie_id",
        "recommendation_id"
    ],
    indices = [
        Index(value = ["recommendation_id"], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomMovie::class,
            parentColumns = ["id"],
            childColumns = ["movie_id"]
        ),
        ForeignKey(
            entity = RoomMovie::class,
            parentColumns = ["id"],
            childColumns = ["recommendation_id"]
        )
    ]
)
data class RoomMovieRecommendationJoin(

    @ColumnInfo(name = "movie_id") val movieId: Long,
    @ColumnInfo(name = "recommendation_id") val recommendationId: Long
)

@Entity(
    tableName = "video",
    indices = [
        Index(value = ["movie_id"], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomMovie::class,
            parentColumns = ["id"],
            childColumns = ["movie_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ]
)
data class RoomVideo(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "site") val site: String?,
    @ColumnInfo(name = "size") val size: Int?, // 360, 480, 720 or 1080
    @ColumnInfo(name = "type") val type: String?, // Trailer, Teaser, Clip, Featurette

    @ColumnInfo(name = "movie_id") val movieId: Long
)

@Entity(
    tableName = "movie_playlist"
)
data class RoomMoviePlaylist(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0,
    @ColumnInfo(name = "name") val name: String
)

@Entity(
    tableName = "movie_playlist_join",
    primaryKeys = [
        "movie_id",
        "playlist_id"
    ],
    indices = [
        Index(value = ["playlist_id"], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomMovie::class,
            parentColumns = ["id"],
            childColumns = ["movie_id"]
        ),
        ForeignKey(
            entity = RoomMoviePlaylist::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"]
        )
    ]
)
data class RoomMoviePlaylistJoin(

    @ColumnInfo(name = "movie_id") val movieId: Long,
    @ColumnInfo(name = "playlist_id") val playlistId: Long
)
