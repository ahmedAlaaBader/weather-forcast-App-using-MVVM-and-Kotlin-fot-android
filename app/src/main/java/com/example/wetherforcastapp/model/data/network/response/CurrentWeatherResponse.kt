package com.example.wetherforcastapp.model.data.network.response

import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Clouds
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Coord
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Main
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Sys
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Weather
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Wind

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