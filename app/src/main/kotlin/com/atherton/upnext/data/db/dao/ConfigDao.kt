package com.atherton.upnext.data.db.dao

import androidx.room.*
import com.atherton.upnext.data.db.model.config.RoomConfig

@Dao
interface ConfigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConfig(config: RoomConfig)

    @Transaction
    @Query("SELECT * FROM config ORDER BY id DESC LIMIT 1")
    fun getConfig(): RoomConfig?
}
