package com.example.proye_1003.Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.models.Cita
import com.example.proye_1003.services.CitaService
import com.example.proye_1003.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CitaViewModel: ViewModel() {
    // Instanciamos el servicio desde el mismo RetrofitClient existente
    private val citaService: CitaService by lazy {
        RetrofitClient.authService as CitaService // ⚠️ Mejoraremos esto abajo
    }

    private val _citas = MutableStateFlow<List<Cita>>(emptyList())
    val citas: StateFlow<List<Cita>> = _citas

    fun cargarCitas(idPaciente: Int) {
        viewModelScope.launch {
            try {
                val response = citaService.obtenerCitasPorPaciente(idPaciente)
                if (response.isSuccessful) {
                    _citas.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun agregarCita(cita: Cita) {
        viewModelScope.launch {
            try {
                citaService.registrarCita(cita)
                cargarCitas(cita.idPaciente)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun eliminarCita(idCita: Int, idPaciente: Int) {
        viewModelScope.launch {
            try {
                citaService.eliminarCita(idCita)
                cargarCitas(idPaciente)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}