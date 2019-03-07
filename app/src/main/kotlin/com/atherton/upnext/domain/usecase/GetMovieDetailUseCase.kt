package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.Watchable
import com.atherton.upnext.domain.repository.MovieRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(private val movieRepository: MovieRepository) {

    operator fun invoke(id: Int): Observable<Response<Watchable>> = movieRepository.getMovie(id).map {
        when (it) {
            is Response.Success -> {
                Response.Success(it.data, it.cached)
            }
            else -> it
        }
    }
}
