package com.example.proye_1003.recordatorios.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordatorios")
data class RecordatorioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idUsuario: Int,
    val nombreMedicamento: String,
    val frecuenciaHoras: Int,
    val fechaInicioMillis: Long,
    val duracionDias: Int,
    val dosis: String,
    val activo: Boolean = true
)
