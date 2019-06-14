package com.atherton.upnext.data.db.dao

import androidx.room.*
import com.atherton.upnext.data.db.model.tv.*
import io.reactivex.Single

@Dao
interface TvShowDao {

    @Transaction
    fun insertTvShowData(
        tvShow: RoomTvShow,
        genres: List<RoomTvShowGenre>?,
        productionCompanies: List<RoomTvShowProductionCompany>?,
        castMembers: List<RoomTvShowCastMember>?,
        crewMembers: List<RoomTvShowCrewMember>?,
        createdBy: List<RoomTvShowCreatedBy>?,
        networks: List<RoomTvShowNetwork>?,
        seasons: List<RoomTvShowSeason>?,
        recommendations: List<RoomTvShow>?,
        videos: List<RoomTvShowVideo>?
    ) {
        insertTvShow(tvShow)

        genres?.let { insertAllGenres(it) }
        productionCompanies?.let { insertAllProductionCompanies(it) }
        castMembers?.let { insertAllCastMembers(it) }
        crewMembers?.let { insertAllCrewMembers(it) }
        createdBy?.let { insertAllCreatedBy(it) }
        networks?.let { insertAllNetworks(it) }
        seasons?.let { insertAllSeasons(it) }
        videos?.let { insertAllVideos(it) }

        val recommendedList = mutableListOf<RoomTvShowRecommendationJoin>()
        recommendations?.forEach { recommendedTvShow ->
            val recommendationId = insertTvShow(recommendedTvShow)
            recommendedList.add(
                RoomTvShowRecommendationJoin(
                    showId = tvShow.id,
                    recommendationId = recommendationId
                )
            )
        }
        if (recommendedList.isNotEmpty()) {
            insertAllRecommendations(recommendedList)
        }
    }

    @Transaction
    fun insertAllTvShowsForPlaylist(tvShows: List<RoomTvShow>, playlistName: String) {

        val playlistId: Long = getPlaylistIdForName(playlistName)
        if (playlistId != 0L) {
            val tvShowIds: List<Long> = insertAllTvShows(tvShows)
            val tvShowPlaylistJoins: List<RoomTvShowPlaylistJoin> = tvShowIds.map { tvShowId ->
                RoomTvShowPlaylistJoin(
                    showId = tvShowId,
                    playlistId = playlistId
                )
            }
            insertAllTvShowPlaylistJoins(tvShowPlaylistJoins)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTvShow(tvShow: RoomTvShow): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTvShows(tvShows: List<RoomTvShow>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllGenres(genres: List<RoomTvShowGenre>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllProductionCompanies(productionCompanies: List<RoomTvShowProductionCompany>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCastMembers(castMembers: List<RoomTvShowCastMember>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCrewMembers(crewMembers: List<RoomTvShowCrewMember>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllVideos(videos: List<RoomTvShowVideo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCreatedBy(createdBy: List<RoomTvShowCreatedBy>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllNetworks(networks: List<RoomTvShowNetwork>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllSeasons(seasons: List<RoomTvShowSeason>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllRecommendations(recommendations: List<RoomTvShowRecommendationJoin>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPlaylists(playlists: List<RoomTvShowPlaylist>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTvShowPlaylistJoins(tvShowPlaylistJoins: List<RoomTvShowPlaylistJoin>)

    // we use a List here because RxJava 2 can't emit nulls if the tv show doesn't exist
    @Transaction
    @Query("SELECT * FROM tv_show WHERE id = :id")
    fun getTvShowListForIdSingle(id: Long): Single<List<RoomTvShow>>

    @Transaction
    @Query("SELECT * FROM tv_show WHERE id = :id")
    fun getFullTvShowForId(id: Long): RoomTvShowAllData?

    @Transaction
    @Query("SELECT tv2.* FROM tv_show_recommendation_join tvrj INNER JOIN tv_show tv1 ON tvrj.show_id = tv1.id INNER JOIN tv_show tv2 ON tvrj.recommendation_id = tv2.id WHERE tvrj.show_id = :showId")
    fun getRecommendationsForTvShow(showId: Long): List<RoomTvShow>

    @Transaction
    @Query("SELECT tv.* FROM tv_show_playlist_join tvpj INNER JOIN tv_show tv ON tvpj.show_id = tv.id INNER JOIN tv_show_playlist p ON tvpj.playlist_id = p.id WHERE p.id = (SELECT id from tv_show_playlist WHERE name = :playlistName) ORDER BY tv.popularity")
    fun getTvShowsForPlaylist(playlistName: String): List<RoomTvShow>

    @Transaction
    @Query("SELECT tv.* FROM tv_show_playlist_join tvpj INNER JOIN tv_show tv ON tvpj.show_id = tv.id INNER JOIN tv_show_playlist p ON tvpj.playlist_id = p.id WHERE p.id = (SELECT id from tv_show_playlist WHERE name = :playlistName) ORDER BY tv.popularity")
    fun getTvShowsForPlaylistSingle(playlistName: String): Single<List<RoomTvShow>>

    @Query("SELECT id from tv_show_playlist WHERE name = :playlistName")
    fun getPlaylistIdForName(playlistName: String): Long
}
