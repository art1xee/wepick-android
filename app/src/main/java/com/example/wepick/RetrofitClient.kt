package com.example.wepick

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val TMDB_BASE_URL = BuildConfig.TMDB_BASE_URL
    private const val JIKAN_BASE_URL = BuildConfig.JIKAN_BASE_URL


    val instanceTmdb: TmdbApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(TmdbApiService::class.java)
    }

    val instanceJikan: JikanApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(JIKAN_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(JikanApiService::class.java)
    }
}