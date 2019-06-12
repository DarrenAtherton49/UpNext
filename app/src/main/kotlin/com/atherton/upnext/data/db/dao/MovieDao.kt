package com.atherton.upnext.data.db.dao

import androidx.room.*
import com.atherton.upnext.data.db.model.movie.*

@Dao
interface MovieDao {

    @Transaction
    fun insertMovieData(
        movie: RoomMovie,
        genres: List<RoomMovieGenre>?,
        productionCompanies: List<RoomProductionCompany>?,
        productionCountries: List<RoomProductionCountry>?,
        spokenLanguages: List<RoomSpokenLanguage>?,
        castMembers: List<RoomCastMember>?,
        crewMembers: List<RoomCrewMember>?,
        recommendations: List<RoomMovie>?,
        videos: List<RoomVideo>?
    ) {
        insertMovie(movie)

        genres?.let { insertAllGenres(it) }
        productionCompanies?.let { insertAllProductionCompanies(it) }
        productionCountries?.let { insertAllProductionCountries(it) }
        spokenLanguages?.let { insertAllSpokenLanguages(it) }
        castMembers?.let { insertAllCastMembers(it) }
        crewMembers?.let { insertAllCrewMembers(it) }
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
    fun insertAllMovies(movies: List<RoomMovie>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllGenres(genres: List<RoomMovieGenre>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllProductionCompanies(productionCompanies: List<RoomProductionCompany>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllProductionCountries(productionCountries: List<RoomProductionCountry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllSpokenLanguages(spokenLanguages: List<RoomSpokenLanguage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCastMembers(castMembers: List<RoomCastMember>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCrewMembers(crewMembers: List<RoomCrewMember>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllVideos(videos: List<RoomVideo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllRecommendations(recommendations: List<RoomMovieRecommendationJoin>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPlaylists(playlists: List<RoomMoviePlaylist>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMoviePlaylistJoins(moviePlaylistJoins: List<RoomMoviePlaylistJoin>)

    @Transaction
    @Query("SELECT * FROM movie WHERE id = :id")
    fun getMovieForId(id: Long): RoomMovieAllData

    @Transaction
    @Query("SELECT m2.* FROM movie_recommendation_join mrj INNER JOIN movie m1 ON mrj.movie_id = m1.id INNER JOIN movie m2 ON mrj.recommendation_id = m2.id WHERE mrj.movie_id = :movieId")
    fun getRecommendationsForMovie(movieId: Long): List<RoomMovie>

    @Transaction
    @Query("SELECT m.* FROM movie_playlist_join mpj INNER JOIN movie m ON mpj.movie_id = m.id INNER JOIN movie_playlist p ON mpj.playlist_id = p.id WHERE p.id = (SELECT id from movie_playlist WHERE name = :playlistName) ORDER BY m.popularity")
    fun getMoviesForPlaylist(playlistName: String): List<RoomMovie>

    @Query("SELECT id from movie_playlist WHERE name = :playlistName")
    fun getPlaylistIdForName(playlistName: String): Long
}
