package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Person
import io.reactivex.Single

interface PeopleRepository {

    fun getPerson(id: Int): Single<LceResponse<Person>>
}
