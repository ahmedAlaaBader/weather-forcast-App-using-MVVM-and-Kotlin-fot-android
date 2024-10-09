package com.example.wetherforcastapp.model.data.database

import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.database.intyty.EntityAlarm
import kotlinx.coroutines.flow.Flow

interface ILocalDataBase{
    fun getAllFav(): Flow<List<DataBaseEntity>>
    fun getCurrent(current: String = "current"): Flow<DataBaseEntity>
    suspend fun deleteByAddress(address: String)
    suspend fun upsertWeather(dataBaseEntity: DataBaseEntity)


    fun getAllAlarm(): Flow<List<EntityAlarm>>
    suspend fun deleteByTime(time: String)
    fun getAlarmByTime(time: String): Flow<EntityAlarm>
    suspend fun insertAlarm(entityAlarm: EntityAlarm)
}