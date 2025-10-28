package com.example.proye_1003.Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.data.Mensaje
import com.example.proye_1003.services.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val geminiService: GeminiService) : ViewModel() {

    private val _mensajes = MutableStateFlow<List<Mensaje>>(emptyList())
    val mensajes: StateFlow<List<Mensaje>> = _mensajes

    fun enviarMensaje(texto: String) {
        val nuevo = Mensaje(texto, true)
        _mensajes.value += nuevo

        // Llamar a IA
        viewModelScope.launch {
            val respuesta = geminiService.enviarTextoAI(texto)
            _mensajes.value += Mensaje(respuesta, false)
        }
    }
}