package com.example.proye_1003.models

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.Response
import java.io.File
import com.example.proye_1003.services.GeminiService
import kotlinx.coroutines.Dispatchers

data class ParsedResult(val ParsedText: String)
data class OcrResponse(val ParsedResults: List<ParsedResult>)

interface OcrService {
    @Multipart
    @POST("parse/image")
    suspend fun parseImage(
        @Part file: MultipartBody.Part,
        @Part("language") language: okhttp3.RequestBody,
        @Part("isOverlayRequired") isOverlayRequired: okhttp3.RequestBody
    ): Response<OcrResponse>
}


class OcrViewModel : ViewModel() {

    private val apiKey = "K81066755388957"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.ocr.space/")
        .client(
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("apikey", apiKey)
                        .build()
                    chain.proceed(request)
                }.build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: OcrService = retrofit.create(OcrService::class.java)

    // Estados para la UI
    var textoDetectado = mutableStateOf("")
    var error = mutableStateOf("")
    var cargando = mutableStateOf(false)

    fun procesarImagen(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val requestFile = file.asRequestBody("image/*".toMediaType())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val languageBody = "spa".toRequestBody("text/plain".toMediaType())
                val overlayBody = "false".toRequestBody("text/plain".toMediaType())

                val response = service.parseImage(body, languageBody, overlayBody)

                if (response.isSuccessful) {
                    val texto = response.body()?.ParsedResults
                        ?.mapNotNull { it.ParsedText?.trim() }
                        ?.filter { it.isNotEmpty() }
                        ?.joinToString("\n") ?: ""

                    if (texto.isNotEmpty()) {
                        textoDetectado.value = texto
                    } else {
                        error.value = "No se pudo detectar texto"
                    }
                } else {
                    error.value = "Error OCR: ${response.code()}"
                }
            } catch (e: Exception) {
                error.value = "Excepción OCR: ${e.message}"
            }
        }
    }
}

