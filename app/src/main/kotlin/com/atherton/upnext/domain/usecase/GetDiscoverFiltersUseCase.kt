package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.DiscoverFilter
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.repository.FilterRepository
import io.reactivex.Single
import javax.inject.Inject

class GetDiscoverFiltersUseCase @Inject constructor(private val filterRepository: FilterRepository) {

    fun build(): Single<Response<List<DiscoverFilter>>> = filterRepository.getFilters()
}
