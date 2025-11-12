package com.example.myunievents

import android.content.Context
import androidx.room.Room
import com.example.myunievents.data.local.AppDatabase
import com.example.myunievents.data.preferences.ThemePreferences
import com.example.myunievents.data.remote.FirebaseRepository
import com.example.myunievents.data.repository.EventRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object ServiceLocator {
    private fun provideDatabase(context: Context): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "myuni.db")
        .fallbackToDestructiveMigration()
        .build()

    fun provideThemePrefs(context: Context): ThemePreferences = ThemePreferences(context.applicationContext)

    fun provideFirebaseRepo(): FirebaseRepository = FirebaseRepository(
        FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance()
    )

    fun provideEventRepo(context: Context): EventRepository = EventRepository(
        provideDatabase(context).eventDao(),
        provideFirebaseRepo()
    )
}
