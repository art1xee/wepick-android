package com.example.wepick.screens.friend

data class FriendRequest(
    val fromUid: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val fromName: String = "",
    val fromUserName: String = "",
    val fromPhotoUrl: String? = null
)
