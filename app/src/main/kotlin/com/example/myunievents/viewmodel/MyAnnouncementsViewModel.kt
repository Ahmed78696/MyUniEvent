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

class MyAnnouncementsViewModel(private val repo: FirebaseRepository) : ViewModel() {

    private val _myAnnouncements = MutableStateFlow<List<Announcement>>(emptyList())
    val myAnnouncements: StateFlow<List<Announcement>> = _myAnnouncements

    init {
        viewModelScope.launch {
            repo.getMyAnnouncements()
                .catch { Log.e("MyAnnouncementsVM", "Error fetching my announcements", it) }
                .collect { announcements ->
                    _myAnnouncements.value = announcements
                }
        }
    }
}