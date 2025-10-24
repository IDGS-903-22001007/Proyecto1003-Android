package com.example.proye_1003.services



import com.example.proye_1003.models.Medicamento
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MedicamentoService {
    @GET("Medicamentos")
    suspend fun getMedicamentos(): Response<List<Medicamento>>
}