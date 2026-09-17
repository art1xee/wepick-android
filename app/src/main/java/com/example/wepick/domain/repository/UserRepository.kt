package com.example.wepick.domain.repository

import android.net.Uri
import com.example.wepick.screens.auth.profile_setup.UserProfile
import com.example.wepick.screens.friend.Friend
import com.example.wepick.screens.friend.FriendRequest
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val currentUserId: String?
    val isGoogleAuth: Boolean
    val authStateFlow: Flow<Boolean>

    suspend fun getUserProfile(): Result<UserProfile?>
    suspend fun saveUserProfile(profile: UserProfile): Result<Unit>
    suspend fun updateProfileFields(fields: Map<String, Any>): Result<Unit>
    suspend fun uploadAvatar(imageUri: Uri): Result<String>
    suspend fun isUsernameAvailable(username: String): Result<Boolean>
    suspend fun isEmailAvailable(email: String): Result<Boolean>
    suspend fun signOut(): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun setPrivacy(isPrivacy: Boolean): Result<Unit>
    suspend fun setPushEnabled(pushEnabled: Boolean): Result<Unit>
    suspend fun setEmailEnabled(emailEnabled: Boolean): Result<Unit>
    suspend fun updateFcmToken(token: String): Result<Unit>
    suspend fun sendPasswordResetEmail(): Result<Unit>

    //friend block
    suspend fun searchUserByUsername(query: String): Result<List<UserProfile>>
    suspend fun sendFriendRequest(toUid: String): Result<Unit>
    val incomingFriendRequests: Flow<List<FriendRequest>>
    suspend fun acceptFriendRequest(fromUid: String): Result<Unit>
    suspend fun declineFriendRequest(fromUid: String): Result<Unit>
    val friends: Flow<List<Friend>>
    suspend fun removeFriend(friendUid: String): Result<Unit>
    suspend fun getUserProfile(uid: String): Result<UserProfile?>
}