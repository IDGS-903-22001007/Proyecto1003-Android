package com.example.proye_1003

data class RegisterRequest(
    val nombre: String,
    val apellido: String,
    val telefono: String,
    val correo: String,
    val usuario: String,
    val direccion: String,
    val contrasena: String,
    val rol: String = "user" // Asumo que el rol por defecto debe ser 'Cliente'
)
