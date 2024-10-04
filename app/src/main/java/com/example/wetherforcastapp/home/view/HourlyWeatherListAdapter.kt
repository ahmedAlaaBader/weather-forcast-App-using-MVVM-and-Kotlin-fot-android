package com.example.wetherforcastapp.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wetherforcastapp.databinding.ItemHourlyBinding
import com.example.wetherforcastapp.model.data.database.forcastweather.ForecastItem
import java.text.SimpleDateFormat
import java.util.Locale

class HourlyWeatherListAdapter :
    ListAdapter<ForecastItem, HourlyWeatherListAdapter.WeatherViewHolder>(MyDifUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding =
            ItemHourlyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val hourlyWeather = getItem(position)

        val formattedTime = convertTo12HourTime(hourlyWeather.dt_txt)
        holder.binding.time.text = formattedTime
        holder.binding.temperature.text = "${hourlyWeather.main.temp}°C"

        val iconUrl = "https://openweathermap.org/img/wn/${hourlyWeather.weather[0].icon}.png"
        Glide.with(holder.itemView.context)
            .load(iconUrl)
            .into(holder.binding.weatherIcon)
    }

    class WeatherViewHolder(val binding: ItemHourlyBinding) :
        RecyclerView.ViewHolder(binding.root)
}

fun convertTo12HourTime(dateTimeString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh a", Locale.getDefault())
    return try {
        val date = inputFormat.parse(dateTimeString)
        outputFormat.format(date ?: "")
    } catch (e: Exception) {
        "Invalid Time"
    }
}