package com.example.myunievents.data.remote

import android.net.Uri
import com.example.myunievents.data.local.entities.EventEntity
import com.example.myunievents.data.model.Announcement
import com.example.myunievents.data.model.PublicEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    private val announcementsCol = firestore.collection("announcements")
    private val publicEventsCol = firestore.collection("public_events")

    suspend fun publishEventAnnouncement(event: EventEntity) {
        val currentUserId = auth.currentUser?.uid ?: return
        val announcement = Announcement(
            title = event.title,
            description = event.description,
            createdBy = event.createdBy,
            authorId = currentUserId,
            dateTimeMillis = event.dateTimeMillis
        )
        announcementsCol.add(announcement).await()
    }

    suspend fun publishPublicEvent(event: EventEntity) {
        val currentUserId = auth.currentUser?.uid ?: return
        val publicEvent = PublicEvent(
            title = event.title,
            description = event.description,
            dateTimeMillis = event.dateTimeMillis,
            location = event.location,
            category = event.category,
            createdBy = event.createdBy,
            authorId = currentUserId
        )
        publicEventsCol.add(publicEvent).await()
    }

    fun getPublicEvents(): Flow<List<PublicEvent>> {
        return publicEventsCol
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(PublicEvent::class.java) }
    }

    fun getPublicAnnouncements(): Flow<List<Announcement>> {
        return announcementsCol
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Announcement::class.java) }
    }

    fun getMyAnnouncements(): Flow<List<Announcement>> {
        val currentUserId = auth.currentUser?.uid ?: return flowOf(emptyList())
        return announcementsCol
            .whereEqualTo("authorId", currentUserId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Announcement::class.java) }
    }

    suspend fun signIn(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password).await()

    suspend fun signUp(email: String, password: String) =
        auth.createUserWithEmailAndPassword(email, password).await()

    fun signOut() = auth.signOut()

    fun currentUser() = auth.currentUser

    suspend fun updateProfile(displayName: String?) {
        val user = auth.currentUser ?: return
        val updates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        user.updateProfile(updates).await()
    }

    suspend fun uploadAvatar(bytes: ByteArray, fileName: String = "avatar.jpg"): String {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")
        val ref = storage.reference.child("avatars/$uid/$fileName")
        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun setAvatarUrlInProfile(downloadUrl: String) {
        val user = auth.currentUser ?: return
        val updates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .setPhotoUri(Uri.parse(downloadUrl))
            .build()
        user.updateProfile(updates).await()
    }
}