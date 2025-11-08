package com.example.proye_1003.models

data class Cita(
    val idCita: Int? = null,
    val idPaciente: Int,
    val fechaHora: String,         // formato ISO: yyyy-MM-dd'T'HH:mm:ss
    val tipoConsulta: String,
    val notas: String? = null,
    val estatus: String? = "A",
    val duracionMin: Int = 30
)
