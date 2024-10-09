package com.example.wetherforcastapp.model.data.network

import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.network.forcastresponse.ForecastResponse
import com.example.wetherforcastapp.model.data.network.response.CurrentWeatherResponse
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query

interface IRemoteDataSource {
    fun getCurrentWeather( latitude: Double,
                           longitude: Double,
                           units: String = "metric",
                           lang: String ): Flow<CurrentWeatherResponse>
    fun getForecastWeather( latitude: Double,
                            longitude: Double,
                            units: String = "metric",
                           lang: String ):Flow<ForecastResponse>



}