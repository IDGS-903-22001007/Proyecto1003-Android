package com.example.proye_1003.services

import com.example.proye_1003.Auth.AuthApiService
import com.example.proye_1003.models.Users
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


object RetrofitClient {
    private const val BASE_URL = "https://api-farmacia.ngrok.app/api/"

    private val client = OkHttpClient.Builder().build()
    private val gson = GsonBuilder().create()

    val authService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AuthApiService::class.java)
    }
}