package com.example.wetherforcastapp.model.data.database.currentweather.pojos

data class Sys(
    val country: String,
    val id: Int,
    val sunrise: Int,
    val sunset: Int,
    val type: Int
)