package com.example.myunievents.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val location: String,
    val dateTimeMillis: Long,
    val category: String,
    val createdBy: String,
    var publishedToCloud: Boolean
)
