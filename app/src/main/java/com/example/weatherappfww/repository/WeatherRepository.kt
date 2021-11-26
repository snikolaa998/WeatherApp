package com.example.weatherappfww.repository

import com.example.weatherappfww.api.RetrofitInstance

class WeatherRepository {
    suspend fun getCityWeather(cityName: String) =
        RetrofitInstance.api.getCityWeather(cityName)

    suspend fun getCityForecast(cityName: String) =
        RetrofitInstance.api.getCityForecast(cityName)
}