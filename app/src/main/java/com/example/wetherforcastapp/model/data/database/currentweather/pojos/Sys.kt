package com.example.wetherforcastapp.model.data.database.currentweather.pojos

data class Sys(
    val sunrise: String?,
    val sunset: String?
) {
    override fun hashCode(): Int {
        // Safely handle null values when calculating hashCode
        return (sunrise?.hashCode() ?: 0) * 31 + (sunset?.hashCode() ?: 0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Sys) return false

        return sunrise == other.sunrise && sunset == other.sunset
    }
}