package com.example.weatherappfww.api

import com.example.weatherappfww.ForecastModels
import com.example.weatherappfww.models.WeatherData
import com.example.weatherappfww.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather")
    suspend fun getCityWeather(
        @Query("q")
        cityName: String,
        @Query("appid")
        apiKey: String = API_KEY,
        @Query("units")
        units: String = "metric"
    ): Response<WeatherData>

    @GET("data/2.5/forecast")
    suspend fun getCityForecast(
        @Query("q")
        cityName: String,
        @Query("appid")
        apiKey: String = API_KEY,
        @Query("units")
        units: String = "metric"
    ): Response<ForecastModels>

}