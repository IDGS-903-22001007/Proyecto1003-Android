package com.example.proye_1003.Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.models.Cita
import com.example.proye_1003.services.CitaService
import com.example.proye_1003.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CitaViewModel : ViewModel() {

    private val citaService: CitaService by lazy {
        RetrofitClient.citaService
    }

    private val _citas = MutableStateFlow<List<Cita>>(emptyList())
    val citas: StateFlow<List<Cita>> = _citas

    private val _estado = MutableStateFlow<String?>(null)
    val estado: StateFlow<String?> = _estado

    /** 🔹 Cargar todas las citas del paciente */
    fun cargarCitas(idPaciente: Int) {
        viewModelScope.launch {
            try {
                val response = citaService.obtenerCitasPorPaciente(idPaciente)
                if (response.isSuccessful) {
                    _citas.value = response.body() ?: emptyList()
                } else {
                    _estado.value = "❌ Error ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                _estado.value = "⚠️ Error de conexión: ${e.message}"
            }
        }
    }

    /** 🔹 Limpiar citas en memoria (opcional) */
    fun limpiarCitas() {
        _citas.value = emptyList()
    }
}
