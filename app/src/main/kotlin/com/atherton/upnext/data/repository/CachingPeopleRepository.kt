package com.atherton.upnext.data.repository

import com.atherton.upnext.data.mapper.toDomainLceResponse
import com.atherton.upnext.data.mapper.toDomainPerson
import com.atherton.upnext.data.network.service.TmdbPeopleService
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Person
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

    override fun getPerson(id: Int): Single<LceResponse<Person>> {
        return peopleService.getPersonDetails(id).map {
            it.toDomainLceResponse(false) { person -> person.toDomainPerson() }
        }
    }
}
