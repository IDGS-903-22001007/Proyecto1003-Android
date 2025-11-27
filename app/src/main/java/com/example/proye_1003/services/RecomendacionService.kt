package com.example.proye_1003.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Data que devuelve tu backend
data class RecomendacionResponse(
    val recomendacion: List<String> // lista de nombres de medicamentos
)

interface RecomendacionService {
    @POST("recomendacion") // tu endpoint en el backend
    suspend fun getRecomendacion(@Body texto: String): Response<RecomendacionResponse>
}
