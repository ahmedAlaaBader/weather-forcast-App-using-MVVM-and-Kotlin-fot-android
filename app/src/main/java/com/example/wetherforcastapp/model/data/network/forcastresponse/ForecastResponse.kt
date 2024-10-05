package com.example.wetherforcastapp.model.data.network.forcastresponse

import com.example.wetherforcastapp.model.data.database.forcastweather.ForecastItem

data class ForecastResponse(val list: List<ForecastItem>)
