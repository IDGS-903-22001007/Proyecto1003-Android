package com.example.proye_1003.services

import com.example.proye_1003.Auth.AuthApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api-farmacia.ngrok.app/api/"

    // 1. Crear el Interceptor de Logging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Muestra el header, body y el estado de la petición
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    // 2. Añadir el Interceptor al Cliente HTTP
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val gson = GsonBuilder().create()

    val authService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // Usa el cliente con logging
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AuthApiService::class.java)
    }

    val medicamentoService: MedicamentoService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(MedicamentoService::class.java)
    }


}