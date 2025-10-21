package com.example.proye_1003.data


import com.example.proye_1003.services.OcrService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import kotlin.getValue

object OcrApi {

    private const val BASE_URL = "https://api.ocr.space/"
    private const val API_KEY = "K81066755388957" // <-- pon tu token


    private val client = OkHttpClient.Builder()
        .addInterceptor { chain: Interceptor.Chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", API_KEY)
                .build()
            chain.proceed(request)
        }
        .build()
    val service: OcrService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OcrService::class.java)
    }

}
