package com.example.wetherforcastapp.ui.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetherforcastapp.model.data.IRepo
import com.example.wetherforcastapp.model.data.UIState
import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

class FavViewModel(private val repo: IRepo) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState

init {
    fetchCurrentWeatherLocal()
}


    fun deleteFavWeather(address: String) {
        viewModelScope.launch {
            repo.deleteWeatherByAddress(address)
            fetchCurrentWeatherLocal()
        }
    }
    // Fetch current weather from local database
    private fun fetchCurrentWeatherLocal() {
        viewModelScope.launch {
            repo.getAllFavorites()
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

    // Fetch weather from API and save to local database
    fun fetchWeatherAndSaveToLocal(latitude: Double, longitude: Double,address:String,lang:String) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading

            // Use flow to handle weather data fetching and error handling
            repo.getCurrentWeatherRemote(latitude, longitude,lang=lang)
                .zip(repo.getForecastWeatherRemote(latitude, longitude,lang=lang)) { currentWeather, forecast ->
                    // Create a DataBaseEntity with fetched data
                    DataBaseEntity(
                        address = address,
                        currentWeatherResponses = currentWeather,
                        forecastResponse = forecast
                    )
                }
                .onEach { dataBaseEntity ->
                    // Emit the success state with the fetched data
                    _uiState.value = UIState.Success(dataBaseEntity)

                    // Save to local database
                    repo.upsertWeather(dataBaseEntity)
                   // fetchCurrentWeatherLocal()

                }
                .catch { exception ->
                    // Emit a failure state if an error occurs
                    _uiState.value = UIState.Failure(exception)

                }
                .collect()
        }
    }
}