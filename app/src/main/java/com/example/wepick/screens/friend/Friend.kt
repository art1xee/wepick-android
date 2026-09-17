package com.example.wepick.screens.friend

data class Friend(
    val friendUid: String = "",
    val fromName: String = "",
    val fromUserName: String = "",
    val fromPhotoUrl: String? = null,
    val since: Long = System.currentTimeMillis(),
)
