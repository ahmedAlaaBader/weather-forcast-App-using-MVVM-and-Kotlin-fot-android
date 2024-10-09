package com.example.wetherforcastapp.model.data.database.intyty

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = "TAlarms")
data class EntityAlarm(
    @PrimaryKey @NotNull val id: Int,
    val date: String,
    val time: String,
    val alarm: Boolean
) : Serializable