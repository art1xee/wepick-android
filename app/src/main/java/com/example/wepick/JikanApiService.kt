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
        @Query("q") query: String
    ): JikanResponse
}