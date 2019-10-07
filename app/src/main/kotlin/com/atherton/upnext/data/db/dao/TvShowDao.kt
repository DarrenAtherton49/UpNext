package com.atherton.upnext.data.db.dao

import androidx.room.*
import com.atherton.upnext.data.db.model.list.RoomTvShowListJoin
import com.atherton.upnext.data.db.model.tv.*
import io.reactivex.Single

private const val ROW_NOT_INSERTED: Long = -1L

@Dao
interface TvShowDao {

    /**
     * Function to insert a tv show or update a tv show if the movie already exists.
     * If the insert returns '-1', the tv show already exists so we need to fetch it, copy it's watchlist state
     * and then update the tv show in the database.
     * Returns the id of the tv show.
     */
    @Transaction
    fun insertOrUpdateTvShow(newTvShow: RoomTvShow): Long {
        val id: Long = insertTvShow(newTvShow)
        return if (id == ROW_NOT_INSERTED) { // tv show already exists, so preserve watchlist state etc.

            val existingTvShow: RoomTvShowMinimal = getMinimalTvShowForId(newTvShow.id)

            // we do this so that we don't overwrite a 'true' with a 'false', e.g. when we go to tv show detail,
            // it becomes true, but then the same tv show is a recommendation for another tv show and is false,
            // we still want it to be true as the tv show data will still exist in the foreign tables
            // (e.g. genre, cast).
            val isModelComplete = newTvShow.isModelComplete || existingTvShow.isModelComplete

            val updatedTvShow: RoomTvShow = newTvShow.copy(
                isModelComplete = isModelComplete,
                state = existingTvShow.state
            )
            updateTvShow(updatedTvShow)
            updatedTvShow.id
        } else {
            id
        }
    }

    /**
     * Function to insert new tv shows and update any tv shows that already exist.
     * Returns the list of tv show id's which have been either inserted or updated.
     */
    @Transaction
    fun insertOrUpdateTvShows(newTvShows: List<RoomTvShow>): List<Long> {

        if (newTvShows.isNotEmpty()) {

            val ids: List<Long> = insertAllTvShows(newTvShows)

            // tv shows that already exist, so preserve watchlist state etc.
            val tvShowsToUpdate = mutableListOf<RoomTvShow>()
            ids.forEachIndexed { index, id ->
                if (id == ROW_NOT_INSERTED) {
                    tvShowsToUpdate.add(newTvShows[index])
                }
            }

            // copy state from each existing tv show to each new tv show
            val updatedTvShows: List<RoomTvShow> = tvShowsToUpdate.map { tvShowToUpdate ->

                val existingTvShow: RoomTvShowMinimal = getMinimalTvShowForId(tvShowToUpdate.id)

                // we do this so that we don't overwrite a 'true' with a 'false', e.g. when we go to tv show detail,
                // it becomes true, but then the same tv show is a recommendation for another tv show and is false,
                // we still want it to be true as the tv show data will still exist in the foreign tables
                // (e.g. genre, cast).
                val isModelComplete = tvShowToUpdate.isModelComplete || existingTvShow.isModelComplete

                tvShowToUpdate.copy(
                    isModelComplete = isModelComplete,
                    state = existingTvShow.state
                )
            }

            if (updatedTvShows.isNotEmpty()) {
                updateTvShows(updatedTvShows)
            }
        }

        return newTvShows.map { tvShow -> tvShow.id }
    }

    @Transaction
    fun insertFullTvShowData(
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

        insertOrUpdateTvShow(tvShow)

        genres?.let { insertAllGenres(it) }
        productionCompanies?.let { insertAllProductionCompanies(it) }
        castMembers?.let { insertAllCastMembers(it) }
        crewMembers?.let { insertAllCrewMembers(it) }
        createdBy?.let { insertAllCreatedBy(it) }
        networks?.let { insertAllNetworks(it) }
        seasons?.let { insertAllSeasons(it) }
        videos?.let { insertAllVideos(it) }

        if (recommendations != null) {
            val tvShowIds: List<Long> = insertOrUpdateTvShows(recommendations)
            val recommendedJoinList: List<RoomTvShowRecommendationJoin> = tvShowIds.map { recommendedTvShowId ->
                RoomTvShowRecommendationJoin(
                    showId = tvShow.id,
                    recommendationId = recommendedTvShowId
                )
            }

            if (recommendedJoinList.isNotEmpty()) {
                insertAllRecommendations(recommendedJoinList)
            }
        }
    }

    @Transaction
    fun insertAllTvShowsForPlaylist(newTvShows: List<RoomTvShow>, playlistName: String) {

        val playlistId: Long = getPlaylistIdForName(playlistName)
        if (playlistId != 0L) {

            val tvShowIds: List<Long> = insertOrUpdateTvShows(newTvShows)

            val tvShowPlaylistJoins: List<RoomTvShowPlaylistJoin> = tvShowIds.map { tvShowId ->
                RoomTvShowPlaylistJoin(
                    showId = tvShowId,
                    playlistId = playlistId
                )
            }
            insertAllTvShowPlaylistJoins(tvShowPlaylistJoins)
        } else {
            throw IllegalStateException("Invalid playlist id")
        }
    }

    @Transaction
    fun updateShowAndToggleListStatus(updatedShow: RoomTvShow, listId: Long) {
        updateTvShow(updatedShow)
        toggleTvShowListStatus(updatedShow.id, listId)
    }

    /**
     * Function to toggle whether or not a tv show is joined to a list. First we try to insert a join from
     * the show id to the list id. If the insert fails, we can assume that the show is in the list. Thus,
     * 'toggle' in this instance means to delete, so we delete the join. If the insert succeeds, we can assume
     * that 'toggle' means to add the tv show to the list.
     */
    @Transaction
    fun toggleTvShowListStatus(showId: Long, listId: Long) {

        if (listId != 0L) {

            val tvShowListJoin = RoomTvShowListJoin(
                showId = showId,
                listId = listId
            )

            // conflict strategy is ignore, so if it already exists then it won't insert it.
            // which then means that movie is in list, so toggle means delete
            val id: Long = insertTvShowListJoin(tvShowListJoin)
            if (id == ROW_NOT_INSERTED) {
                deleteTvShowListJoin(tvShowListJoin)
            }
        } else {
            throw IllegalStateException("Invalid list id")
        }
    }

    @Delete
    fun deleteTvShowListJoin(tvShowListJoin: RoomTvShowListJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTvShow(tvShow: RoomTvShow): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTvShowListJoin(tvShowListJoin: RoomTvShowListJoin): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
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
    fun getTvShowForIdSingle(id: Long): Single<List<RoomTvShow>>

    // we use this cut-down version of movie e.g. when preserving watchlist state as part of a movie update
    @Transaction
    @Query("SELECT id, is_model_complete, state_in_watchlist, state_is_watched FROM tv_show WHERE id = :id")
    fun getMinimalTvShowForId(id: Long): RoomTvShowMinimal

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

    @Update
    fun updateTvShow(tvShow: RoomTvShow)

    @Update
    fun updateTvShows(tvShows: List<RoomTvShow>)

    companion object {
        const val PLAYLIST_POPULAR = "Popular"
        const val PLAYLIST_TOP_RATED = "Top Rated"
        const val PLAYLIST_AIRING_TODAY = "Airing Today"
        const val PLAYLIST_ON_THE_AIR = "On The Air"
    }
}
