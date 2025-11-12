package com.example.myunievents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myunievents.data.local.entities.EventEntity
import com.example.myunievents.data.repository.EventRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventsViewModel(private val repo: EventRepository): ViewModel() {
    val events: StateFlow<List<EventEntity>> =
        repo.observeLocal().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addEvent(
        title: String,
        desc: String,
        location: String,
        millis: Long,
        category: String,
        createdBy: String,
        publishToAnnouncements: Boolean,
        isPublic: Boolean
    ) {
        if (title.isBlank() || location.isBlank()) return
        viewModelScope.launch {
            val event = EventEntity(
                title = title.trim(),
                description = desc.trim(),
                location = location.trim(),
                dateTimeMillis = millis,
                category = category,
                createdBy = createdBy,
                publishedToCloud = false
            )
            val id = repo.addLocal(event)
            val savedEvent = event.copy(id = id)

            if (publishToAnnouncements) {
                try { repo.pushToCloud(savedEvent) } catch (_: Exception) {}
            }
            if (isPublic) {
                try { repo.pushToPublicCloud(savedEvent) } catch (_: Exception) {}
            }
        }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch {
            repo.deleteLocal(event)
        }
    }
}