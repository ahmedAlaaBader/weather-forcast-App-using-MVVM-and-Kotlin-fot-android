package com.example.wetherforcastapp.ui.home.view

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wetherforcastapp.databinding.FragmentHomeBinding
import com.example.wetherforcastapp.model.data.RepoImpl
import com.example.wetherforcastapp.ui.home.viewmodel.HomeViewModel
import com.example.wetherforcastapp.model.data.UIState
import com.example.wetherforcastapp.model.data.database.LocalDataBaseImp
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.network.IRemoteDataSourceImpl
import com.example.wetherforcastapp.ui.helperClasess.LocationPermissions
import com.example.wetherforcastapp.ui.helperClasess.LocationResultListener
import com.example.wetherforcastapp.ui.home.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment(),LocationResultListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var hourlyWeatherListAdapter: HourlyWeatherListAdapter
    private lateinit var daysWeatherListAdapter: DaysWeatherListAdapter
    private lateinit var locationPermissions: LocationPermissions
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var lang :String
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
        sharedPreferences = requireContext().getSharedPreferences("R3-pref", MODE_PRIVATE)
        locationPermissions = LocationPermissions(this,this)
        setupRecyclerViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val latitude = arguments?.getDouble("latitude")
        val longitude = arguments?.getDouble("longitude")
        latitude?.let {
            if (longitude != null) {
                showDataByLocationSelection(it,longitude)
            }
        }
    }
    override fun onResume() {
        super.onResume()


        // Fetch weather data (current and forecast)
       // fetchWeatherData(30.2681475, 30.60733385) // Example coordinates

        observeUIState()
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

    private fun observeUIState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UIState.Loading -> {
                        showLoadingState()

                    }
                    is UIState.Success<*> -> {
                        val dataBaseEntity = state.data as DataBaseEntity
                        Log.d("HomeFragment", "Current weather data received")
                        updateCurrentWeatherUI(dataBaseEntity)
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



    private fun updateCurrentWeatherUI(dataBaseEntity: DataBaseEntity) {
        binding.progressBar.visibility = View.GONE
        binding.weatherCondition.text = dataBaseEntity.currentWeatherResponses.weather[0].description
        binding.cityName.text = dataBaseEntity.currentWeatherResponses.name

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
    private fun showDataByLocationSelection(latitude: Double, longitude: Double){
        val language = sharedPreferences.getString("LANG","en")
        when (checkLocationSelection()){
            "m" -> {
              if (latitude!=null && longitude != null){
                  viewModel.fetchWeatherAndSaveToLocal(latitude,longitude,language.toString())
              }
                else{
                  locationPermissions.checkLocationPermissions()
                }
            }
            else -> {
                locationPermissions.checkLocationPermissions()
            }
        }


    }
    private fun checkLocationSelection():String{
        val locationSelection =sharedPreferences.getString("map", "g")
        return when (locationSelection) {
            "m" -> {
                "m"
            }

            else -> {
                "g"
            }
        }

    }
    private fun showLoadingState() {

       binding.progressBar.visibility = View.VISIBLE
        //Toast.makeText(requireContext(), "loading", Toast.LENGTH_LONG).show()
    }

    private fun showError(exception: Throwable) {
        // Handle error (show a Toast, Snackbar, or TextView with the error message)
        Toast.makeText(requireContext(), "Error: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
    }

    override fun onLocationReceived(latitude: Double, longitude: Double) {
        lang= sharedPreferences.getString("LANG","en").toString()
        viewModel.fetchWeatherAndSaveToLocal(latitude, longitude, lang)
        Log.d("lang", "onLocationReceived: "+lang)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationPermissions.REQUEST_LOCATION_CODE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // If granted, check if location services are enabled
                    if (locationPermissions.isLocationEnabled()) {
                        locationPermissions.getFreshLocation()
                    } else {
                        locationPermissions.promptEnableLocation()
                    }
                } else {
                    // Permission denied (temporary)
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Toast.makeText(requireActivity(), "Location permission is required for this app to function.", Toast.LENGTH_SHORT).show()
                    } else {
                        // User has denied permission with "Don't ask again"
                        Toast.makeText(requireActivity(), "Location permission was denied. Please enable it in app settings.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}
