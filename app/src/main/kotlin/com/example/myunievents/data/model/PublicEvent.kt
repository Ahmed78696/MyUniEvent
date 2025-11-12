package com.example.myunievents.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PublicEvent(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val category: String = "",
    val dateTimeMillis: Long = 0L,
    val createdBy: String = "",
    val authorId: String = "",
    @ServerTimestamp val createdAt: Date? = null
)
