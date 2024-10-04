package com.example.wetherforcastapp.model.data.network.response

import com.example.wetherforcastapp.model.data.database.currentweather.intyty.Clouds
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.Coord
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.Main
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.Sys
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.Weather
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.Wind

data class CurrentWeatherResponse(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)