package com.example.wepick

import com.google.gson.annotations.SerializedName

data class TmdbResponse (
    val results: List<TmdbMovie>
    )

data class TmdbMovie(
    val id: Int,
    val title: String?,
    val name: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    val overview: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?
)

fun TmdbMovie.toContentItem(): ContentItem {
    val displayTitle = this.title ?: this.name ?: "Unknown"

    val fullDate = this.releaseDate ?: this.firstAirDate ?: "0000"
    val yearString = if (fullDate.length >= 4) fullDate.take(4) else "0000"

    return ContentItem(
        id = this.id,
        title = displayTitle,
        posterPath = "https://image.tmdb.org/t/p/w500${this.posterPath}",
        rating = this.voteAverage ?: 0.0,
        overview = this.overview ?: "",
        mediaType = if (this.title != null) "movie" else "tv",
        releaseDate = yearString
    )
}

