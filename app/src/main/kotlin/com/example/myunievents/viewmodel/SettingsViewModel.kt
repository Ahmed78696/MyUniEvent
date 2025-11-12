package com.example.myunievents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myunievents.data.preferences.ThemePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val prefs: ThemePreferences): ViewModel() {
    val isDark: StateFlow<Boolean> = prefs.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun setDark(enabled: Boolean) {
        viewModelScope.launch { prefs.setDarkMode(enabled) }
    }
}
