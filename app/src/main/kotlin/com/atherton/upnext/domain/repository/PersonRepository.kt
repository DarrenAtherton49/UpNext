package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Person
import io.reactivex.Observable

interface PersonRepository {

    fun getPerson(id: Long): Observable<LceResponse<Person>>
}
