package com.atherton.upnext.domain.usecase

import com.atherton.upnext.domain.model.DiscoverFilter
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.repository.FilterRepository
import io.reactivex.Single
import javax.inject.Inject

class GetDiscoverFiltersUseCase @Inject constructor(private val filterRepository: FilterRepository) {

    operator fun invoke(): Single<LceResponse<List<DiscoverFilter>>> = filterRepository.getFilters()
}
