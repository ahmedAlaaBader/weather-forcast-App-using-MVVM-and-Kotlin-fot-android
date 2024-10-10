package com.example.wetherforcastapp.model.data.database

import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.database.intyty.EntityAlarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLocalDataBaseImp (private var weatherList: MutableStateFlow<List<DataBaseEntity>> = MutableStateFlow(emptyList())):ILocalDataBase {

    override fun getAllFav(): Flow<List<DataBaseEntity>> {
        return weatherList
    }
    // Method to add entities for testing
    fun addWeatherEntity(entity: DataBaseEntity) {
        weatherList.value = weatherList.value + entity
    }

    override fun getCurrent(current: String): Flow<DataBaseEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByAddress(address: String) {
        weatherList.value = weatherList.value.filterNot { it.address == address }

    }

    override suspend fun upsertWeather(dataBaseEntity: DataBaseEntity) {
        TODO("Not yet implemented")
    }

    override fun getAllAlarm(): Flow<List<EntityAlarm>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByTime(time: String) {
        TODO("Not yet implemented")
    }

    override fun getAlarmByTime(time: String): Flow<EntityAlarm> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(entityAlarm: EntityAlarm) {
        TODO("Not yet implemented")
    }
}