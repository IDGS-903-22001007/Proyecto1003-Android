package com.example.proye_1003.Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.models.Cita
import com.example.proye_1003.services.CitaService
import com.example.proye_1003.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CitaCreateViewModel : ViewModel() {

    private val _estado = MutableStateFlow<String?>(null)
    val estado: StateFlow<String?> = _estado

    private val citaService: CitaService by lazy {
        // Asegúrate de agregarlo en RetrofitClient (ver abajo 👇)
        RetrofitClient.citaService
    }

    fun registrarCita(cita: Cita) {
        viewModelScope.launch {
            try {
                val response = citaService.registrarCita(cita)
                if (response.isSuccessful) {
                    _estado.value = "✅ Cita registrada correctamente"
                } else {
                    _estado.value = "❌ Error ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                _estado.value = "Error al conectar con la API: ${e.message}"
            }
        }
    }
}
