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

    override suspend fun searchUserByUsername(query: String): Result<List<UserProfile>> =
        runCatching {
            val snapshot = db.collection("users").whereGreaterThanOrEqualTo("userName", query)
                .whereLessThanOrEqualTo("userName", query + "\uf8ff").get().await()

            snapshot.toObjects(UserProfile::class.java)
        }

    override suspend fun sendFriendRequest(toUid: String): Result<Unit> = runCatching {
        val uid = currentUserId ?: throw IllegalStateException("User don`t logged in")
        val profile = getUserProfile().getOrThrow() ?: throw IllegalStateException("Error")
        val friendRequest =
            FriendRequest(
                fromUid = uid,
                fromName = profile.name,
                fromUserName = profile.userName,
                fromPhotoUrl = profile.photoUrl
            )

        db.collection("users").document(toUid).collection("friendRequests").document(uid)
            .set(friendRequest).await()
    }

    override val incomingFriendRequests: Flow<List<FriendRequest>> = callbackFlow {
        val uid: String? = currentUserId
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

    override val friends: Flow<List<Friend>> = callbackFlow {
        val uid: String? = currentUserId
        val registration =
            db.collection("users/$uid/friends").addSnapshotListener { snapshots, error ->
                if (error != null) {
                    close(error)
                } else {
                    val friendList = snapshots?.toObjects(Friend::class.java)
                    trySend(friendList ?: emptyList())
                }
            }
        awaitClose { registration.remove() }
    }


    override suspend fun acceptFriendRequest(fromUid: String): Result<Unit> = runCatching {
        val uid = currentUserId ?: throw IllegalStateException("Error")
        val snapshot =
            db.collection("users").document(uid).collection("friendRequests").document(fromUid)
                .get().await()

        val friendProfile = snapshot.toObject(FriendRequest::class.java)
        val profile = getUserProfile().getOrThrow() ?: throw IllegalStateException("Error")
        val batch = db.batch()

        val userRequest = Friend(
            friendUid = fromUid,
            fromName = friendProfile?.fromName ?: "",
            fromUserName = friendProfile?.fromUserName ?: "",
            fromPhotoUrl = friendProfile?.fromPhotoUrl ?: "",
        )
        val friendRequest = Friend(
            friendUid = uid,
            fromName = profile.name,
            fromUserName = profile.userName,
            fromPhotoUrl = profile.photoUrl
        )

        val myFriendRef = db.collection("users").document(uid).collection("friends").document(fromUid)
        val theirFriendRef = db.collection("users").document(fromUid).collection("friends").document(uid)
        val requestRef = db.collection("users").document(uid).collection("friendRequests").document(fromUid)

        batch.set(myFriendRef, userRequest)
        batch.set(theirFriendRef, friendRequest)
        batch.delete(requestRef)
        batch.commit().await()
    }

    override suspend fun declineFriendRequest(fromUid: String): Result<Unit> = runCatching {
        val uid = currentUserId ?: throw IllegalStateException("Error")
        db.collection("users").document(uid).collection("friendRequests").document(fromUid).delete()
            .await()
    }


    override suspend fun removeFriend(friendUid: String): Result<Unit> = runCatching {
        val uid = currentUserId ?: throw IllegalStateException("Error")
        val batch = db.batch()
        val userDelete =
            db.collection("users").document(uid).collection("friends").document(friendUid)
        batch.delete(userDelete)
        val friendDelete =
            db.collection("users").document(friendUid).collection("friends").document(uid)
        batch.delete(friendDelete)
        batch.commit().await()
    }

    override suspend fun getUserProfile(uid: String): Result<UserProfile?> = runCatching {
        val snapshot = db.collection("users").document(uid).get().await()
        if (snapshot.exists()) {
            snapshot.toObject(UserProfile::class.java)
        } else {
            null
        }
    }

}