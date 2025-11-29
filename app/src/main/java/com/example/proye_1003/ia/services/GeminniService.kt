package com.example.proye_1003.ia.services

import android.util.Log
import com.example.proye_1003.ia.data.Mensaje
import com.example.proye_1003.ia.data.MessageForAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import java.util.concurrent.TimeUnit

@Serializable
data class MedicamentoSimple(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val cantidad: Int
)

class GeminiService {

    private val apiKey = "AIzaSyCiLYIU2e5yCutebUGxrCTeImyQezZ0F-A"

    // Timeout aumentado
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val baseUrl =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

    // ----------------------------------------------------------
    // 🔹 1. ENVÍO DE TEXTO
    // ----------------------------------------------------------
    suspend fun enviarTextoAI(texto: String): String = withContext(Dispatchers.IO) {
        try {

            val jsonRequest = """
            {
              "contents": [
                {
                  "parts": [
                    { "text": ${JSONObject.quote(texto)} }
                  ]
                }
              ]
            }
            """.trimIndent()

            val body = jsonRequest.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(baseUrl)
                .post(body)
                .build()

            client.newCall(request).execute().use { res ->

                val raw = res.body?.string()

                if (!res.isSuccessful) {
                    return@withContext "Error Gemini: ${res.code} - $raw"
                }

                if (raw.isNullOrEmpty()) {
                    return@withContext "Error: respuesta vacía"
                }

                val json = Json.parseToJsonElement(raw).jsonObject
                val candidates = json["candidates"]?.jsonArray ?: return@withContext "Sin candidatos"
                val firstCandidate = candidates.first().jsonObject
                val content = firstCandidate["content"]?.jsonObject ?: return@withContext "Sin contenido"
                val parts = content["parts"]?.jsonArray ?: return@withContext "Sin partes"

                return@withContext parts.first().jsonObject["text"]?.jsonPrimitive?.content ?: "Sin texto"
            }

        } catch (e: Exception) {
            Log.e("AI", "Error IA: ", e)
            "Error IA: ${e.message}"
        }
    }

    // ----------------------------------------------------------
    // 🔹 2. BUSCAR MEDICAMENTOS
    // ----------------------------------------------------------
    suspend fun buscarMedicamentosSimilares(
        medicamentos: List<MedicamentoSimple>,
        query: String
    ): String = withContext(Dispatchers.IO) {
        try {
            val medsJson = Json.encodeToString(
                ListSerializer(MedicamentoSimple.serializer()),
                medicamentos
            )
            val prompt = """
            Tienes esta lista de medicamentos: $medsJson
            El usuario preguntó: "$query"
            Devuelve solo los medicamentos que sean similares o relevantes según la consulta.
        """.trimIndent()

            val jsonRequest = """
            {
              "contents":[
                {"parts":[{"text":${JSONObject.quote(prompt)}}]}
              ]
            }
            """.trimIndent()

            val body = jsonRequest.toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(baseUrl).post(body).build()

            client.newCall(request).execute().use { res ->
                if (!res.isSuccessful) return@withContext "Error Gemini: ${res.code}"

                val raw = res.body?.string() ?: return@withContext "Respuesta vacía"
                val json = Json.parseToJsonElement(raw).jsonObject
                val candidates = json["candidates"]?.jsonArray ?: return@withContext "Sin candidatos"
                val content = candidates.first().jsonObject["content"]?.jsonObject ?: return@withContext "Sin contenido"
                val parts = content["parts"]?.jsonArray ?: return@withContext "Sin partes"
                return@withContext parts.first().jsonObject["text"]?.jsonPrimitive?.content ?: "Sin texto"
            }

        } catch (e: Exception) {
            "Error IA: ${e.message}"
        }
    }

    // ----------------------------------------------------------
    // 🔹 3. ENVÍO DE CONVERSACIÓN (Chat)
    // ----------------------------------------------------------
    suspend fun enviarConversacion(history: List<Mensaje>): String = withContext(Dispatchers.IO) {
        try {
            // Construir JSON de la conversación
            val contents = buildJsonArray {
                add(buildJsonObject {
                    put("parts", buildJsonArray {
                        history.forEach { msg ->
                            add(buildJsonObject {
                                put("text", msg.texto)  // ← USAR Texto del Mensaje
                            })
                        }
                    })
                })
            }

            val jsonRequest = buildJsonObject {
                put("contents", contents)
            }.toString()

            val body = jsonRequest.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(baseUrl)
                .post(body)
                .build()

            client.newCall(request).execute().use { res ->
                if (!res.isSuccessful) return@withContext "Error Gemini: ${res.code}"

                val raw = res.body?.string() ?: return@withContext "Respuesta vacía"
                val json = Json.parseToJsonElement(raw).jsonObject
                val candidates = json["candidates"]?.jsonArray ?: return@withContext "Sin candidatos"
                val firstCandidate = candidates.first().jsonObject
                val content = firstCandidate["content"]?.jsonObject ?: return@withContext "Sin contenido"
                val parts = content["parts"]?.jsonArray ?: return@withContext "Sin partes"
                return@withContext parts.first().jsonObject["text"]?.jsonPrimitive?.content ?: "Sin texto"
            }

        } catch (e: Exception) {
            Log.e("AI", "Error IA", e)
            "Error IA: ${e.message}"
        }
    }
}
