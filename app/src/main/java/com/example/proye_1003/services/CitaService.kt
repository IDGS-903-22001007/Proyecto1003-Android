package com.example.proye_1003.services

import com.example.proye_1003.models.Cita
import retrofit2.Response
import retrofit2.http.*

interface CitaService {

    @GET("citas")
    suspend fun obtenerCitasPorPaciente(
        @Query("idPaciente") idPaciente: Int
    ): Response<List<Cita>>

    @POST("citas/registrar")
    suspend fun registrarCita(
        @Body cita: Cita
    ): Response<Cita>

    @PUT("citas/{idCita}")
    suspend fun actualizarCita(
        @Path("idCita") idCita: Int,
        @Body cita: Cita
    ): Response<Cita>

    @DELETE("citas/{idCita}")
    suspend fun eliminarCita(
        @Path("idCita") idCita: Int
    ): Response<Unit>
}
