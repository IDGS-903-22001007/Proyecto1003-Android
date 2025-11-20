package com.example.proye_1003.services

import com.example.proye_1003.models.Cita
import com.example.proye_1003.models.SlotResponse
import retrofit2.Response
import retrofit2.http.*

interface CitaService {

    // 🔹 GET /api/Citas -> Todas las citas del usuario autenticado (requiere JWT)
    @GET("api/Citas")
    suspend fun obtenerCitas(): Response<List<Cita>>

    // 🔹 GET /api/Citas/{id} -> Cita específica por ID
    @GET("api/Citas/{id}")
    suspend fun obtenerCitaPorId(@Path("id") id: Int): Response<Cita>

    // 🔹 POST /api/Citas -> Crear nueva cita
    @POST("api/Citas")
    suspend fun registrarCita(@Body cita: Cita): Response<Cita>

    // 🔹 PUT /api/Citas/{id} -> Actualizar cita existente
    @PUT("api/Citas/{id}")
    suspend fun actualizarCita(
        @Path("id") id: Int,
        @Body cita: Cita
    ): Response<Cita>

    // 🔹 DELETE /api/Citas/{id} -> Cancelar cita
    @DELETE("api/Citas/{id}")
    suspend fun eliminarCita(@Path("id") id: Int): Response<Unit>

    // 🔹 GET /api/Citas/slots -> Consultar horarios disponibles
    @GET("api/Citas/slots")
    suspend fun obtenerSlots(@Query("dia") dia: String): Response<List<SlotResponse>>
}
