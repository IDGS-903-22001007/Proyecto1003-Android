package com.example.proye_1003.models

data class Cita(
    val idCita: Int? = null,
    val idPaciente: Int,
    val fechaCita: String,
    val tipoConsulta: String,
    val notas: String?,
    val estatus: String? = "A"
)
