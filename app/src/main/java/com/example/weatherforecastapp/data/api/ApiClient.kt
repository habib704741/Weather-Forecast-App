package com.example.weatherforecastapp.data.api


import com.example.weatherforecastapp.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}
