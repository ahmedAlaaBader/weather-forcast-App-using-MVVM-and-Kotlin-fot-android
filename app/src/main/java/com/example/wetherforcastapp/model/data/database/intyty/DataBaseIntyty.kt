package com.example.wetherforcastapp.model.data.database.intyty


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.wetherforcastapp.model.data.network.forcastresponse.ForecastResponse
import com.example.wetherforcastapp.model.data.network.response.CurrentWeatherResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = "TWeather")
data class DataBaseEntity (
    @PrimaryKey @NotNull
    val address: String,
    val currentWeatherResponses: CurrentWeatherResponse,
    val forecastResponse: ForecastResponse
) : Serializable

class CurrentWeatherResponsesConverter {
    private val gson = Gson()
@TypeConverter
fun fromCurrentWeatherResponses(value: CurrentWeatherResponse?): String? {
    return gson.toJson(value)
}

@TypeConverter
fun toCurrentWeatherResponses(value: String?): CurrentWeatherResponse? {
    val type = object : TypeToken<CurrentWeatherResponse>() {}.type
    return gson.fromJson(value, type)
}
}

class ForecastResponseConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromForecastResponse(value: ForecastResponse?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toForecastResponse(value: String?): ForecastResponse? {
        val type = object : TypeToken<ForecastResponse>() {}.type
        return gson.fromJson(value, type)
    }
}