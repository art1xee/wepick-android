package com.example.wepick.screens.auth.profile_setup

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val userName: String = "",
    val bio: String = "",
    val birthday: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val profileCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)