package com.example.wepick

import com.google.gson.annotations.SerializedName

data class JikanResponse(
    val data: List<AnimeItem>
)

data class AnimeItem(
    @SerializedName("mal_id") val malId: Int,
    val title: String,
    val synopsis: String?,
    val images: JikanImages,
    @SerializedName("score") val score: Double?,
    val year: Int?,
    val aired: Aired
)

data class Aired(
    @SerializedName("from") val from: String?
)
fun AnimeItem.toContentItem(): ContentItem {
    val yearString = this.year?.toString()
        ?: this.aired.from?.take(4)
        ?: "0000"

    return ContentItem(
        id = this.malId,
        title = this.title,
        posterPath = this.images.jpg.imageUrl,
        rating = this.score ?: 0.0,
        overview = this.synopsis ?: "",
        mediaType = "anime",
        releaseDate = yearString
    )
}

data class JikanImages(
    val jpg: JikanJpg
)

data class JikanJpg(
    @SerializedName("image_url") val imageUrl: String
)


