package com.example.weatherforecastapp.data.api

import com.example.weatherforecastapp.util.Constants
import com.example.weatherforecastapp.data.ForecastResponse
import com.example.weatherforecastapp.data.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String = Constants.API_KEY,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    @GET("forecast")
    suspend fun getFiveDayForecast(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String = Constants.API_KEY,
        @Query("units") units: String = "metric"
    ): ForecastResponse
}