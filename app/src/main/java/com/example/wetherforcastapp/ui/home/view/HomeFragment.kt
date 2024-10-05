package com.example.wetherforcastapp.ui.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wetherforcastapp.databinding.FragmentHomeBinding
import com.example.wetherforcastapp.model.data.IRepo
import com.example.wetherforcastapp.model.data.RepoImpl
import com.example.wetherforcastapp.ui.home.viewmodel.HomeViewModel
import com.example.wetherforcastapp.model.data.UIState
import com.example.wetherforcastapp.model.data.database.LocalDataBaseImp
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.network.IRemoteDataSourceImpl
import com.example.wetherforcastapp.ui.home.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var hourlyWeatherListAdapter: HourlyWeatherListAdapter
    private lateinit var daysWeatherListAdapter: DaysWeatherListAdapter

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            RepoImpl.getInstance(
                IRemoteDataSourceImpl.getInstance(),
                LocalDataBaseImp.getInstance(requireContext())
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerViews()



        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Fetch weather data (current and forecast)
        fetchWeatherData(30.2681475, 30.60733385) // Example coordinates
        observeUIState()
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

    private fun observeUIState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UIState.Loading -> {
                        showLoadingState()

                    }
                    is UIState.Success -> {
                        Log.d("HomeFragment", "Current weather data received")
                        updateCurrentWeatherUI(state.dataBaseEntity)
                    }
                    is UIState.Failure -> {
                        Log.e("HomeFragment", "Error: ${state.msg}")
                       // viewModel.fetchCurrentWeatherLocal()

                        showError(state.msg)
                    }
                    else ->{Log.e("HomeFragment", "else:")}

                }
            }
        }
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        viewModel.fetchWeatherAndSaveToLocal(latitude, longitude)
    }

    private fun updateCurrentWeatherUI(dataBaseEntity: DataBaseEntity) {
        binding.progressBar.visibility = View.GONE
        binding.weatherCondition.text = dataBaseEntity.currentWeatherResponses.weather[0].description
        binding.cityName.text = dataBaseEntity.currentWeatherResponses.name

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

    private fun showLoadingState() {

       binding.progressBar.visibility = View.VISIBLE
        //Toast.makeText(requireContext(), "loading", Toast.LENGTH_LONG).show()
    }

    private fun showError(exception: Throwable) {
        // Handle error (show a Toast, Snackbar, or TextView with the error message)
        Toast.makeText(requireContext(), "Error: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}
