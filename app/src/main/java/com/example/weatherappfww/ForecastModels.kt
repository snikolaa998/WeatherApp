package com.example.weatherappfww

data class ForecastModels(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<ForecastList>,
    val message: Int
)