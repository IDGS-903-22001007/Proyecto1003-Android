package com.example.proye_1003.citas.viewmodel

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

    private val _citaDetalle = MutableStateFlow<Cita?>(null)
    val citaDetalle: StateFlow<Cita?> = _citaDetalle

    fun cargarCitas() {
        viewModelScope.launch {
            try {
                val response = citaService.obtenerCitas()
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

    fun cargarCitaPorId(id: Int) {
        viewModelScope.launch {
            try {
                val response = citaService.obtenerCitaPorId(id)
                if (response.isSuccessful) {
                    _citaDetalle.value = response.body()
                } else {
                    _estado.value = "❌ Error ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                _estado.value = "⚠️ Error de conexión: ${e.message}"
            }
        }
    }

    fun limpiarDetalle() {
        _citaDetalle.value = null
    }

    fun eliminarCita(idCita: Int) {
        viewModelScope.launch {
            try {
                val response = citaService.eliminarCita(idCita)

                if (response.isSuccessful) {
                    _citas.value = _citas.value.filterNot { it.idCita == idCita }
                    _estado.value = "Cita eliminada correctamente"
                } else {
                    _estado.value = "Error al eliminar cita (${response.code()})"
                }
            } catch (e: Exception) {
                _estado.value = "Error de conexión: ${e.message}"
            }
        }
    }

}
