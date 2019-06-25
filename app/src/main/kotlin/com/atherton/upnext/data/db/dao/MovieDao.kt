package com.atherton.upnext.data.db.dao

import androidx.room.*
import com.atherton.upnext.data.db.model.list.RoomMovieListJoin
import com.atherton.upnext.data.db.model.movie.*
import io.reactivex.Single

private const val ROW_NOT_INSERTED: Long = -1L

@Dao
interface MovieDao {

    /**
     * Function to insert a movie or update a movie if the movie already exists.
     * If the insert returns '-1', the movie already exists so we need to fetch it, copy it's watchlist state
     * and then update the movie in the database.
     * Returns the id of the movie.
     */
    @Transaction
    fun insertOrUpdateMovie(newMovie: RoomMovie): Long {
        val id: Long = insertMovie(newMovie)
        return if (id == ROW_NOT_INSERTED) { // movie already exists, so preserve watchlist state etc.
            val existingMovie: RoomMovieMinimal = getMinimalMovieForId(newMovie.id)
            val updatedMovie: RoomMovie = newMovie.copy(
                isModelComplete = existingMovie.isModelComplete,
                state = existingMovie.state
            )
            updateMovie(updatedMovie)
            updatedMovie.id
        } else {
            id
        }
    }

    /**
     * Function to insert new movies and update any movies that already exist.
     * Returns the list of movie id's which have been either inserted or updated.
     */
    @Transaction
    fun insertOrUpdateMovies(newMovies: List<RoomMovie>): List<Long> {

        if (newMovies.isNotEmpty()) {

            val ids: List<Long> = insertAllMovies(newMovies)

            // movies that already exist, so preserve watchlist state etc.
            val moviesToUpdate = mutableListOf<RoomMovie>()
            ids.forEachIndexed { index, id ->
                if (id == ROW_NOT_INSERTED) {
                    moviesToUpdate.add(newMovies[index])
                }
            }

            // copy state from each existing movie to each new movie
            val updatedMovies: List<RoomMovie> = moviesToUpdate.map { movieToUpdate ->
                val existingMovie: RoomMovieMinimal = getMinimalMovieForId(movieToUpdate.id)
                movieToUpdate.copy(
                    isModelComplete = existingMovie.isModelComplete,
                    state = existingMovie.state
                )
            }

            if (updatedMovies.isNotEmpty()) {
                updateMovies(updatedMovies)
            }
        }

        return newMovies.map { movie -> movie.id }
    }

    @Transaction
    fun insertFullMovieData(
        movie: RoomMovie,
        castMembers: List<RoomMovieCastMember>?,
        crewMembers: List<RoomMovieCrewMember>?,
        genres: List<RoomMovieGenre>?,
        productionCompanies: List<RoomMovieProductionCompany>?,
        productionCountries: List<RoomProductionCountry>?,
        spokenLanguages: List<RoomSpokenLanguage>?,
        recommendations: List<RoomMovie>?,
        videos: List<RoomMovieVideo>?
    ) {

        insertOrUpdateMovie(movie)

        castMembers?.let { insertAllCastMembers(it) }
        crewMembers?.let { insertAllCrewMembers(it) }
        genres?.let { insertAllGenres(it) }
        productionCompanies?.let { insertAllProductionCompanies(it) }
        productionCountries?.let { insertAllProductionCountries(it) }
        spokenLanguages?.let { insertAllSpokenLanguages(it) }
        videos?.let { insertAllVideos(it) }


        if (recommendations != null) {
            val movieIds: List<Long> = insertOrUpdateMovies(recommendations)
            val recommendedJoinList: List<RoomMovieRecommendationJoin> = movieIds.map { recommendedMovieId ->
                RoomMovieRecommendationJoin(
                    movieId = movie.id,
                    recommendationId = recommendedMovieId
                )
            }

            if (recommendedJoinList.isNotEmpty()) {
                insertAllRecommendations(recommendedJoinList)
            }
        }
    }

    @Transaction
    fun insertAllMoviesForPlaylist(newMovies: List<RoomMovie>, playlistName: String) {

        val playlistId: Long = getPlaylistIdForName(playlistName)
        if (playlistId != 0L) {

            val movieIds: List<Long> = insertOrUpdateMovies(newMovies)

            val moviePlaylistJoins: List<RoomMoviePlaylistJoin> = movieIds.map { movieId ->
                RoomMoviePlaylistJoin(
                    movieId = movieId,
                    playlistId = playlistId
                )
            }
            insertAllMoviePlaylistJoins(moviePlaylistJoins)
        } else {
            throw IllegalStateException("Invalid playlist id")
        }
    }

