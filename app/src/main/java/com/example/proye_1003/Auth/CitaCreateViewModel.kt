package com.example.proye_1003.Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.models.Cita
import com.example.proye_1003.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CitaCreateViewModel : ViewModel() {

    private val citaService = RetrofitClient.citaService

    private val _estado = MutableStateFlow<String?>(null)
    val estado: StateFlow<String?> = _estado

    fun registrarCita(cita: Cita) {
        viewModelScope.launch {
            try {
                val response = citaService.registrarCita(cita)
                if (response.isSuccessful) {
                    _estado.value = "✅ Cita registrada correctamente"
                } else {
                    _estado.value = "❌ Error al registrar la cita"
                }
            } catch (e: Exception) {
                _estado.value = "⚠️ Error: ${e.message}"
            }
        }
    }
}
