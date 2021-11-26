package com.example.weatherappfww.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        btn_get_temp.setOnClickListener {
            viewModel.getWeatherData("Belgrade")
        }
    }
}