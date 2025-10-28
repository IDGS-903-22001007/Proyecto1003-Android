package com.example.proye_1003.services

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class IAService(private val apiKey: String) {

    private val client = OkHttpClient()
    private val baseUrl =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    fun enviarPrompt(prompt: String): String {
        // Construir el JSON del prompt
        val json = buildJsonObject {
            put("prompt", buildJsonObject {
                put("text", prompt)
            })
        }.toString()

        // Crear RequestBody
        val mediaType = "application/json".toMediaType()
        val body = json.toRequestBody(mediaType)

        // Crear la request
        val request = Request.Builder()
            .url(baseUrl)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        // Ejecutar la request
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Error al llamar a Gemini API: ${response.code} ${response.message}")
            }
            return response.body?.string() ?: ""
        }
    }
}
