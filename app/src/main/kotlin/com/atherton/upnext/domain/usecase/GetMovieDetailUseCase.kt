package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.Watchable
import com.atherton.upnext.domain.repository.MovieRepository
import io.reactivex.Single
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(private val movieRepository: MovieRepository) {

    fun build(id: Int): Single<Response<Watchable>> = movieRepository.getMovie(id).map {
        when (it) {
            is Response.Success -> {
                Response.Success(it.data, it.cached)
            }
            else -> it
        }
    }
}
