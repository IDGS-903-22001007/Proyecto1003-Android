package com.example.proye_1003.services


import com.example.proye_1003.models.OcrResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OcrService {
    @Multipart
    @POST("parse/image")
    suspend fun parseImage(
        @Part file: MultipartBody.Part
    ): Response<OcrResponse>
}
