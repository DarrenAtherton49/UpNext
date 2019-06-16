package com.atherton.upnext.data.db.model.config

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "config"
)
data class RoomConfig(
    @ColumnInfo(name = "backdrop_sizes") val backdropSizes: List<String>,
    @ColumnInfo(name = "base_url") val baseUrl: String,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0,
    @ColumnInfo(name = "logo_sizes") val logoSizes: List<String>,
    @ColumnInfo(name = "poster_sizes") val posterSizes: List<String>,
    @ColumnInfo(name = "profile_sizes") val profileSizes: List<String>,
    @ColumnInfo(name = "secure_base_url") val secureBaseUrl: String,
    @ColumnInfo(name = "still_sizes") val stillSizes: List<String> // for episode still images
)
