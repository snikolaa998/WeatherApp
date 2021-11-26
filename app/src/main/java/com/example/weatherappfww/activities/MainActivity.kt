package com.example.weatherappfww.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.weatherappfww.R
import com.example.weatherappfww.repository.WeatherRepository
import com.example.weatherappfww.util.Resources
import com.example.weatherappfww.viewmodel.WeatherVIewModelProviderFactory
import com.example.weatherappfww.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val weatherRepository = WeatherRepository()
        val viewModelProviderFactory = WeatherVIewModelProviderFactory(application, weatherRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(WeatherViewModel::class.java)

        viewModel.weatherData.observe(this) {   response ->
            when(response) {
                is Resources.Success -> {
                    response.data?.let { weatherResponse ->
                        cityTemp.text = weatherResponse.main.temp.toString()
                    }
                }
                is Resources.Error -> {
                    response.message?.let { message ->
                        cityTemp.text = "Some error..."
                    }
                }
                is Resources.Loading -> {
                    cityTemp.text = "Loading data..."
                }
            }
        }

        viewModel.forecastData.observe(this) { response ->
            when(response) {
                is Resources.Success -> {
                    response.data?.let { forecastData ->
                        Log.d("MainActivityState", forecastData.list[0].main.temp.toString())
                        Log.d("MainActivityState", forecastData.list[8].main.temp.toString())
                        Log.d("MainActivityState", forecastData.list[16].main.temp.toString())
                        Log.d("MainActivityState", forecastData.list[24].main.temp.toString())
                        Log.d("MainActivityState", forecastData.list[32].main.temp.toString())
                    }
                }
                is Resources.Error -> {
                    response.message?.let { message ->
                        Log.d("MainActivityState", "Error")
                    }
                }
                is Resources.Loading -> {
                    Log.d("MainActivityState", "Loading")
                }
            }
        }

        btn_get_temp.setOnClickListener {
            viewModel.getWeatherData("Belgrade")
        }

        btn_get_forecast.setOnClickListener {
            viewModel.getForecastData("Belgrade")
        }
    }
}