package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.MovieList
import io.reactivex.Observable

interface MovieRepository {

    fun getMovie(id: Long): Observable<LceResponse<Movie>>
    fun getPopular(): Observable<LceResponse<List<Movie>>>
    fun getUpcoming(): Observable<LceResponse<List<Movie>>>
    fun getTopRated(): Observable<LceResponse<List<Movie>>>
    fun getNowPlaying(): Observable<LceResponse<List<Movie>>>
    fun getMovieLists(): Observable<LceResponse<List<MovieList>>>
    fun getMoviesForList(listId: Long): Observable<LceResponse<List<Movie>>>
}
