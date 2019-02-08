package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModel
import com.atherton.upnext.domain.repository.SearchRepository
import io.reactivex.Single
import javax.inject.Inject

class SearchMultiUseCase @Inject constructor(private val searchRepository: SearchRepository) {

    fun build(query: String): Single<Response<List<SearchModel>>> = searchRepository.searchMulti(query)
}
