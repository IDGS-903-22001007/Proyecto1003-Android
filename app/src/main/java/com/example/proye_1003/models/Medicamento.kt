package com.example.proye_1003.models



data class Medicamento(
    val id: Int = 0,
    val nombre: String,
    val tipo: String = "",
    val cantidad: Int = 0,
    val precio: Double = 0.0,
    val descripcion: String = "",
    val fotoUrl: String? = null,
    val fechaCreacion: String = "",
    val activo: Boolean = true
)
