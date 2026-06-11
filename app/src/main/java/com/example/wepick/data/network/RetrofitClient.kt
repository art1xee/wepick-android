package com.example.wepick.data.network

import com.example.wepick.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val TMDB_BASE_URL = BuildConfig.TMDB_BASE_URL
    private const val JIKAN_BASE_URL = BuildConfig.JIKAN_BASE_URL


    val instanceTmdb: TmdbApiService by lazy {
        val retrofit = Retrofit.Builder()
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build())
            .baseUrl(TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(TmdbApiService::class.java)
    }

    val instanceJikan: JikanApiService by lazy {
        val retrofit = Retrofit.Builder()
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build())
            .baseUrl(JIKAN_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(JikanApiService::class.java)
    }
}