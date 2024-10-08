package com.example.wetherforcastapp.ui.home.view

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wetherforcastapp.databinding.ItemHourlyBinding
import com.example.wetherforcastapp.model.data.database.forcastweather.ForecastItem
import java.text.SimpleDateFormat
import java.util.Locale

class HourlyWeatherListAdapter(private val sharedPreferences: SharedPreferences) :
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


        val convertedTemp = convertTemp(hourlyWeather.main.temp.toString())
        holder.binding.temperature.text = convertedTemp


        val iconUrl = "https://openweathermap.org/img/wn/${hourlyWeather.weather[0].icon}.png"
        Glide.with(holder.itemView.context)
            .load(iconUrl)
            .into(holder.binding.weatherIcon)
    }

    class WeatherViewHolder(val binding: ItemHourlyBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun convertTemp(temp: String): String {
        val celsiusTemp = temp.toDoubleOrNull() ?: return "Invalid temperature"

        return when (checkTemp()) {
            "f" -> {

                val fahrenheit = (celsiusTemp * 9 / 5) + 32
                "%.2f °F".format(fahrenheit)
            }
            "k" -> {

                val kelvin = celsiusTemp + 273.15
                "%.2f K".format(kelvin)
            }
            else -> {

                "%.2f °C".format(celsiusTemp)
            }
        }
    }


    private fun checkTemp(): String {
        return sharedPreferences.getString("temperature", "c") ?: "c"
    }

    // Format date and time to 12-hour format
    private fun convertTo12HourTime(dateTimeString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh a", Locale.getDefault())
        return try {
            val date = inputFormat.parse(dateTimeString)
            outputFormat.format(date ?: "")
        } catch (e: Exception) {
            "Invalid Time"
        }
    }
}
