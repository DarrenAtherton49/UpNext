package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.repository.MovieRepository
import io.reactivex.Single
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(private val movieRepository: MovieRepository) {

    fun build(id: Int): Single<Response<Movie>> = movieRepository.getMovie(id)
}
