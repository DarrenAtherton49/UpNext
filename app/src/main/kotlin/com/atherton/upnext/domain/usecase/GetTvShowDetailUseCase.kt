package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.Watchable
import com.atherton.upnext.domain.repository.TvShowRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetTvShowDetailUseCase @Inject constructor(private val tvShowRepository: TvShowRepository) {

    operator fun invoke(id: Int): Observable<Response<Watchable>> = tvShowRepository.getTvShow(id).map {
        when (it) {
            is Response.Success -> {
                Response.Success(it.data, it.cached)
            }
            else -> it
        }
    }
}