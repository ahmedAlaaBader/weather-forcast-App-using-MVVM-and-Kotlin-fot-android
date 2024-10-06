package com.example.wetherforcastapp.ui.favorite.view

import androidx.recyclerview.widget.DiffUtil
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.database.forcastweather.ForecastItem

class FavDifUtil: DiffUtil.ItemCallback<DataBaseEntity>() {
    override fun areItemsTheSame(oldItem: DataBaseEntity, newItem: DataBaseEntity): Boolean {
        return oldItem.address == newItem.address
    }

    override fun areContentsTheSame(oldItem: DataBaseEntity, newItem: DataBaseEntity): Boolean {
        return oldItem == newItem
    }
}
