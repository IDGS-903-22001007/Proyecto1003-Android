package com.example.proye_1003.Auth

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.models.SlotResponse
import com.example.proye_1003.services.RetrofitClient
import kotlinx.coroutines.launch

class CitaHorariosViewModel : ViewModel() {

    // Lista de horas disponibles (ej. ["09:00","09:30",...])
    private val _horas = mutableStateOf<List<String>>(emptyList())
    val horas: State<List<String>> = _horas

    // Indicador de carga
    private val _cargando = mutableStateOf(false)
    val cargando: State<Boolean> = _cargando

    // Mensaje de error
    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    /**
     * Llama al backend:
     * GET /api/Citas/slots?dia=YYYY-MM-DD
     * Y carga los horarios disponibles para la fecha dada.
     */
    fun cargarHorasDisponibles(fechaISO: String) {
        viewModelScope.launch {
            try {
                _cargando.value = true
                _error.value = null
                _horas.value = emptyList()

                val response = RetrofitClient.citaService.obtenerSlots(fechaISO)

                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()

                    // Filtra solo los disponibles
                    val disponibles = lista
                        .filter { it.disponible }
                        .map { it.horaTexto }

                    _horas.value = disponibles
                } else {
                    _error.value = "Error al cargar horarios (${response.code()})"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }
}