    @Transaction
    fun toggleMovieWatchlistStatus(updatedMovie: RoomMovie, listId: Long) {

        if (listId != 0L) {
            updateMovie(updatedMovie)

            val movieWatchlistJoin = RoomMovieListJoin(
                movieId = updatedMovie.id,
                listId = listId
            )

            if (updatedMovie.state.inWatchlist) {
                insertMovieListJoin(movieWatchlistJoin)
            } else {
                deleteMovieListJoin(movieWatchlistJoin)
            }
        } else {
            throw IllegalStateException("Invalid list id")
        }
    }

    @Delete
    fun deleteMovieListJoin(movieListJoin: RoomMovieListJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovie(movie: RoomMovie): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieListJoin(movieListJoin: RoomMovieListJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllMovies(movies: List<RoomMovie>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllGenres(genres: List<RoomMovieGenre>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllProductionCompanies(productionCompanies: List<RoomMovieProductionCompany>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllProductionCountries(productionCountries: List<RoomProductionCountry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllSpokenLanguages(spokenLanguages: List<RoomSpokenLanguage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCastMembers(castMembers: List<RoomMovieCastMember>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCrewMembers(crewMembers: List<RoomMovieCrewMember>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllVideos(videos: List<RoomMovieVideo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllRecommendations(recommendations: List<RoomMovieRecommendationJoin>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPlaylists(playlists: List<RoomMoviePlaylist>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMoviePlaylistJoins(moviePlaylistJoins: List<RoomMoviePlaylistJoin>)

    // we use a List here because RxJava 2 can't emit nulls if the movie doesn't exist
    @Transaction
    @Query("SELECT * FROM movie WHERE id = :id")
    fun getMovieForIdSingle(id: Long): Single<List<RoomMovie>>

    @Transaction
    @Query("SELECT * FROM movie WHERE id = :id")
    fun getMovieForId(id: Long): RoomMovie?

    // we use this cut-down version of movie e.g. when preserving watchlist state as part of a movie update
    @Transaction
    @Query("SELECT id, is_model_complete, state_in_watchlist, state_is_watched FROM movie WHERE id = :id")
    fun getMinimalMovieForId(id: Long): RoomMovieMinimal

    @Transaction
    @Query("SELECT * FROM movie WHERE id = :id")
    fun getFullMovieForId(id: Long): RoomMovieAllData?

    // we use a List here because RxJava 2 can't emit nulls if the movie doesn't exist
    @Transaction
    @Query("SELECT * FROM movie WHERE id = :id")
    fun getFullMovieForIdSingle(id: Long): Single<List<RoomMovieAllData>>

    @Transaction
    @Query("SELECT m2.* FROM movie_recommendation_join mrj INNER JOIN movie m1 ON mrj.movie_id = m1.id INNER JOIN movie m2 ON mrj.recommendation_id = m2.id WHERE mrj.movie_id = :movieId")
    fun getRecommendationsForMovie(movieId: Long): List<RoomMovie>

    @Transaction
    @Query("SELECT m.* FROM movie_playlist_join mpj INNER JOIN movie m ON mpj.movie_id = m.id INNER JOIN movie_playlist p ON mpj.playlist_id = p.id WHERE p.id = (SELECT id from movie_playlist WHERE name = :playlistName) ORDER BY m.popularity")
    fun getMoviesForPlaylist(playlistName: String): List<RoomMovie>

    @Transaction
    @Query("SELECT m.* FROM movie_playlist_join mpj INNER JOIN movie m ON mpj.movie_id = m.id INNER JOIN movie_playlist p ON mpj.playlist_id = p.id WHERE p.id = (SELECT id from movie_playlist WHERE name = :playlistName) ORDER BY m.popularity")
    fun getMoviesForPlaylistSingle(playlistName: String): Single<List<RoomMovie>>

    @Query("SELECT id from movie_playlist WHERE name = :playlistName")
    fun getPlaylistIdForName(playlistName: String): Long

    @Update
    fun updateMovie(movie: RoomMovie)

    @Update
    fun updateMovies(movies: List<RoomMovie>)

    companion object {
        const val PLAYLIST_POPULAR = "Popular"
        const val PLAYLIST_TOP_RATED = "Top Rated"
        const val PLAYLIST_UPCOMING = "Upcoming"
        const val PLAYLIST_NOW_PLAYING = "Now Playing"
    }
}
