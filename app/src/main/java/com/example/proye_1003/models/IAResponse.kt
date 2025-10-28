package com.example.proye_1003.models

// models/Mensaje.kt
// models/RespuestaGemini.kt
import kotlinx.serialization.Serializable

@Serializable
data class RespuestaGemini(
    val es_receta: Boolean = false,
    val ingredientes: List<String> = emptyList(),
    val pasos: List<String> = emptyList(),
    val recomendaciones: List<String> = emptyList(),
    val escalados: Map<String, List<String>> = emptyMap(),
    val es_lista_compra: Boolean = false,
    val categorias: Map<String, List<String>> = emptyMap(),
    val lista_compra: List<String> = emptyList(),
    val texto_bruto: String? = null // para recomendaciones tipo “dame recomendacion”
)
