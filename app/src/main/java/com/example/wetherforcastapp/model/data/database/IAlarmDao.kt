package com.example.wetherforcastapp.model.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wetherforcastapp.model.data.database.intyty.EntityAlarm
import kotlinx.coroutines.flow.Flow

@Dao
interface IAlarmDao {
    @Query("SELECT * FROM TAlarms")
    fun getAllAlarm(): Flow<List<EntityAlarm>>


    @Query("DELETE FROM TAlarms WHERE time = :time")
    suspend fun deleteByTime(time: String)

    @Query("SELECT * FROM TAlarms WHERE time = :time LIMIT 1")
    fun getAlarmByTime(time: String): Flow<EntityAlarm>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlarm(entityAlarm: EntityAlarm)
}