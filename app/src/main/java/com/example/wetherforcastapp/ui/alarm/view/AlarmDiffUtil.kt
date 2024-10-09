package com.example.wetherforcastapp.ui.alarm.view

import androidx.recyclerview.widget.DiffUtil
import com.example.wetherforcastapp.model.data.database.intyty.EntityAlarm

class AlarmDiffUtil : DiffUtil.ItemCallback<EntityAlarm>() {
    override fun areItemsTheSame(oldItem: EntityAlarm, newItem: EntityAlarm): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: EntityAlarm, newItem: EntityAlarm): Boolean {
        return oldItem == newItem
    }
}
