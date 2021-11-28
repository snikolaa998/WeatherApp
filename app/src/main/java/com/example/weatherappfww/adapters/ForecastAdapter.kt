package com.example.weatherappfww.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherappfww.R
import com.example.weatherappfww.models.CustomForecast
import kotlinx.android.synthetic.main.item_view.view.*

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    private val diffCallBack = object : DiffUtil.ItemCallback<CustomForecast>() {
        override fun areItemsTheSame(oldItem: CustomForecast, newItem: CustomForecast): Boolean {
            return oldItem.minTemp == newItem.minTemp
        }

        override fun areContentsTheSame(oldItem: CustomForecast, newItem: CustomForecast): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    var forecasts: List<CustomForecast>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = forecasts[position]
        holder.itemView.apply {
            item_temp_max.text = forecast.maxTemp.toString()
            item_temp_min.text = forecast.minTemp.toString()
            if (position % 2 == 0) {
                item_img.setImageResource(R.drawable.cloudy)
            } else {
                item_img.setImageResource(R.drawable.sun)
            }
        }
    }

    override fun getItemCount(): Int {
        return forecasts.size
    }

    inner class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}