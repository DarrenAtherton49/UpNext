package com.atherton.upnext.data.db

import androidx.room.TypeConverter
import com.atherton.upnext.data.db.model.person.RoomPersonCreditType

internal class RoomTypeConverters {

    @TypeConverter
    fun stringToListOfStrings(string: String?): List<String>? {
        return string.takeIf { !it.isNullOrBlank() }
            ?.split(",")
            ?.fold(listOf()) { list, s ->
                list + s
            }
    }

    @TypeConverter
    fun listOfStringsToString(stringList: List<String>?): String? {
        return stringList?.joinToString(separator = ",")
    }

    @TypeConverter
    fun stringToListOfInts(value: String?): List<Int>? {
        val list = mutableListOf<Int>()
        value.takeIf { !it.isNullOrBlank() }
            ?.split(",")
            ?.map { integerAsString: String ->
                list.add(integerAsString.toInt())
            }
        return list
    }

    @TypeConverter
    fun listOfIntsToString(value: List<Int>?): String? {
        return value?.joinToString(separator = ",")
    }

    @TypeConverter
    fun personCreditTypeToInt(credit: RoomPersonCreditType): Int {
        return when (credit) {
            RoomPersonCreditType.TV_CAST -> RoomPersonCreditType.TYPE_TV_CAST
            RoomPersonCreditType.TV_CREW -> RoomPersonCreditType.TYPE_TV_CREW
            RoomPersonCreditType.MOVIE_CAST -> RoomPersonCreditType.TYPE_MOVIE_CAST
            RoomPersonCreditType.MOVIE_CREW -> RoomPersonCreditType.TYPE_MOVIE_CREW
        }
    }

    @TypeConverter
    fun intToPersonCreditType(int: Int): RoomPersonCreditType {
        return when (int) {
            RoomPersonCreditType.TYPE_TV_CAST -> RoomPersonCreditType.TV_CAST
            RoomPersonCreditType.TYPE_TV_CREW -> RoomPersonCreditType.TV_CREW
            RoomPersonCreditType.TYPE_MOVIE_CAST -> RoomPersonCreditType.MOVIE_CAST
            RoomPersonCreditType.TYPE_MOVIE_CREW -> RoomPersonCreditType.MOVIE_CREW
            else -> throw IllegalArgumentException("Invalid person credit type")
        }
    }
}
