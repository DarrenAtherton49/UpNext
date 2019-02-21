package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.Person
import com.atherton.upnext.domain.model.Response
import io.reactivex.Single

interface PeopleRepository {

    fun getPerson(id: Int): Single<Response<Person>>
}