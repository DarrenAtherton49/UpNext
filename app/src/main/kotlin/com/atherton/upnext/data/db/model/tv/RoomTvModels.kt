package com.atherton.upnext.data.db.model.tv

import androidx.room.*

private const val ID = "id"
private const val SHOW_ID = "show_id"

class RoomTvShowAllData {
    
    @Embedded
    var tvShow: RoomTvShow? = null

    @Relation(parentColumn = ID, entityColumn = SHOW_ID, entity = RoomTvShowCastMember::class)
    var cast: List<RoomTvShowCastMember> = ArrayList()

    @Relation(parentColumn = ID, entityColumn = SHOW_ID, entity = RoomTvShowCreatedBy::class)
    var createdBy: List<RoomTvShowCreatedBy> = ArrayList()

    @Relation(parentColumn = ID, entityColumn = SHOW_ID, entity = RoomTvShowCrewMember::class)
    var crew: List<RoomTvShowCrewMember> = ArrayList()

    @Relation(parentColumn = ID, entityColumn = SHOW_ID, entity = RoomTvShowGenre::class)
    var genres: List<RoomTvShowGenre> = ArrayList()

    @Relation(parentColumn = ID, entityColumn = SHOW_ID, entity = RoomTvShowProductionCompany::class)
    var productionCompanies: List<RoomTvShowProductionCompany> = ArrayList()

    @Relation(parentColumn = ID, entityColumn = SHOW_ID, entity = RoomTvShowNetwork::class)
    var networks: List<RoomTvShowNetwork> = ArrayList()

    @Relation(parentColumn = ID, entityColumn = SHOW_ID, entity = RoomTvShowSeason::class)
    var seasons: List<RoomTvShowSeason> = ArrayList()

    @Relation(parentColumn = ID, entityColumn = SHOW_ID, entity = RoomTvShowVideo::class)
    var videos: List<RoomTvShowVideo> = ArrayList()

    // tv show recommendations can be obtained using the RoomTvShowRecommendationJoin table (many to many)
}

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Entity(
    tableName = "tv_show"
)
data class RoomTvShow(

    // base/search fields
    @ColumnInfo(name = "backdrop_path") val backdropPath: String?,
    @ColumnInfo(name = "first_air_date") val firstAirDate: String?,
    @PrimaryKey @ColumnInfo(name = ID) val id: Long,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "origin_countries") val originCountries: List<String>?,
    @ColumnInfo(name = "original_language") val originalLanguage: String?,
    @ColumnInfo(name = "original_name") val originalName: String?,
    @ColumnInfo(name = "overview") val overview: String?,
    @ColumnInfo(name = "poster_path") val posterPath: String?,
    @ColumnInfo(name = "popularity") val popularity: Float?,
    @ColumnInfo(name = "vote_average") val voteAverage: Float?,
    @ColumnInfo(name = "vote_count") val voteCount: Int?,

    // detail fields (more detail fields can be found in class RoomTvShowAllData)
    @ColumnInfo(name = "episode_run_times") val runTimes: List<Int>?,
    @ColumnInfo(name = "homepage") val homepage: String?,
    @ColumnInfo(name = "in_production") val inProduction: Boolean?,
    @ColumnInfo(name = "languages") val languages: List<String>?,
    @ColumnInfo(name = "last_air_date") val lastAirDate: String?,
    @Embedded(prefix = "last_episode_to_air") val lastEpisodeToAir: RoomTvShowLastEpisodeToAir?,
    @ColumnInfo(name = "number_of_episodes") val numberOfEpisodes: Int?,
    @ColumnInfo(name = "number_of_seasons") val numberOfSeasons: Int?,
    @ColumnInfo(name = "status") val status: String?,
    @ColumnInfo(name = "type") val type: String?,

    @Embedded(prefix = "state") val state: RoomTvShowState = RoomTvShowState(),

    @ColumnInfo(name = "is_model_complete") val isModelComplete: Boolean
)

data class RoomTvShowState(
    @ColumnInfo(name = "in_watchlist") val inWatchlist: Boolean = false,
    @ColumnInfo(name = "is_watched") val isWatched: Boolean = false
)

