package com.example.weatherappfww.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherappfww.repository.WeatherRepository

class WeatherVIewModelProviderFactory(val app: Application, val weatherRepo: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WeatherViewModel(app, weatherRepo) as T
    }
}