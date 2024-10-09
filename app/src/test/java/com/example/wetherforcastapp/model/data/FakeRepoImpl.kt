package com.example.wetherforcastapp.model.data

import com.example.wetherforcastapp.model.data.database.FakeLocalDataBaseImp
import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.network.FakeIRemoteDataSourceImpl
import com.example.wetherforcastapp.model.data.network.forcastresponse.ForecastResponse
import com.example.wetherforcastapp.model.data.network.response.CurrentWeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeRepoImpl(val fakeIRemoteDataSourceImpl: FakeIRemoteDataSourceImpl,val fakeLocalDataBaseImp: FakeLocalDataBaseImp):IRepo {
    private var weatherList: MutableStateFlow<List<DataBaseEntity>> = MutableStateFlow(emptyList())

    override fun getCurrentWeatherRemote(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<CurrentWeatherResponse> {
        TODO("Not yet implemented")
    }

    override fun getForecastWeatherRemote(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<ForecastResponse> {
        TODO("Not yet implemented")
    }

    override fun getCurrentWeatherLocal(): Flow<DataBaseEntity> {
        TODO("Not yet implemented")
    }

    override fun getAllFavorites(): Flow<List<DataBaseEntity>> {
        return fakeLocalDataBaseImp.getAllFav()
    }
    // Method to add entities for testing
    fun addWeatherEntity(entity: DataBaseEntity) {
        weatherList.value = weatherList.value + entity
    }
    override suspend fun upsertWeather(dataBaseEntity: DataBaseEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeatherByAddress(address: String) {
        TODO("Not yet implemented")
    }


}