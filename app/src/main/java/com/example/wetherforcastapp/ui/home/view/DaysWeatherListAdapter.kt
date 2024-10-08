package com.example.wetherforcastapp.ui.home.view

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wetherforcastapp.databinding.ItemWeaklyBinding
import com.example.wetherforcastapp.model.data.database.forcastweather.ForecastItem
import java.text.SimpleDateFormat
import java.util.Locale

class DaysWeatherListAdapter(
    private val sharedPreferences: SharedPreferences // Add SharedPreferences as a constructor parameter
) : ListAdapter<ForecastItem, DaysWeatherListAdapter.DailyViewHolder>(MyDifUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding =
            ItemWeaklyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val dailyWeather = getItem(position)


        holder.binding.forecastTime.text = convertToDays(dailyWeather.dt_txt)


        val convertedTemp = convertTemp(dailyWeather.main.temp.toString())
        holder.binding.forecastTemperature.text = convertedTemp

        holder.binding.forecastCondition.text = dailyWeather.weather[0].description

        val iconUrl = "https://openweathermap.org/img/wn/${dailyWeather.weather[0].icon}.png"
        Glide.with(holder.itemView.context)
            .load(iconUrl)
            .into(holder.binding.forecastIcon)
    }

    class DailyViewHolder(val binding: ItemWeaklyBinding) : RecyclerView.ViewHolder(binding.root)


    private fun convertToDays(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE", Locale.getDefault())
        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: "")
        } catch (e: Exception) {
            "Invalid Date"
        }
    }


    private fun convertTemp(temp: String): String {
        val celsiusTemp = temp.toDoubleOrNull() ?: return "Invalid temp"

        return when (getPreferredTempUnit()) {
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

    // Retrieve the user's preferred temperature unit from SharedPreferences
    private fun getPreferredTempUnit(): String {
        return sharedPreferences.getString("temperature", "c") ?: "c"
    }
}
