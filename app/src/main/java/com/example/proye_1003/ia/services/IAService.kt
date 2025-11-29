package com.example.proye_1003.ia.services

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.serialization.json.*

class IAService(private val apiKey: String) {

    private val client = OkHttpClient()
    private val baseUrl =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"

    fun enviarPrompt(prompt: String): String {
        val jsonRequest = buildJsonObject {
            put("contents", buildJsonArray {
                add(buildJsonObject {
                    put("parts", buildJsonArray {
                        add(buildJsonObject {
                            put("text", prompt)
                        })
                    })
                })
            })
        }.toString()

        val mediaType = "application/json".toMediaType()
        val body = jsonRequest.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(baseUrl)
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Error API Gemini: ${response.code} ${response.message}")
            }

            val jsonResponse = response.body?.string() ?: ""

            return Json.parseToJsonElement(jsonResponse)
                .jsonObject["candidates"]
                ?.jsonArray?.firstOrNull()
                ?.jsonObject?.get("content")
                ?.jsonObject?.get("parts")
                ?.jsonArray?.firstOrNull()
                ?.jsonObject?.get("text")
                ?.jsonPrimitive?.content
                ?: "Sin respuesta del modelo"
        }
    }
}
