package com.example.proye_1003.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.InputStream

class GeminiService(private val apiKey: InputStream) {

    private val client = OkHttpClient()
    private val baseUrl =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"

    suspend fun enviarTextoAI(texto: String): String = withContext(Dispatchers.IO) {

        val jsonBody = """
            {
              "contents": [
                { "parts": [ { "text": "$texto" } ] }
              ]
            }
        """.trimIndent()

        val body = RequestBody.create(
            "application/json".toMediaType(),
            jsonBody
        )

        val request = Request.Builder()
            .url(baseUrl)
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        val respText = response.body?.string() ?: return@withContext "Sin respuesta de IA"

        return@withContext try {
            val json = JSONObject(respText)
            json
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
        } catch (e: Exception) {
            "Error al leer respuesta IA: $respText"
        }
    }
}
