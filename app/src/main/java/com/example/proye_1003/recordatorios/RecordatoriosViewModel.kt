package com.example.proye_1003.recordatorios

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.recordatorios.data.RecordatorioEntity
import com.example.proye_1003.recordatorios.domain.RecordatorioRepository
import kotlinx.coroutines.launch

class RecordatoriosViewModel(
    private val repo: RecordatorioRepository,
    private val idUsuario: Int
) : ViewModel() {

    var lista by mutableStateOf<List<RecordatorioEntity>>(emptyList())
        private set

    init {
        recargar()
    }

    fun recargar() {
        viewModelScope.launch {
            lista = repo.getActivosPorUsuario(idUsuario)
        }
    }

    fun agregar(
        nombre: String,
        frecuenciaHoras: Int,
        fechaInicioMillis: Long,
        duracionDias: Int,
        dosis: String
    ) {
        viewModelScope.launch {
            repo.agregar(
                RecordatorioEntity(
                    idUsuario = idUsuario,
                    nombreMedicamento = nombre,
                    frecuenciaHoras = frecuenciaHoras,
                    fechaInicioMillis = fechaInicioMillis,
                    duracionDias = duracionDias,
                    dosis = dosis
                )
            )
            recargar()
        }
    }

    fun actualizar(r: RecordatorioEntity) {
        viewModelScope.launch {
            repo.actualizar(r)
            recargar()
        }
    }

    fun eliminar(r: RecordatorioEntity) {
        viewModelScope.launch {
            repo.eliminar(r)
            recargar()
        }
    }

    fun reprogramar() {
        viewModelScope.launch {
            repo.reprogramarTodos(idUsuario)
            recargar()
        }
    }
}
