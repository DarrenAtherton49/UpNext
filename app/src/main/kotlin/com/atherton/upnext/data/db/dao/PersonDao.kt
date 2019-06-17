package com.atherton.upnext.data.db.dao

import androidx.room.*
import com.atherton.upnext.data.db.model.person.RoomPerson
import com.atherton.upnext.data.db.model.person.RoomPersonAllData
import com.atherton.upnext.data.db.model.person.RoomPersonCredit
import io.reactivex.Single

@Dao
interface PersonDao {

    @Transaction
    fun insertPersonData(
        person: RoomPerson,
        movieCredits: List<RoomPersonCredit>?,
        tvCredits: List<RoomPersonCredit>?
    ) {
        insertPerson(person)

        movieCredits?.let { insertAllCredits(it) }
        tvCredits?.let { insertAllCredits(it) }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(person: RoomPerson): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCredits(credits: List<RoomPersonCredit>)

    // we use a List here because RxJava 2 can't emit nulls if the person doesn't exist
    @Transaction
    @Query("SELECT * FROM person WHERE id = :id")
    fun getPersonListForIdSingle(id: Long): Single<List<RoomPerson>>

    @Transaction
    @Query("SELECT * FROM person WHERE id = :id")
    fun getFullPersonForId(id: Long): RoomPersonAllData?
}
