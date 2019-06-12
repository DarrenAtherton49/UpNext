package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Watchable
import com.atherton.upnext.domain.repository.TvShowRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetTvShowDetailUseCase @Inject constructor(private val tvShowRepository: TvShowRepository) {

    //todo check why we need to call map{} in order to cast from TvShow to Watchable
    operator fun invoke(id: Long): Observable<LceResponse<Watchable>> = tvShowRepository.getTvShow(id).map { it }
}
