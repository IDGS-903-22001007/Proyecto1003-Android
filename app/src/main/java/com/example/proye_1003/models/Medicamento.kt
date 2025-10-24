package com.example.proye_1003.models



data class Medicamento(
    val id: Int,
    val nombre: String,
    val cantidad: Int,
    val tipo: String,
    val precio: Double,
    val descripcion: String,
    val fotoUrl: String?,
    val activo: Boolean,
    val fechaCreacion: String
)