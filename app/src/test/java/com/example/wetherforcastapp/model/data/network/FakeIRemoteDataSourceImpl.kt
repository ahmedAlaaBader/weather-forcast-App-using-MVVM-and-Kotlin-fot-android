package com.example.wetherforcastapp.model.data.network

import com.example.wetherforcastapp.model.data.network.forcastresponse.ForecastResponse
import com.example.wetherforcastapp.model.data.network.response.CurrentWeatherResponse
import kotlinx.coroutines.flow.Flow

class FakeIRemoteDataSourceImpl:IRemoteDataSource {
    override fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<CurrentWeatherResponse> {
        TODO("Not yet implemented")
    }

    override fun getForecastWeather(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<ForecastResponse> {
        TODO("Not yet implemented")
    }
}