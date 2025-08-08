package com.example.weatherforecastapp.data.repository

import com.example.weatherforecastapp.data.ForecastResponse
import com.example.weatherforecastapp.data.WeatherResponse
import com.example.weatherforecastapp.data.api.WeatherApiService


class WeatherRepository(private val weatherApiService: WeatherApiService) {

    suspend fun getCurrentWeather(cityName: String): WeatherResponse {

        return weatherApiService.getCurrentWeather(cityName)
    }

    suspend fun getFiveDayForecast(cityName: String): ForecastResponse {
        return weatherApiService.getFiveDayForecast(cityName)
    }
}