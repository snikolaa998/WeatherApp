package com.example.weatherappfww.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherappfww.R
import com.example.weatherappfww.adapters.ForecastAdapter
import com.example.weatherappfww.models.CustomForecast
import com.example.weatherappfww.repository.WeatherRepository
import com.example.weatherappfww.util.Resources
import com.example.weatherappfww.viewmodel.WeatherVIewModelProviderFactory
import com.example.weatherappfww.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_view.view.*
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: WeatherViewModel
    lateinit var forecastAdapter: ForecastAdapter
    lateinit var forecastList: MutableList<CustomForecast>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        val weatherRepository = WeatherRepository()
        val viewModelProviderFactory = WeatherVIewModelProviderFactory(application, weatherRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(WeatherViewModel::class.java)

        forecastList = mutableListOf()

        viewModel.weatherData.observe(this) {   response ->
            when(response) {
                is Resources.Success -> {
                    response.data?.let { weatherResponse ->
                        tv_current_city.text = weatherResponse.name
                        tv_celsius.visibility = View.VISIBLE
                        tv_current_temp.text = weatherResponse.main.temp.toInt().toString()
                        hideProgressBar()
                    }
                }
                is Resources.Error -> {
                    response.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        hideProgressBar()
                    }
                }
                is Resources.Loading -> {
                        showProgressBar()
                }
            }
        }

        viewModel.forecastData.observe(this) { response ->
            when(response) {
                is Resources.Success -> {
                    response.data?.let { forecastData ->
                        forecastList.clear()
                        for (i in 0..32 step 8) {
                            val minTemp = forecastData.list[i].main.temp_min.toInt()
                            val maxTemp = forecastData.list[i].main.temp_max.toInt()
                            Log.d("ForecastAdapter", "Min: $minTemp")
                            Log.d("ForecastAdapter", "Max: $maxTemp")
                            val forecast = CustomForecast(minTemp, maxTemp)
                            forecastList.add(forecast)
                        }
                        forecastAdapter.forecasts = forecastList
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

        et_current_city.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when(actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    val city = et_current_city.text.toString()
                    if (city.isNotEmpty()) {
                        viewModel.getWeatherData(city)
                        viewModel.getForecastData(city)
                    } else {
                        Toast.makeText(this, "Please enter a city!", Toast.LENGTH_SHORT).show()
                    }
                    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                    true
                }
                else -> false
            }
        }

        btn_search_weather.setOnClickListener {
            val city = et_current_city.text.toString()
            if (city.isNotEmpty()) {
                viewModel.getWeatherData(city)
                viewModel.getForecastData(city)
            } else {
                Toast.makeText(this, "Please enter a city!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        forecastAdapter = ForecastAdapter()
        forecast_rv.apply {
            adapter = forecastAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }
}