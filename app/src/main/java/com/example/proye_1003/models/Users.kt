package com.example.proye_1003.models


data class Users(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val telefono: String,
    val correo: String,
    val user: String,
    val direccion: String?,
    val rol: String,
    val activo: Boolean,
    val fechaCreacion: String
)
