package com.atherton.upnext.data.repository

import com.atherton.upnext.data.db.dao.PersonDao
import com.atherton.upnext.data.db.model.person.RoomPerson
import com.atherton.upnext.data.db.model.person.RoomPersonAllData
import com.atherton.upnext.data.mapper.*
import com.atherton.upnext.data.network.model.NetworkResponse
import com.atherton.upnext.data.network.model.TmdbPerson
import com.atherton.upnext.data.network.service.TmdbPersonService
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.Person
import com.atherton.upnext.domain.repository.PersonRepository
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingPersonRepository @Inject constructor(
    private val personDao: PersonDao,
    private val personService: TmdbPersonService
) : PersonRepository {

    //todo modify this to check if movie is in database and if it is still valid (time based?)
    //todo modify this call to add a 'forceRefresh' parameter (e.g. in case of pull-to-refresh)
    override fun getPerson(id: Long): Observable<LceResponse<Person>> {
        return personDao.getPersonListForIdSingle(id)
            .toObservable()
            .flatMap { personList ->
                if (personList.isNotEmpty() && personList[0].isModelComplete) { // person is cached and has all data
                    val person: Person? = getPersonFromDatabase(id) // fetch the full person and all relations
                    Observable.fromCallable {
                        if (person != null) {
                            LceResponse.Content(data = person)
                        } else {
                            throw IllegalStateException("Person should be in database - check query")
                        }
                    }
                } else {
                    personService.getPersonDetails(id)
                        .toObservable()
                        .doOnNext { networkResponse ->
                            if (networkResponse is NetworkResponse.Success) {
                                val networkPerson: TmdbPerson = networkResponse.body
                                saveFullPersonToDatabase(networkPerson)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getPersonFromDatabase(id))
                        }
                }
            }
    }

    private fun saveFullPersonToDatabase(person: TmdbPerson) {
        val personId: Long = person.id.toLong()
        personDao.insertPersonData(
            person = person.toRoomPerson(true),
            movieCredits = person.movieCredits?.toRoomPersonMovieCredits(personId),
            tvCredits = person.tvCredits?.toRoomPersonTvCredits(personId)
        )
    }

    private fun getPersonFromDatabase(id: Long): Person? {
        val dbPersonData: RoomPersonAllData? = personDao.getFullPersonForId(id)
        return if (dbPersonData != null) {
            val person: RoomPerson? = dbPersonData.person
            if (person != null) {
                return dbPersonData.toDomainPerson()
            } else null
        } else null
    }
}
