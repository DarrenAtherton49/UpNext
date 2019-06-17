package com.atherton.upnext.data.db.model.person

import androidx.room.*

private const val ID = "id"
private const val PERSON_ID = "person_id"

class RoomPersonAllData {

    @Embedded
    var person: RoomPerson? = null

    @Relation(parentColumn = ID, entityColumn = PERSON_ID, entity = RoomPersonCredit::class)
    var credits: List<RoomPersonCredit> = ArrayList()
}

@Entity(
    tableName = "person"
)
data class RoomPerson(

    // base/search fields
    @ColumnInfo(name = "adult") val adultContent: Boolean?,
    @PrimaryKey @ColumnInfo(name = ID) val id: Long,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "popularity") val popularity: Float?,
    @ColumnInfo(name = "profile_path") val profilePath: String?,

    // detail fields (more detail fields can be found in class RoomPersonAllData)
    @ColumnInfo(name = "also_known_as") val alsoKnownAs: String?,
    @ColumnInfo(name = "biography") val biography: String?,
    @ColumnInfo(name = "birthday") val birthday: String?,
    @ColumnInfo(name = "deathday") val deathDay: String?,
    @ColumnInfo(name = "gender") val gender: Int?, // 1 = female, 2 = male
    @ColumnInfo(name = "homepage") val homepage: String?,
    @ColumnInfo(name = "imdb_id") val imdbId: String?,
    @ColumnInfo(name = "known_for_department") val knownForDepartment: String?,
    @ColumnInfo(name = "place_of_birth") val placeOfBirth: String?,

    @ColumnInfo(name = "is_model_complete") val isModelComplete: Boolean
)

@Entity(
    tableName = "person_credit",
    indices = [
        Index(value = [PERSON_ID], unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomPerson::class,
            parentColumns = [ID],
            childColumns = [PERSON_ID],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomPersonCredit(

    @PrimaryKey @ColumnInfo(name = ID) val id: Long,
    @ColumnInfo(name = "poster_path") val posterPath: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "credit_type") val creditType: RoomPersonCreditType,

    // join/utility field
    @ColumnInfo(name = PERSON_ID) val personId: Long
)

enum class RoomPersonCreditType(type: Int) {
    TV_CAST(1),
    TV_CREW(2),
    MOVIE_CAST(3),
    MOVIE_CREW(4);

    companion object {
        const val TYPE_TV_CAST = 1
        const val TYPE_TV_CREW = 2
        const val TYPE_MOVIE_CAST = 3
        const val TYPE_MOVIE_CREW = 4
    }
}
