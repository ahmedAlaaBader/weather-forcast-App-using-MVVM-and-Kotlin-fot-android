package com.example.wetherforcastapp.ui.favorite.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherforcastapp.databinding.FavItemBinding
import com.example.wetherforcastapp.databinding.FragmentFavBinding
import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity

class FavWeatherListAdapter (
    var myListener: (DataBaseEntity) -> Unit,
    var myDeleteListener: (String) -> Unit
) : ListAdapter<DataBaseEntity, FavViewHolder>(
    FavDifUtil()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val binding = FavItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val favWeather = getItem(position)
        if (favWeather.currentWeatherResponses.name != ""){
        holder.binding.locationName.text = favWeather.currentWeatherResponses.name}else{
            holder.binding.locationName.text = favWeather.address
        }
        holder.binding.favLinearLayout.setOnClickListener {
            myListener.invoke(favWeather)
        }
        holder.binding.menuIcon.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.binding.menuIcon)
            popupMenu.menuInflater.inflate(com.example.wetherforcastapp.R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                if (item.itemId == com.example.wetherforcastapp.R.id.action_delete) {
                    myDeleteListener.invoke(favWeather.address) // Handling delete event
                    true
                } else {
                    false
                }
            }
            popupMenu.show()
        }
    }
}

class FavViewHolder(val binding: FavItemBinding) : RecyclerView.ViewHolder(binding.root)