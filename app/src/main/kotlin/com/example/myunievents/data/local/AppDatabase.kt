package com.example.myunievents.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myunievents.data.local.dao.EventDao
import com.example.myunievents.data.local.entities.EventEntity

@Database(entities = [EventEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}
