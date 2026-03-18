package com.example.a4200gp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String?,
    val dateTime: Long,
    val category: String,
    val priority: Int,
    val location: String?,
    val reminderTime: Long?,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
