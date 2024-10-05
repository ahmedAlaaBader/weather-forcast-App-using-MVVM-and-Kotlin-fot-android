package com.example.wetherforcastapp.model.data.network

import com.example.wetherforcastapp.model.data.ApiWeatherService
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.network.forcastresponse.ForecastResponse
import com.example.wetherforcastapp.model.data.network.response.CurrentWeatherResponse
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class IRemoteDataSourceImpl (private val apiWeatherService: ApiWeatherService ): IRemoteDataSource {
    companion object{@Volatile
    private var instance : IRemoteDataSourceImpl?=null
        fun getInstance (): IRemoteDataSourceImpl {
            return instance?: synchronized(this){
                val temp = IRemoteDataSourceImpl(ApiWeatherService.invoke())

                instance=temp
                temp
            }
        }}
    override fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<CurrentWeatherResponse> = flow {
        emit(apiWeatherService.getCurrentWeather(latitude,longitude))
    }

    override fun getForecastWeather(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<ForecastResponse> = flow {
        emit(apiWeatherService.getForecastWeather(latitude,longitude))
    }


}