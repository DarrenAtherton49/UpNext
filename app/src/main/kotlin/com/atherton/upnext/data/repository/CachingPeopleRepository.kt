package com.atherton.upnext.data.repository

import com.atherton.upnext.data.mapper.toDomainPerson
import com.atherton.upnext.data.mapper.toDomainResponse
import com.atherton.upnext.data.network.service.TmdbPeopleService
import com.atherton.upnext.domain.model.Person
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.repository.PeopleRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingPeopleRepository @Inject constructor(
    //todo add in-memory cache
    //todo add database
    private val peopleService: TmdbPeopleService
) : PeopleRepository {

    override fun getPerson(id: Int): Single<Response<Person>> {
        return peopleService.getPersonDetails(id).map {
            it.toDomainResponse(false) { person -> person.toDomainPerson() }
        }
    }
}
