package com.example.proye_1003.models

data class Cita(
    val idCita: Int? = null,
    val idPaciente: Int,
    val nombrePaciente: String? = null,
    val fechaHora: String,
    val tipoConsulta: String,
    val notas: String? = null,
    val estatus: String? = "A",
    val duracionMin: Int = 30,
    val observaciones: String? = null,
    val diagnostico: String? = null,
    val medicamentos: String? = null
)
