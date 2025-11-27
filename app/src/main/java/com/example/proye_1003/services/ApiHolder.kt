package com.example.proye_1003.services

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * ApiHolder es un contenedor simple que crea una instancia del servicio Retrofit
 * para poder llamar a tu API sin ViewModel ni inyección de dependencias.
 */
object ApiHolder {
    val medsApi: MedicamentoService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            // Si tu API requiere token, puedes descomentar y ajustar:
            /*
            .addInterceptor { chain ->
                val token = "TU_TOKEN_AQUI"
                val req = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(req)
            }
            */
            .build()

        val gson = GsonBuilder().setLenient().create()

        Retrofit.Builder()
            .baseUrl("https://api-farmacia.ngrok.app/") // <- Asegúrate de que termine en "/"
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(MedicamentoService::class.java)
    }
}
