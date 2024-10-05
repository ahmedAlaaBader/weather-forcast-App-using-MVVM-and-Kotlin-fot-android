package com.example.wetherforcastapp.model.data.database.forcastweather

import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Main
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Weather

data class ForecastItem(val main: Main, val weather: List<Weather>, val dt_txt: String)
