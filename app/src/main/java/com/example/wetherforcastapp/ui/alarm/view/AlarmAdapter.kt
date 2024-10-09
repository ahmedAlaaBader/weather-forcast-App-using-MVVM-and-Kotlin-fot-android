package com.example.wetherforcastapp.ui.alarm.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherforcastapp.R
import com.example.wetherforcastapp.databinding.AlarmItemBinding
import com.example.wetherforcastapp.model.data.database.intyty.EntityAlarm

class AlarmAdapter(
    var myDeleteListener: (Int,String) -> Unit
) : ListAdapter<EntityAlarm, AlarmViewHolder>(
    AlarmDiffUtil()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = AlarmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = getItem(position)
        holder.binding.startTime.text = alarm.date // Binding data to UI
        holder.binding.dateText.text = alarm.time
        holder.binding.menuIcon.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.binding.menuIcon)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.action_delete) {
                    myDeleteListener.invoke(alarm.id,alarm.time)
                    true
                } else {
                    false
                }
            }
            popupMenu.show()
        }
    }
}

class AlarmViewHolder(val binding: AlarmItemBinding) : RecyclerView.ViewHolder(binding.root)