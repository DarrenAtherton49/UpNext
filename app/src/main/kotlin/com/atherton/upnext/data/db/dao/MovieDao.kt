package com.atherton.upnext.data.db.dao

import androidx.room.*
import com.atherton.upnext.data.db.model.list.RoomMovieListJoin
import com.atherton.upnext.data.db.model.movie.*
import io.reactivex.Single

@Dao
interface MovieDao {

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

        val existingMovie: RoomMovie? = getMovieForId(movie.id)
        if (existingMovie != null) { // preserve watchlist state etc.
            val updatedMovie: RoomMovie = movie.copy(
                state = existingMovie.state
            )
            updateMovie(updatedMovie)
        } else {
            insertMovie(movie)
        }

        castMembers?.let { insertAllCastMembers(it) }
        crewMembers?.let { insertAllCrewMembers(it) }
        genres?.let { insertAllGenres(it) }
        productionCompanies?.let { insertAllProductionCompanies(it) }
        productionCountries?.let { insertAllProductionCountries(it) }
        spokenLanguages?.let { insertAllSpokenLanguages(it) }
        videos?.let { insertAllVideos(it) }

        val recommendedList = mutableListOf<RoomMovieRecommendationJoin>()
        recommendations?.forEach { recommendedMovie ->

            val existingRecommendedMovie: RoomMovie? = getMovieForId(recommendedMovie.id)
            val recommendationId: Long = if (existingRecommendedMovie != null) { // preserve watchlist state etc.
                val updatedMovie: RoomMovie = recommendedMovie.copy(
                    state = existingRecommendedMovie.state,
                    isModelComplete = existingRecommendedMovie.isModelComplete
                )
                updateMovie(updatedMovie)
                updatedMovie.id
            } else {
                insertMovie(recommendedMovie)
            }

            recommendedList.add(
                RoomMovieRecommendationJoin(
                    movieId = movie.id,
                    recommendationId = recommendationId
                )
            )
        }
        if (recommendedList.isNotEmpty()) {
            insertAllRecommendations(recommendedList)
        }
    }

    @Transaction
    fun insertAllMoviesForPlaylist(movies: List<RoomMovie>, playlistName: String) {

        val playlistId: Long = getPlaylistIdForName(playlistName)
        if (playlistId != 0L) {

            val movieIds: List<Long> = movies.map { playlistMovie ->
                val existingMovie: RoomMovie? = getMovieForId(playlistMovie.id)
                if (existingMovie != null) { // preserve watchlist state etc.
                    val updatedMovie: RoomMovie = playlistMovie.copy(
                        state = existingMovie.state,
                        isModelComplete = existingMovie.isModelComplete
                    )
                    updateMovie(updatedMovie)
                    updatedMovie.id
                } else {
                    insertMovie(playlistMovie)
                }
            }

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
        }
    }

    @Delete
    fun deleteMovieListJoin(movieListJoin: RoomMovieListJoin)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: RoomMovie): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieListJoin(movieListJoin: RoomMovieListJoin)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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

    companion object {
        const val PLAYLIST_POPULAR = "Popular"
        const val PLAYLIST_TOP_RATED = "Top Rated"
        const val PLAYLIST_UPCOMING = "Upcoming"
        const val PLAYLIST_NOW_PLAYING = "Now Playing"
    }
}
