package com.example.proye_1003.Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.models.LoginRequest
import com.example.proye_1003.models.Users
import com.example.proye_1003.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewLogin : ViewModel() {

    private val _loginState = MutableStateFlow<String?>(null)
    val loginState: StateFlow<String?> = _loginState

    fun login(user: String, password: String, onSuccess: (Users) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.authService.login(
                    LoginRequest(user = user, contrasena = password)
                )
                if (response.isSuccessful) {
                    val usuario = response.body()
                    if (usuario != null) {
                        onSuccess(usuario)
                    } else {
                        _loginState.value = "Usuario o contraseña incorrecta"
                    }
                } else {
                    _loginState.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _loginState.value = "Error de conexión: ${e.localizedMessage}"
            }
        }
    }

    fun clearMessage() {
        _loginState.value = null
    }
}