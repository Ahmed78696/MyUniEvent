package com.example.myunievents.data.repository

import com.example.myunievents.data.local.dao.EventDao
import com.example.myunievents.data.local.entities.EventEntity
import com.example.myunievents.data.remote.FirebaseRepository
import kotlinx.coroutines.flow.Flow

class EventRepository(
    private val dao: EventDao,
    private val firebase: FirebaseRepository
) {
    fun observeLocal(): Flow<List<EventEntity>> = dao.getAllEvents()

    suspend fun getEvent(eventId: Long): EventEntity? = dao.getById(eventId)

    suspend fun addLocal(event: EventEntity) = dao.insert(event)

    suspend fun deleteLocal(event: EventEntity) = dao.delete(event)

    suspend fun markPublished(event: EventEntity) = dao.update(event.copy(publishedToCloud = true))

    suspend fun pushToCloud(event: EventEntity) {
        firebase.publishEventAnnouncement(event)
        markPublished(event)
    }

    suspend fun pushToPublicCloud(event: EventEntity) {
        firebase.publishPublicEvent(event)
    }

    suspend fun unpublished() = dao.getUnpublished()
}
