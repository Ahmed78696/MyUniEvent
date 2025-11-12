package com.example.myunievents.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myunievents.data.model.Announcement
import com.example.myunievents.data.remote.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AnnouncementsViewModel(private val repo: FirebaseRepository) : ViewModel() {

    private val _items = MutableStateFlow<List<Announcement>>(emptyList())
    val items: StateFlow<List<Announcement>> = _items

    init {
        viewModelScope.launch {
            repo.getPublicAnnouncements()
                .catch { Log.e("AnnouncementsViewModel", "Error fetching public announcements", it) }
                .collect { announcements ->
                    _items.value = announcements
                }
        }
    }
}