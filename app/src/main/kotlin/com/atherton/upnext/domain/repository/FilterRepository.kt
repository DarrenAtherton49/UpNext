package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.DiscoverFilter
import com.atherton.upnext.domain.model.LceResponse
import io.reactivex.Single

interface FilterRepository {

    fun getFilters(): Single<LceResponse<List<DiscoverFilter>>>
}