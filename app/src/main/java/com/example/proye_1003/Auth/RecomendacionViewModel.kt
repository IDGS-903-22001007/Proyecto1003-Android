package com.example.proye_1003.Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.models.Medicamento
import com.example.proye_1003.services.RecomendacionService
import com.example.proye_1003.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecomendacionViewModel : ViewModel() {

    // === Flujo de datos para las recomendaciones ===
    private val _recomendaciones = MutableStateFlow<List<Medicamento>>(emptyList())
    val recomendaciones: StateFlow<List<Medicamento>> get() = _recomendaciones

    // === Flujo para errores ===
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // === Servicio de Retrofit ===
    private val service: RecomendacionService = RetrofitClient.recomendacionService

    fun obtenerRecomendaciones(texto: String) {
        viewModelScope.launch {
            try {
                val response = service.getRecomendacion(texto)
                if (response.isSuccessful) {
                    val lista = response.body()?.recomendacion.orEmpty() // usa orEmpty() para evitar null
                    _recomendaciones.value = lista.map { nombre ->
                        Medicamento(
                            nombre = nombre,
                            tipo = "",
                            cantidad = 1,
                            precio = 0.0,
                            descripcion = "",
                            fotoUrl = null
                        )
                    }
                    _error.value = null
                } else {
                    _error.value = "Error API: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de red: ${e.localizedMessage ?: e.toString()}"
            }
        }
    }
}
