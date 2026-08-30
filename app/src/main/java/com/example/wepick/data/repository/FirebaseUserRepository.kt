package com.example.wepick.data.repository

import android.net.Uri
import androidx.core.net.toUri
import com.example.wepick.domain.repository.UserRepository
import com.example.wepick.screens.auth.profile_setup.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseUserRepository(
    private val auth: FirebaseAuth = Firebase.auth,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
) : UserRepository {

    override val currentUserId: String?
        get() = auth.currentUser?.uid

    override val isGoogleAuth: Boolean
        get() = auth.currentUser?.providerData?.any { it.providerId == "google.com" } == true

    override val authStateFlow: Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }
    override suspend fun getUserProfile(): Result<UserProfile?> = runCatching {
        val uid = currentUserId ?: return@runCatching null
        val snapshot = db.collection("users").document(uid).get().await()
        if (snapshot.exists()) {
            snapshot.toObject(UserProfile::class.java)
        } else {
            // if document don't created in the db, taking default values from AUTH
            val user = auth.currentUser
            UserProfile(
                uid = uid,
                email = user?.email ?: "",
                name = if (isGoogleAuth) user?.displayName ?: "" else "",
                photoUrl = user?.photoUrl?.toString(),
                userName = ""
            )
        }
    }

    override suspend fun saveUserProfile(profile: UserProfile): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("User not logged in")

        db.collection("users").document(user.uid).set(profile).await()

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(profile.name)
            .apply {
                if (!profile.photoUrl.isNullOrEmpty()) {
                    photoUri = profile.photoUrl.toUri()
                }
            }.build()

        user.updateProfile(profileUpdates).await()
    }

    override suspend fun updateProfileFields(fields: Map<String, Any>): Result<Unit> = runCatching {
        val uid = currentUserId ?: throw IllegalStateException("User not logged in")
        db.collection("users").document(uid).update(fields).await()
    }

    override suspend fun uploadAvatar(imageUri: Uri): Result<String> = runCatching {
        val uid = currentUserId ?: throw IllegalStateException("User not logged in")
        val storageRef = storage.reference.child("profile_images/$uid.jpg")

        storageRef.putFile(imageUri).await()
        val downloadUrl = storageRef.downloadUrl.await().toString()

        db.collection("users").document(uid).update("photoUrl", downloadUrl).await()
        downloadUrl
    }

    override suspend fun isUsernameAvailable(username: String): Result<Boolean> = runCatching {
        val snapshot = db.collection("users").whereEqualTo("userName", username).get().await()
        val uid = currentUserId
        snapshot.documents.none { it.id != uid }
    }

    override suspend fun isEmailAvailable(email: String): Result<Boolean> = runCatching {
        val snapshot = db.collection("users").whereEqualTo("email", email).get().await()
        val uid = currentUserId
        snapshot.documents.none { it.id != uid }
    }
}