@Entity(
    tableName = "tv_show_genre",
    indices = [
        Index(value = [SHOW_ID], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomTvShow::class,
            parentColumns = [ID],
            childColumns = [SHOW_ID],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomTvShowGenre(

    @PrimaryKey @ColumnInfo(name = ID) val id: Long,
    @ColumnInfo(name = "name") val name: String?,

    // Join/utility field
    @ColumnInfo(name = SHOW_ID) val showId: Long
)

@Entity(
    tableName = "tv_show_production_company",
    indices = [
        Index(value = [SHOW_ID], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomTvShow::class,
            parentColumns = [ID],
            childColumns = [SHOW_ID],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomTvShowProductionCompany(
    @PrimaryKey @ColumnInfo(name = ID) val id: Long,
    @ColumnInfo(name = "logo_path") val logoPath: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "origin_country") val originCountry: String?,

    // Join/utility field
    @ColumnInfo(name = SHOW_ID) val showId: Long
)

@Entity(
    tableName = "tv_show_recommendation_join",
    primaryKeys = [
        SHOW_ID,
        "recommendation_id"
    ],
    indices = [
        Index(value = ["recommendation_id"], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomTvShow::class,
            parentColumns = [ID],
            childColumns = [SHOW_ID]
        ),
        ForeignKey(
            entity = RoomTvShow::class,
            parentColumns = [ID],
            childColumns = ["recommendation_id"]
        )
    ]
)
data class RoomTvShowRecommendationJoin(

    @ColumnInfo(name = SHOW_ID) val showId: Long,
    @ColumnInfo(name = "recommendation_id") val recommendationId: Long
)

@Entity(
    tableName = "tv_show_cast_member",
    indices = [
        Index(value = [SHOW_ID], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomTvShow::class,
            parentColumns = [ID],
            childColumns = [SHOW_ID],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomTvShowCastMember(
    @ColumnInfo(name = "cast_id") val castId: Int?,
    @ColumnInfo(name = "character") val character: String?,
    @ColumnInfo(name = "credit_id") val creditId: String?,
    @ColumnInfo(name = "gender") val gender: Int?, // 1 = female, 2 = male
    @PrimaryKey @ColumnInfo(name = ID) val id: Long,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "order") val order: Int?,
    @ColumnInfo(name = "profile_path") val profilePath: String?,

    // Join/utility field
    @ColumnInfo(name = SHOW_ID) val showId: Long
)

@Entity(
    tableName = "tv_show_crew_member",
    indices = [
        Index(value = [SHOW_ID], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomTvShow::class,
            parentColumns = [ID],
            childColumns = [SHOW_ID],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomTvShowCrewMember(
    @ColumnInfo(name = "credit_id") val creditId: String?,
    @ColumnInfo(name = "department") val department: String?,
    @ColumnInfo(name = "gender") val gender: Int?, // 1 = female, 2 = male
    @PrimaryKey @ColumnInfo(name = ID) val id: Long,
    @ColumnInfo(name = "job") val job: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "profile_path") val profilePath: String?,

    // Join/utility field
    @ColumnInfo(name = SHOW_ID) val showId: Long
)

@Entity(
    tableName = "tv_show_video",
    indices = [
        Index(value = [SHOW_ID], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomTvShow::class,
            parentColumns = [ID],
            childColumns = [SHOW_ID],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomTvShowVideo(
    @PrimaryKey @ColumnInfo(name = ID) val id: String,
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "site") val site: String?,
    @ColumnInfo(name = "size") val size: Int?, // 360, 480, 720 or 1080
    @ColumnInfo(name = "type") val type: String?, // Trailer, Teaser, Clip, Featurette

    @ColumnInfo(name = SHOW_ID) val showId: Long
)

@Entity(
    tableName = "tv_show_created_by",
    indices = [
        Index(value = [SHOW_ID], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomTvShow::class,
            parentColumns = [ID],
            childColumns = [SHOW_ID],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomTvShowCreatedBy(
    @PrimaryKey @ColumnInfo(name = ID) val id: Long,
    @ColumnInfo(name = "credit_id") val creditId: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "gender") val gender: Int?, // 1 = female, 2 = male
    @ColumnInfo(name = "profile_path") val profilePath: String?,

    @ColumnInfo(name = SHOW_ID) val showId: Long
)

data class RoomTvShowLastEpisodeToAir(
    @ColumnInfo(name = "air_date") val airDate: String?,
    @ColumnInfo(name = "episode_number") val episodeNumber: Int?,
    @PrimaryKey @ColumnInfo(name = ID) val id: Long,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "overview") val overview: String?,
    @ColumnInfo(name = "production_code") val productionCode: String?,
    @ColumnInfo(name = "season_number") val seasonNumber: Int?,
    @ColumnInfo(name = "show_id") val showId: Int?,
    @ColumnInfo(name = "still_path") val stillPath: String?,
    @ColumnInfo(name = "vote_average") val voteAverage: Float?,
    @ColumnInfo(name = "vote_count") val voteCount: Int?
)

@Entity(
    tableName = "tv_show_network",
    indices = [
        Index(value = [SHOW_ID], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomTvShow::class,
            parentColumns = [ID],
            childColumns = [SHOW_ID],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomTvShowNetwork(
    @ColumnInfo(name = "headquarters") val headquarters: String?,
    @ColumnInfo(name = "homepage") val homepage: String?,
    @PrimaryKey @ColumnInfo(name = ID) val id: Long,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "origin_country") val originCountry: String?,

    @ColumnInfo(name = SHOW_ID) val showId: Long
)

@Entity(
    tableName = "tv_show_season",
    indices = [
        Index(value = [SHOW_ID], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomTvShow::class,
            parentColumns = [ID],
            childColumns = [SHOW_ID],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomTvShowSeason(
    @ColumnInfo(name = "air_date") val airDate: String?,
    @ColumnInfo(name = "episode_count") val episodeCount: Int?,
    @PrimaryKey @ColumnInfo(name = ID) val id: Long,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "overview") val overview: String?,
    @ColumnInfo(name = "poster_path") val posterPath: String?,
    @ColumnInfo(name = "season_number") val seasonNumber: Int?,
    @ColumnInfo(name = "watch_count") val watchCount: Int = 0,

    @ColumnInfo(name = SHOW_ID) val showId: Long
)

@Entity(
    tableName = "tv_show_playlist"
)
data class RoomTvShowPlaylist(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) var id: Long = 0,
    @ColumnInfo(name = "name") val name: String
)

@Entity(
    tableName = "tv_show_playlist_join",
    primaryKeys = [
        SHOW_ID,
        "playlist_id"
    ],
    indices = [
        Index(value = ["playlist_id"], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomTvShow::class,
            parentColumns = [ID],
            childColumns = [SHOW_ID]
        ),
        ForeignKey(
            entity = RoomTvShowPlaylist::class,
            parentColumns = [ID],
            childColumns = ["playlist_id"]
        )
    ]
)
data class RoomTvShowPlaylistJoin(

    @ColumnInfo(name = SHOW_ID) val showId: Long,
    @ColumnInfo(name = "playlist_id") val playlistId: Long
)
