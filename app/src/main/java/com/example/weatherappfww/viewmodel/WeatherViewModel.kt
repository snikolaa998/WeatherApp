package com.example.weatherappfww.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherappfww.ForecastModels
import com.example.weatherappfww.WeatherApplication
import com.example.weatherappfww.models.WeatherData
import com.example.weatherappfww.repository.WeatherRepository
import com.example.weatherappfww.util.Resources
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class WeatherViewModel(app: Application, val weatherRepo: WeatherRepository) : AndroidViewModel(app) {

    val weatherData: MutableLiveData<Resources<WeatherData>> = MutableLiveData()
    var weatherDataResponse: WeatherData? = null

    val forecastData: MutableLiveData<Resources<ForecastModels>> = MutableLiveData()
    var forecastDataResponse: ForecastModels? = null

    fun getForecastData(cityName: String) = viewModelScope.launch {
        searchForecast(cityName)
    }

    fun getWeatherData(cityName: String) = viewModelScope.launch {
        searchWeather(cityName)
    }

    private suspend fun searchForecast(cityName: String) {
        forecastData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = weatherRepo.getCityForecast(cityName)
                forecastData.postValue(handleSearchingForecast(response))
            } else {
                forecastData.postValue(Resources.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> weatherData.postValue(Resources.Error("Network Failure"))
                else -> weatherData.postValue(Resources.Error("Conversion error"))
            }
        }
    }

    private suspend fun searchWeather(cityName: String) {
        weatherData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = weatherRepo.getCityWeather(cityName)
                weatherData.postValue(handleSearchingWeather(response))
            } else {
                weatherData.postValue(Resources.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> weatherData.postValue(Resources.Error("Network Failure"))
                else -> weatherData.postValue(Resources.Error("Conversion error"))
            }
        }
    }

    private fun handleSearchingForecast(response: Response<ForecastModels>): Resources<ForecastModels> {
        if (response.isSuccessful) {
            response.body()?.let { forecastResponse ->
                forecastDataResponse = forecastResponse
                return Resources.Success(forecastDataResponse ?: forecastResponse)
            }
        }
        return Resources.Error(response.message())
    }

    private fun handleSearchingWeather(response: Response<WeatherData>): Resources<WeatherData> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                weatherDataResponse = resultResponse
                return Resources.Success(weatherDataResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<WeatherApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}