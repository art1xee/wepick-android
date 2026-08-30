package com.example.wepick.domain.repository

import android.net.Uri
import com.example.wepick.screens.auth.profile_setup.UserProfile
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

}