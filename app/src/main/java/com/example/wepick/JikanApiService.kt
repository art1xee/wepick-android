package com.example.wepick

import retrofit2.http.GET
import retrofit2.http.Query

interface JikanApiService {
    @GET("top/anime")
    suspend fun getTopAnime(
        @Query("page") page: Int = 1
    ): JikanResponse

    @GET("anime")
    suspend fun searchAnime(
        @Query("genres") genres: String? = null,
        @Query("page") page: Int = 1,
        @Query("order_by") orderBy: String = "score",
        @Query("sort") sort: String = "desc",
        @Query("min_score") minScore: Double = 7.0,
        @Query("limit") limit: Int = 25,
    ): JikanResponse
}