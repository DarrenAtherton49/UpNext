package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.ContentList
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Movie
import io.reactivex.Observable

interface MovieRepository {

    fun getMovie(id: Long): Observable<LceResponse<Movie>>
    fun getPopular(): Observable<LceResponse<List<Movie>>>
    fun getUpcoming(): Observable<LceResponse<List<Movie>>>
    fun getTopRated(): Observable<LceResponse<List<Movie>>>
    fun getNowPlaying(): Observable<LceResponse<List<Movie>>>
    fun getMovieLists(): Observable<LceResponse<List<ContentList>>>
    fun getMoviesForList(listId: Long): Observable<LceResponse<List<Movie>>>
    fun toggleMovieWatchlistStatus(movieId: Long): Observable<LceResponse<Movie>>
    fun toggleMovieWatchedStatus(movieId: Long): Observable<LceResponse<Movie>>
    fun toggleMovieListStatus(movieId: Long, listId: Long): Observable<LceResponse<Movie>>
}
