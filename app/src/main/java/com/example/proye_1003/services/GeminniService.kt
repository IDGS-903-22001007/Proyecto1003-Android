package com.example.proye_1003.services

import MessageForAI
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
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
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

@Serializable
data class MedicamentoSimple(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val cantidad: Int
) {
    companion object
}

class GeminiService {
    private val apiKey = "AIzaSyDw2xADlKwV8ixgkuOdcMoSeAy7uvKTABs"

    // Timeout aumentado
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val baseUrl =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

    suspend fun enviarTextoAI(texto: String): String = withContext(Dispatchers.IO) {
        try {

            Log.d("AI","Preparando request con texto: $texto")

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

            Log.d("AI","Enviando request a Gemini")
            Log.d("Gemini", "URL: $baseUrl")


            client.newCall(request).execute().use { res ->

                Log.d("AI","Código de respuesta: ${res.code}")

                val raw = res.body?.string()
                Log.d("AI","Raw recibido: $raw")

                if (!res.isSuccessful) {
                    return@withContext "Error Gemini: ${res.code} - $raw"
                }

                if (raw.isNullOrEmpty()) {
                    return@withContext "Error: respuesta vacía"
                }

                // --- PARSEO ---
                val json = Json.parseToJsonElement(raw).jsonObject
                val candidates = json["candidates"]?.jsonArray ?: return@withContext "Sin candidatos"

                val firstCandidate = candidates.first().jsonObject
                val content = firstCandidate["content"]?.jsonObject ?: return@withContext "Sin contenido"

                val parts = content["parts"]?.jsonArray ?: return@withContext "Sin partes"

                val textoRespuesta =
                    parts.first().jsonObject["text"]?.jsonPrimitive?.content ?: "Sin texto"

                return@withContext textoRespuesta
            }

        } catch (e: Exception) {
            Log.e("AI", "Error IA: ", e)  // <--- log real del error con stacktrace
            "Error IA: ${e.message}"
        }
    }
    suspend fun buscarMedicamentosSimilares(
        medicamentos: List<MedicamentoSimple>,
        query: String
    ): String = withContext(Dispatchers.IO) {
        try {
            val medsJson = Json.encodeToString(ListSerializer(MedicamentoSimple.serializer()), medicamentos)
            val prompt = """
            Tienes esta lista de medicamentos: $medsJson
            El usuario preguntó: "$query"
            Devuelve solo los medicamentos que sean similares o relevantes según la consulta.
            Incluye nombre, precio y cantidad de forma concisa.
        """.trimIndent()

            val jsonRequest = """
        {
          "contents":[
            {"parts":[{"text":${JSONObject.quote(prompt)}}]}
          ]
        }
        """.trimIndent()

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
                val content = candidates.first().jsonObject["content"]?.jsonObject ?: return@withContext "Sin contenido"
                val parts = content["parts"]?.jsonArray ?: return@withContext "Sin partes"
                return@withContext parts.first().jsonObject["text"]?.jsonPrimitive?.content ?: "Sin texto"
            }
        } catch (e: Exception) {
            "Error IA: ${e.message}"
        }
    }




    suspend fun enviarConversacion(history: List<MessageForAI>): String = withContext(Dispatchers.IO) {
        try {
            // Construir JSON de la conversación
            val contents = buildJsonArray {
                add(buildJsonObject {
                    put("parts", buildJsonArray {
                        history.forEach { msg ->
                            add(buildJsonObject {
                                put("text", msg.content)  // texto del mensaje
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
                val textoRespuesta = parts.first().jsonObject["text"]?.jsonPrimitive?.content ?: "Sin texto"
                return@withContext textoRespuesta
            }

        } catch (e: Exception) {
            Log.e("AI", "Error IA: ", e)
            "Error IA: ${e.message}"
        }
    }


}