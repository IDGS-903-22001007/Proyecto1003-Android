package com.example.proye_1003.models

import com.google.gson.annotations.SerializedName

data class Medicamento(
    val id: Int? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val beneficios: String? = null,
    val instrucciones: String? = null,
    val advertencias: String? = null,
    val tipo: String? = null,
    val cantidad: Int? = null,
    val precio: Double? = null,
    @SerializedName(value = "fotoUrl", alternate = ["imagenUrl"])
    val fotoUrl: String? = null,
    val activo: Boolean? = null
)
