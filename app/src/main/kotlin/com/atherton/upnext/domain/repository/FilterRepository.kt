package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.DiscoverFilter
import com.atherton.upnext.domain.model.LceResponse
import io.reactivex.Single

interface FilterRepository {

    fun getFiltersObservable(): Single<LceResponse<List<DiscoverFilter>>>
}