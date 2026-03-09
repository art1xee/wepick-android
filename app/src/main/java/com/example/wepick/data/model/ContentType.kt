package com.example.wepick.data.model

sealed class ContentType(val name: String) {
    object Movie : ContentType("movie")
    object Tv : ContentType("series")
    object Anime : ContentType("anime")
}