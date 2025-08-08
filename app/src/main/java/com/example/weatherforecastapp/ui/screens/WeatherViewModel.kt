package com.example.weatherforecastapp.ui.screens


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.data.ForecastResponse
import com.example.weatherforecastapp.data.WeatherResponse
import com.example.weatherforecastapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.example.weatherforecastapp.util.Constants

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val currentWeather: WeatherResponse, val forecast: ForecastResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
    object Initial : WeatherUiState() // State for when the app is first launched
}

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Initial)
    val uiState: StateFlow<WeatherUiState> = _uiState


    fun getWeatherForCity(cityName: String) {
        viewModelScope.launch {

            _uiState.value = WeatherUiState.Loading
            try {
                // Fetch both current weather and forecast concurrently
                val currentWeather = repository.getCurrentWeather(cityName)
                val forecast = repository.getFiveDayForecast(cityName)
                _uiState.value = WeatherUiState.Success(currentWeather, forecast)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}