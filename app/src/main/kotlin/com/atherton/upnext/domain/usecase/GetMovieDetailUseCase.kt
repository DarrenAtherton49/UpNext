package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Watchable
import com.atherton.upnext.domain.repository.MovieRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(private val movieRepository: MovieRepository) {

    //todo check why we need to call map{} in order to cast from TvShow to Watchable
    operator fun invoke(id: Long): Observable<LceResponse<Watchable>> = movieRepository.getMovie(id).map { it }
}
