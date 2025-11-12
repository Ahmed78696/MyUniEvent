package com.example.myunievents.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myunievents.data.model.PublicEvent
import com.example.myunievents.data.remote.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DiscoverViewModel(private val repo: FirebaseRepository) : ViewModel() {

    private val _publicEvents = MutableStateFlow<List<PublicEvent>>(emptyList())
    val publicEvents: StateFlow<List<PublicEvent>> = _publicEvents

    init {
        viewModelScope.launch {
            repo.getPublicEvents()
                .catch { Log.e("DiscoverViewModel", "Error fetching public events", it) }
                .collect { events ->
                    _publicEvents.value = events
                }
        }
    }
}