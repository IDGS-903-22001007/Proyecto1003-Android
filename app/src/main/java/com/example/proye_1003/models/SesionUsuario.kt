package com.example.proye_1003.models

object SesionUsuario {
    var idUsuario: Int? = null
    var nombre: String? = null
    var correo: String? = null

    fun limpiarSesion() {
        idUsuario = null
        nombre = null
        correo = null
    }
}