package com.example.myunievents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myunievents.data.local.entities.EventEntity
import com.example.myunievents.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventDetailViewModel(private val repo: EventRepository) : ViewModel() {

    private val _event = MutableStateFlow<EventEntity?>(null)
    val event: StateFlow<EventEntity?> = _event

    fun getEvent(eventId: Long) {
        viewModelScope.launch {
            _event.value = repo.getEvent(eventId)
        }
    }
}