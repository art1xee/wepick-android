package com.example.wepick.data.repository

import android.net.Uri
import androidx.core.net.toUri
import com.example.wepick.domain.repository.UserRepository
import com.example.wepick.screens.auth.profile_setup.UserProfile
import com.example.wepick.screens.friend.Friend
import com.example.wepick.screens.friend.FriendRequest
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

    override suspend fun signOut(): Result<Unit> {
        return runCatching {
            auth.signOut()
        }
    }

    override suspend fun deleteAccount(): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("User don`t exist")
        db.collection("users").document(user.uid).delete().await()
        user.delete().await()
    }

    override suspend fun setPrivacy(isPrivacy: Boolean): Result<Unit> {
        return updateProfileFields(
            fields = mapOf("isPrivate" to isPrivacy)
        )
    }

    override suspend fun setEmailEnabled(emailEnabled: Boolean): Result<Unit> {
        return updateProfileFields(
            fields = mapOf("emailEnabled" to emailEnabled)
        )
    }

    override suspend fun setPushEnabled(pushEnabled: Boolean): Result<Unit> {
        return updateProfileFields(
            fields = mapOf("pushEnabled" to pushEnabled)
        )
    }

    override suspend fun updateFcmToken(token: String): Result<Unit> {
        return updateProfileFields(fields = mapOf("fcmToken" to token))
    }

    override suspend fun sendPasswordResetEmail(): Result<Unit> = runCatching {
        val email = auth.currentUser?.email ?: throw IllegalStateException("User don`t logged in")
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun searchUserByUsername(query: String): Result<List<UserProfile>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendFriendRequest(toUid: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override val incomingFriendRequests: Flow<List<FriendRequest>> = callbackFlow {
        val uid = currentUserId
        val registration =
            db.collection("users/$uid/friendRequests").addSnapshotListener { snapshots, error ->
                if (error != null) {
                    close(error)
                } else {
                    val friendList = snapshots?.toObjects(FriendRequest::class.java)
                    trySend(friendList ?: emptyList())
                }

            }
        awaitClose { registration.remove() }
    }

    override suspend fun acceptFriendRequest(fromUid: String): Result<Unit> {
        TODO("No yet implement")
    }

    override suspend fun declineFriendRequest(fromUid: String): Result<Unit> = runCatching {

        val friendRequest = auth.?: throw IllegalStateException("Something went wrong")
        db.collection("friendRequest").document(friendRequest.uid).delete().await()

    }


    override val friends: Flow<List<Friend>>
        get() = TODO("Not yet implemented")

    override suspend fun removeFriend(friendUid: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserProfile(uid: String): Result<UserProfile?> {
        TODO("Not yet implemented")
    }
}