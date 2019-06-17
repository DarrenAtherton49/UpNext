package com.atherton.upnext.data.db.dao

import androidx.room.*
import com.atherton.upnext.data.db.model.movie.*
import io.reactivex.Single

@Dao
interface MovieDao {

    @Transaction
    fun insertMovieData(
        movie: RoomMovie,
        movieStatus: RoomMovieStatus,
        castMembers: List<RoomMovieCastMember>?,
        crewMembers: List<RoomMovieCrewMember>?,
        genres: List<RoomMovieGenre>?,
        productionCompanies: List<RoomMovieProductionCompany>?,
        productionCountries: List<RoomProductionCountry>?,
        spokenLanguages: List<RoomSpokenLanguage>?,
        recommendations: List<RoomMovie>?,
        videos: List<RoomMovieVideo>?
    ) {
        insertMovie(movie)
        insertMovieStatus(movieStatus)

        castMembers?.let { insertAllCastMembers(it) }
        crewMembers?.let { insertAllCrewMembers(it) }
        genres?.let { insertAllGenres(it) }
        productionCompanies?.let { insertAllProductionCompanies(it) }
        productionCountries?.let { insertAllProductionCountries(it) }
        spokenLanguages?.let { insertAllSpokenLanguages(it) }
        videos?.let { insertAllVideos(it) }

        val recommendedList = mutableListOf<RoomMovieRecommendationJoin>()
        recommendations?.forEach { recommendedMovie ->
            val recommendationId = insertMovie(recommendedMovie)
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
            val movieIds: List<Long> = insertAllMovies(movies)
            val moviePlaylistJoins: List<RoomMoviePlaylistJoin> = movieIds.map { movieId ->
                RoomMoviePlaylistJoin(
                    movieId = movieId,
                    playlistId = playlistId
                )
            }
            insertAllMoviePlaylistJoins(moviePlaylistJoins)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: RoomMovie): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieStatus(movieStatus: RoomMovieStatus)

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
    fun getMovieListForIdSingle(id: Long): Single<List<RoomMovie>>

    @Transaction
    @Query("SELECT * FROM movie WHERE id = :id")
    fun getFullMovieForId(id: Long): RoomMovieAllData?

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
}
