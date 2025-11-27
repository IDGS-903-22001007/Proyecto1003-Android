package com.example.proye_1003.Auth

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.models.RegisterRequest
import com.example.proye_1003.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
        contrasena: String,
        rol: String = "user",
        activo: Boolean = true
    ) {
        viewModelScope.launch {
            try {
                val requestBody = RegisterRequest(
                    nombre = nombre,
                    apellido = apellido,
                    telefono = telefono,
                    correo = correo,
                    usuario = user,
                    direccion = direccion,
                    contrasena = contrasena,
                    rol = rol
                )

                val response = RetrofitClient.authService.registerUsuario(requestBody)

                Log.d("AuthViewRegister", "Registro request: $requestBody")
                Log.d("AuthViewRegister", "Registro response code: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("AuthViewRegister", "Registro response body: $body")

                    if (body != null) {
                        // Usar un mensaje claro y corto para mostrar al usuario
                        _registerState.value = "Registro exitoso"
                    } else {
                        // Respuesta 2xx pero body vacío -> informar con mensaje claro
                        _registerState.value = "Registro exitoso"
                    }
                } else {
                    // Leer body de error para entender detalles
                    val errorBody = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        null
                    }
                    Log.d("AuthViewRegister", "Registro errorBody: $errorBody")
                    _registerState.value = "Error (${response.code()}): ${errorBody ?: response.message()}"
                }
            } catch (e: Exception) {
                Log.e("AuthViewRegister", "Excepción al registrar", e)
                _registerState.value = "Error de conexión: ${e.localizedMessage}"
            }
        }
    }

    fun clearMessage() {
        _registerState.value = null
    }
}
