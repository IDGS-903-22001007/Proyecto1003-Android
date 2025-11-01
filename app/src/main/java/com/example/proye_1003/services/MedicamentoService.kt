package com.example.proye_1003.services

import com.example.proye_1003.models.Medicamento
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MedicamentoService {

    // GET /api/Medicamentos
    @GET("api/Medicamentos")
    suspend fun getMedicamentos(
        @Query("q") q: String? = null
    ): List<Medicamento>

    // GET /api/Medicamentos/{id}
    @GET("api/Medicamentos/{id}")
    suspend fun getById(
        @Path("id") id: Int
    ): Medicamento
}
