package com.example.proye_1003.services

import com.example.proye_1003.Auth.AuthApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

object RetrofitClient {

    private const val BASE_URL = "https://api-farmacia.ngrok.app/api/"

    // Interceptor para ver logs de peticiones/respuestas
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP con interceptor
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Configuración de Gson
    private val gson = GsonBuilder().create()

    // ✅ Instancia única de Retrofit (aquí estaba el error)
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    // Servicios API
    val authService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val citaService: CitaService by lazy {
        retrofit.create(CitaService::class.java)
    }

    val medicamentoService: MedicamentoService by lazy {
        retrofit.create(MedicamentoService::class.java)
    }
    val recomendacionService: RecomendacionService by lazy {
        retrofit.create(RecomendacionService::class.java)
    }


}
