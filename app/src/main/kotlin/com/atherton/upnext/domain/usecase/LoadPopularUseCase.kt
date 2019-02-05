package com.atherton.upnext.domain.usecase

import com.atherton.upnext.data.model.Movie
import com.atherton.upnext.data.repository.Response
import com.atherton.upnext.data.repository.movies.MoviesRepository
import io.reactivex.Single
import javax.inject.Inject

class LoadPopularUseCase @Inject constructor(
    private val moviesRepository: MoviesRepository
) {

    fun build(): Single<Response<List<Movie>>> = moviesRepository.popular()
}