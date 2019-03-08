package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Searchable
import com.atherton.upnext.domain.repository.SearchRepository
import io.reactivex.Observable
import javax.inject.Inject

class SearchMultiUseCase @Inject constructor(private val searchRepository: SearchRepository) {

    operator fun invoke(query: String): Observable<LceResponse<List<Searchable>>> = searchRepository.searchMulti(query)
}
