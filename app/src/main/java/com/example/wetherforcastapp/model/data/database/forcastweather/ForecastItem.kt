package com.example.wetherforcastapp.model.data.database.forcastweather

import com.example.wetherforcastapp.model.data.database.currentweather.intyty.Main
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.Weather

data class ForecastItem(val main: Main, val weather: List<Weather>, val dt_txt: String)
