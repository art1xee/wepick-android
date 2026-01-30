package com.example.wepick

import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ru_RU",
    ): TmdbResponse

    @GET("search/movie")
    suspend fun movieSearch(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "ru_RU",
    ): TmdbResponse

    @GET("discover/movie")
    suspend fun getDiscoveryMovie(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genres: String,
        @Query("primary_release_date.gte") dateStart: String,
        @Query("primary_release_date.lte") dateEnd: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "ru_RU",
    ): TmdbResponse

    @GET("tv/popular")
    suspend fun getPopularTV(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ru_RU",
    ): TmdbResponse

    @GET("search/tv")
    suspend fun tvSearch(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "ru_RU",
    ): TmdbResponse

    @GET("discover/tv")
    suspend fun getDiscoveryTV(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genres: String,
        @Query("first_air_date.gte") dateStart: String,
        @Query("first_air_date.lte") dateEnd: String,
        @Query("language") language: String = "ru_RU",
    ): TmdbResponse
}