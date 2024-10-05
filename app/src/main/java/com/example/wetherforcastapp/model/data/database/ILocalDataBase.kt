package com.example.wetherforcastapp.model.data.database

import com.example.wetherforcastapp.model.data.database.currentweather.intyty.DataBaseEntity
import kotlinx.coroutines.flow.Flow

interface ILocalDataBase{
    fun getAllFav(): Flow<List<DataBaseEntity>>
    fun getCurrent(current: String = "current"): Flow<DataBaseEntity>
    suspend fun deleteByAddress(address: String)
    suspend fun upsertWeather(dataBaseEntity: DataBaseEntity)
}