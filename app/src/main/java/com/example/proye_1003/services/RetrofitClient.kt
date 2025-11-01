package com.example.proye_1003.services

import com.example.proye_1003.Auth.AuthApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // ⚠️ SIN /api al final (termina con /)
    private const val BASE_URL = "https://api-farmacia.ngrok.app/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // Instancia única de Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Exponemos servicios ya tipados
    val authService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val citaService: CitaService by lazy {
        retrofit.create(CitaService::class.java)
    }

    val medicamentoService: MedicamentoService by lazy {
        retrofit.create(MedicamentoService::class.java)
    }

    // (opcional) Si alguna vez necesitas el retrofit crudo:
    val retrofitInstance: Retrofit get() = retrofit
}
