package com.example.wetherforcastapp.model.data

import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.database.intyty.EntityAlarm
import com.example.wetherforcastapp.model.data.network.forcastresponse.ForecastResponse
import com.example.wetherforcastapp.model.data.network.response.CurrentWeatherResponse
import kotlinx.coroutines.flow.Flow

interface IRepo {
    // Remote data methods
    fun getCurrentWeatherRemote(latitude: Double, longitude: Double, units: String = "metric", lang: String ): Flow<CurrentWeatherResponse>
    fun getForecastWeatherRemote(latitude: Double, longitude: Double, units: String = "metric", lang: String ): Flow<ForecastResponse>

    // Local data methods
    fun getCurrentWeatherLocal(): Flow<DataBaseEntity>
    fun getAllFavorites(): Flow<List<DataBaseEntity>>
    suspend fun upsertWeather(dataBaseEntity: DataBaseEntity)
    suspend fun deleteWeatherByAddress(address: String)
    fun getAllAlarm(): Flow<List<EntityAlarm>>
    suspend fun deleteByTime(time: String)
    fun getAlarmByTime(time: String): Flow<EntityAlarm>
    suspend fun insertAlarm(entityAlarm: EntityAlarm)
}
