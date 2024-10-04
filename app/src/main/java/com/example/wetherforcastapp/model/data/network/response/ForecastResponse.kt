package com.example.wetherforcastapp.model.data.network.response

import com.example.wetherforcastapp.model.data.database.forcastweather.ForecastItem

data class ForecastResponse(val list: List<ForecastItem>)
