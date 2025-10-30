package com.example.proye_1003.models

import com.google.gson.annotations.SerializedName

data class Medicamento(
    val id: Int?,
    val nombre: String?,
    val descripcion: String?,
    val beneficios: String?,
    val instrucciones: String?,
    val advertencias: String?,
    val tipo: String?,
    val cantidad: Int?,
    val precio: Double?,
    @SerializedName(value = "fotoUrl", alternate = ["imagenUrl"])
    val fotoUrl: String?,      // ✅ Compatible con ambos nombres
    val activo: Boolean?
)
