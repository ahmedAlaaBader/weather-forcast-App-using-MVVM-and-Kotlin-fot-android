package com.example.wetherforcastapp.ui.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wetherforcastapp.databinding.ItemWeaklyBinding
import com.example.wetherforcastapp.model.data.database.forcastweather.ForecastItem
import java.text.SimpleDateFormat
import java.util.Locale

class DaysWeatherListAdapter : ListAdapter<ForecastItem, DailyViewHolder>(MyDifUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding =
            ItemWeaklyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val dailyWeather = getItem(position)
        holder.binding.forecastTime.text = convertToDays(dailyWeather.dt_txt)
        holder.binding.forecastTemperature.text = "${dailyWeather.main.temp}°C"
        holder.binding.forecastCondition.text = dailyWeather.weather[0].description

        val iconUrl = "https://openweathermap.org/img/wn/${dailyWeather.weather[0].icon}.png"
        Glide.with(holder.itemView.context)
            .load(iconUrl)
            .into(holder.binding.forecastIcon)
    }
}

class DailyViewHolder(val binding: ItemWeaklyBinding) : RecyclerView.ViewHolder(binding.root)

fun convertToDays(date: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val date = inputFormat.parse(date)
    return outputFormat.format(date)
}