package com.example.proye_1003.Auth

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.models.Users
import com.example.proye_1003.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AuthViewRegister : ViewModel() {

    private val _registerState = MutableStateFlow<String?>(null)
    val registerState: StateFlow<String?> = _registerState

    @RequiresApi(Build.VERSION_CODES.O)
    fun registrarUsuario(
        nombre: String,
        apellido: String,
        telefono: String,
        correo: String,
        user: String,
        direccion: String,
        rol: String = "cliente",
        activo: Boolean = true
    ) {
        viewModelScope.launch {
            try {
                val fechaActual = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)

                val nuevoUsuario = Users(
                    id = 0, // el backend normalmente genera el ID
                    nombre = nombre,
                    apellido = apellido,
                    telefono = telefono,
                    correo = correo,
                    user = user,
                    direccion = direccion,
                    rol = rol,
                    activo = activo,
                    fechaCreacion = fechaActual
                )

                val response = RetrofitClient.authService.registerUsuario(nuevoUsuario)

                if (response.isSuccessful) {
                    _registerState.value = "Registro exitoso"
                } else {
                    _registerState.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _registerState.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun clearMessage() {
        _registerState.value = null
    }
}

