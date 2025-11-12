package com.example.myunievents.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Announcement(
    val title: String = "",
    val description: String = "",
    val createdBy: String = "",
    val authorId: String = "",
    val dateTimeMillis: Long = 0L,
    @ServerTimestamp val createdAt: Date? = null
)
