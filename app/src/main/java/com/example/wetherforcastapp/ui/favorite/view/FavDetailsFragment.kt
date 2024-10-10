package com.example.wetherforcastapp.ui.favorite.view

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.example.wetherforcastapp.databinding.FragmentFavDetailsBinding
import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity
import com.example.wetherforcastapp.ui.home.view.DaysWeatherListAdapter
import com.example.wetherforcastapp.ui.home.view.HourlyWeatherListAdapter


class FavDetailsFragment : Fragment() {
    private lateinit var binding: FragmentFavDetailsBinding
    private lateinit var hourlyWeatherListAdapter: HourlyWeatherListAdapter
    private lateinit var daysWeatherListAdapter: DaysWeatherListAdapter
    private lateinit var dataBaseEntity: DataBaseEntity
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavDetailsBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("R3-pref", MODE_PRIVATE)
        setupRecyclerViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBaseEntity = arguments?.getSerializable("dataBaseEntity") as DataBaseEntity
        updateCurrentWeatherUI(dataBaseEntity)


    }

    private fun setupRecyclerViews() {
        hourlyWeatherListAdapter = HourlyWeatherListAdapter(sharedPreferences)
        binding.hourlyForecastRecyclerview.apply {
            adapter = hourlyWeatherListAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        daysWeatherListAdapter = DaysWeatherListAdapter(sharedPreferences)
        binding.weeklyForecastRecyclerview.apply {
            adapter = daysWeatherListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    private fun updateCurrentWeatherUI(dataBaseEntity: DataBaseEntity) {

        binding.weatherCondition.text = dataBaseEntity.currentWeatherResponses.weather[0].description
        if (dataBaseEntity.currentWeatherResponses.name != ""){
        binding.cityName.text = dataBaseEntity.currentWeatherResponses.name
        }else {binding.cityName.text = dataBaseEntity.address}

        val tempCelsius = dataBaseEntity.currentWeatherResponses.main.temp
        val convertedTemp = convertTemp(tempCelsius.toString())

        binding.temperature.text = convertedTemp

        val speed = dataBaseEntity.currentWeatherResponses.wind.speed
        val convertedSpeed = convertSpeed(speed.toString())
        binding.windValue.text = convertedSpeed

        binding.visibilityValue.text = "${dataBaseEntity.currentWeatherResponses.visibility}"
        // Load weather icon using Glide
        val iconCode = dataBaseEntity.currentWeatherResponses.weather[0].icon
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
        Glide.with(this)
            .load(iconUrl)
            .into(binding.weatherIcon)
        binding.pressureValue.text = "${dataBaseEntity.currentWeatherResponses.main.pressure} hPa"
        binding.humidityValue.text = "${dataBaseEntity.currentWeatherResponses.main.humidity}%"
        binding.cloudValue.text = "${dataBaseEntity.currentWeatherResponses.clouds.all}%"



        // Update RecyclerViews with forecast data
        hourlyWeatherListAdapter.submitList(dataBaseEntity.forecastResponse.list)
        daysWeatherListAdapter.submitList(dataBaseEntity.forecastResponse.list)
    }
    private fun convertSpeed(speed: String): String {

        val speedInMs = speed.toDoubleOrNull() ?: return "Invalid speed"

        return when (checkSpeed()) {
            "h" -> {

                val speedInMph = speedInMs * 2.23694
                "%.2f mph".format(speedInMph)
            }
            else -> {

                "%.2f m/s".format(speedInMs)
            }
        }
    }
    private fun checkSpeed():String{
        val speed =sharedPreferences.getString("wind", "s")
        return when (speed) {
            "h" -> {
                "h"
            }
            else -> {
                "s"
            }
        }
    }
    private fun convertTemp(temp: String): String {

        val celsiusTemp = temp.toDoubleOrNull() ?: return "Invalid temperature"

        return when (checkTemp()) {
            "f" -> {
                // Celsius to Fahrenheit
                val fahrenheit = (celsiusTemp * 9 / 5) + 32
                "%.2f °F".format(fahrenheit)
            }
            "k" -> {
                // Celsius to Kelvin
                val kelvin = celsiusTemp + 273.15
                "%.2f K".format(kelvin)
            }
            else -> {
                // If Celsius, just return the original value with °C
                "%.2f °C".format(celsiusTemp)
            }
        }
    }
    private fun checkTemp():String{
        val temp =sharedPreferences.getString("temperature", "c")
        return when (temp) {
            "f" -> {
                "f"
            }
            "k" -> {
                "k"
            }
            else -> {
                "c"
            }
        }

    }
}