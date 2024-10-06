package com.example.wetherforcastapp.ui.favorite.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.example.wetherforcastapp.R
import com.example.wetherforcastapp.databinding.FragmentFavDetailsBinding
import com.example.wetherforcastapp.databinding.FragmentHomeBinding
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.DataBaseEntity
import com.example.wetherforcastapp.ui.helperClasess.LocationPermissions
import com.example.wetherforcastapp.ui.home.view.DaysWeatherListAdapter
import com.example.wetherforcastapp.ui.home.view.HourlyWeatherListAdapter


class FavDetailsFragment : Fragment() {
    private lateinit var binding: FragmentFavDetailsBinding
    private lateinit var hourlyWeatherListAdapter: HourlyWeatherListAdapter
    private lateinit var daysWeatherListAdapter: DaysWeatherListAdapter
    private lateinit var dataBaseEntity: DataBaseEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavDetailsBinding.inflate(inflater, container, false)

        setupRecyclerViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBaseEntity = arguments?.getSerializable("dataBaseEntity") as DataBaseEntity
        updateCurrentWeatherUI(dataBaseEntity)


    }

    private fun setupRecyclerViews() {
        hourlyWeatherListAdapter = HourlyWeatherListAdapter()
        binding.hourlyForecastRecyclerview.apply {
            adapter = hourlyWeatherListAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        daysWeatherListAdapter = DaysWeatherListAdapter()
        binding.weeklyForecastRecyclerview.apply {
            adapter = daysWeatherListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    private fun updateCurrentWeatherUI(dataBaseEntity: DataBaseEntity) {

        binding.weatherCondition.text = dataBaseEntity.currentWeatherResponses.weather[0].description
        binding.cityName.text = dataBaseEntity.address

        val tempCelsius = dataBaseEntity.currentWeatherResponses.main.temp
        binding.temperature.text = String.format("%.1f°C", tempCelsius)

        // Load weather icon using Glide
        val iconCode = dataBaseEntity.currentWeatherResponses.weather[0].icon
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
        Glide.with(this)
            .load(iconUrl)
            .into(binding.weatherIcon)

        // Update other weather details
        binding.pressureValue.text = "${dataBaseEntity.currentWeatherResponses.main.pressure} hPa"
        binding.humidityValue.text = "${dataBaseEntity.currentWeatherResponses.main.humidity}%"
        binding.windValue.text = "${dataBaseEntity.currentWeatherResponses.wind.speed} m/s"
        binding.visibilityValue.text = "${dataBaseEntity.currentWeatherResponses.visibility} km"
        binding.cloudValue.text = "${dataBaseEntity.currentWeatherResponses.clouds.all}%"

        // Update RecyclerViews with forecast data
        hourlyWeatherListAdapter.submitList(dataBaseEntity.forecastResponse.list)
        daysWeatherListAdapter.submitList(dataBaseEntity.forecastResponse.list)
    }
}