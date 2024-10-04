package com.example.wetherforcastapp.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wetherforcastapp.databinding.FragmentHomeBinding
import com.example.wetherforcastapp.home.viewmodel.HomeViewModel
import com.example.wetherforcastapp.model.data.ApiWeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val viewModel: HomeViewModel by viewModels()
    lateinit var binding: FragmentHomeBinding
    private lateinit var hourlyWeatherListAdapter: HourlyWeatherListAdapter
    private lateinit var daysWeatherListAdapter:DaysWeatherListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val apiWeatherService = ApiWeatherService()
        // Launch coroutine to fetch weather data
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Fetch current weather data asynchronously
                val currentWeatherResponse = apiWeatherService
                    .getCurrentWeather(33.2681475, 33.60733385).await()

                // Update the weather condition (description)
                binding.weatherCondition.text = currentWeatherResponse.weather[0].description
                binding.cityName.text = currentWeatherResponse.name

                // Convert temperature from Kelvin to Celsius and update TextView
                val tempCelsius = currentWeatherResponse.main.temp
                binding.temperature.text = String.format("%.1f°C", tempCelsius)

                // Set the weather icon using Glide (ensure you have Glide added in your dependencies)
                val iconCode = currentWeatherResponse.weather[0].icon
                val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
                Glide.with(this@HomeFragment)
                    .load(iconUrl)
                    .into(binding.weatherIcon)

                // Update other weather details (pressure, humidity, wind speed)
                binding.pressureValue.text = "${currentWeatherResponse.main.pressure} hPa"
                binding.humidityValue.text = "${currentWeatherResponse.main.humidity}%"
                binding.windValue.text = "${currentWeatherResponse.wind.speed} m/s"
                binding.visibilityValue.text = "${currentWeatherResponse.visibility} km"
                binding.cloudValue.text = "${currentWeatherResponse.clouds.all}%"

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        hourlyWeatherListAdapter = HourlyWeatherListAdapter()

        binding.hourlyForecastRecyclerview.apply {
            adapter = hourlyWeatherListAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false )

        }
        daysWeatherListAdapter = DaysWeatherListAdapter()

        binding.weeklyForecastRecyclerview.apply {
            adapter = daysWeatherListAdapter
            layoutManager = LinearLayoutManager(requireContext())

        }
        lifecycleScope.launch(Dispatchers.IO) {
            val response = apiWeatherService.getForecastWeather(33.2681475, 33.60733385).await()
            withContext(Dispatchers.Main) {
                hourlyWeatherListAdapter.submitList(response.list)
                daysWeatherListAdapter.submitList(response.list)

            }
        }

        return binding.root
    }
}