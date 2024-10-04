package com.example.wetherforcastapp.home.view

import androidx.recyclerview.widget.DiffUtil
import com.example.wetherforcastapp.model.data.database.forcastweather.ForecastItem

class MyDifUtil : DiffUtil.ItemCallback<ForecastItem>() {
    override fun areItemsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
        return oldItem.dt_txt == newItem.dt_txt
    }

    override fun areContentsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
        return oldItem == newItem
    }
}