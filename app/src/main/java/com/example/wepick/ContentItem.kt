package com.example.wepick

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializer

/*
* Класс, который имеет информацию про выбранный контент (фильмы, сериалы и аниме).
*/
data class ContentItem(
    val id: Int,
    @SerializedName(value = "title", alternate = ["name"])
    val title: String,
    @SerializedName(value = "poster_path", alternate = ["image_url"])
    val posterPath: String?,
    val overview: String,
    @SerializedName("vote_average")
    val rating: Double = 0.0,
    val mediaType: String,
)
