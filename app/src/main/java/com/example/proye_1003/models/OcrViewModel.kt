        package com.example.proye_1003.models

        import androidx.lifecycle.ViewModel
        import androidx.lifecycle.viewModelScope
        import kotlinx.coroutines.launch
        import okhttp3.MultipartBody
        import okhttp3.OkHttpClient
        import okhttp3.Interceptor
        import okhttp3.RequestBody
        import okhttp3.RequestBody.Companion.asRequestBody
        import okhttp3.MediaType.Companion.toMediaType
        import retrofit2.Retrofit
        import retrofit2.converter.gson.GsonConverterFactory
        import retrofit2.http.Multipart
        import retrofit2.http.POST
        import retrofit2.http.Part
        import retrofit2.Response
        import java.io.File

        // -------------------- RESPUESTA DEL OCR --------------------
        data class ParsedResult(
            val ParsedText: String
        )

        data class OcrResponse(
            val ParsedResults: List<ParsedResult>
        )

        // -------------------- INTERFAZ RETROFIT --------------------
        interface OcrService {
            @Multipart
            @POST("parse/image")
            suspend fun parseImage(
                @Part file: MultipartBody.Part
            ): Response<OcrResponse>
        }

        // -------------------- VIEWMODEL --------------------
        class OcrViewModel : ViewModel() {

            private val apiKey = "K81066755388957"

            private val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://api.ocr.space/") // Endpoint OCR
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor { chain: Interceptor.Chain ->
                            val request = chain.request().newBuilder()
                                .addHeader("apikey", apiKey)
                                .build()
                            chain.proceed(request)
                        }
                        .build()
                )
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            private val service: OcrService = retrofit.create(OcrService::class.java)

            fun procesarImagen(
                file: File,
                onSuccess: (String) -> Unit,
                onError: (String) -> Unit
            ) {
                viewModelScope.launch {
                    try {
                        // Aquí usamos las extensiones correctamente importadas
                        val requestFile: RequestBody = file.asRequestBody("image/*".toMediaType())
                        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                        val response = service.parseImage(body)
                        if (response.isSuccessful) {
                            val texto = response.body()?.ParsedResults?.firstOrNull()?.ParsedText ?: ""
                            onSuccess(texto)
                        } else {
                            onError("Error de OCR: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        onError("Error: ${e.message}")
                    }
                }
            }
        }
