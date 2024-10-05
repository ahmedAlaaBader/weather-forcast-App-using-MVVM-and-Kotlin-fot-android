package com.example.wetherforcastapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetherforcastapp.model.data.IRepo
import com.example.wetherforcastapp.model.data.UIState
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.DataBaseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: IRepo) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState
init {
    fetchCurrentWeatherLocal()
}
    // Fetch weather from API and save to local database
    fun fetchWeatherAndSaveToLocal(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading

            // Use flow to handle weather data fetching and error handling
            repo.getCurrentWeatherRemote(latitude, longitude)
                .zip(repo.getForecastWeatherRemote(latitude, longitude)) { currentWeather, forecast ->
                    // Create a DataBaseEntity with fetched data
                    DataBaseEntity(
                        address = "current",
                        currentWeatherResponses = currentWeather,
                        forecastResponse = forecast
                    )
                }
                .onEach { dataBaseEntity ->
                    // Emit the success state with the fetched data
                    _uiState.value = UIState.Success(dataBaseEntity)

                    // Save to local database
                    repo.upsertWeather(dataBaseEntity)
                }
                .catch { exception ->
                    // Emit a failure state if an error occurs
                    _uiState.value = UIState.Failure(exception)
                    fetchCurrentWeatherLocal()
                }
                .collect()
        }
    }

    // Fetch current weather from local database
    private fun fetchCurrentWeatherLocal() {
        viewModelScope.launch {
            repo.getCurrentWeatherLocal()
                .catch { exception ->
                    // Emit a failure state if an error occurs
                    _uiState.value = UIState.Failure(exception)
                }
                .collect { data ->
                    // Check if data is null
                    if (data != null) {
                        // Emit success state only if data is not null
                        _uiState.value = UIState.Success(data)
                    } else {
                        // Emit no data state when no data is found
                        _uiState.value = UIState.NoData
                    }
                }
        }
    }
}
