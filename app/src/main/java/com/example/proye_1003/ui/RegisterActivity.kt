// Kotlin
package com.example.proye_1003.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.proye_1003.data.model.RegisterRequest
import com.example.proye_1003.data.model.RegisterResponse
import com.example.proye_1003.data.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

// Asegúrate de que las clases de la API (RegisterRequest, RegisterResponse, RetrofitClient)
// estén importadas o definidas correctamente.

@Composable
fun RegisterScreen(onRegisterSuccess: (String) -> Unit, onBack: () -> Unit) {
    // Campos de estado para el formulario
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // **Nota:** El fondo de imagen y el scrim deben ser recreados aquí o
            // extraídos a un Composable de fondo común para reutilización.
            // Por simplicidad, se asume que solo el Box central es el principal.

            // Contenedor principal de Registro
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.Center)
                    .zIndex(1f)
                    .shadow(12.dp, RoundedCornerShape(20.dp))
                    .background(color = Color.White.copy(alpha = 0.95f), shape = RoundedCornerShape(20.dp))
                    .padding(30.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Crea tu cuenta", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(20.dp))

                    // Campos de Registro
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Teléfono") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(value = usuario, onValueChange = { usuario = it }, label = { Text("Nombre de Usuario") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirmar contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(18.dp))

                    // Botón de Registro con API
                    Button(
                        onClick = {
                            val passwordsMatch = password == confirmPassword

                            scope.launch {
                                if (!passwordsMatch) {
                                    snackbarHostState.showSnackbar("Las contraseñas no coinciden")
                                    return@launch
                                }
                                // Agrega validación de campos vacíos si es necesario

                                try {
                                    // Asume que RegisterRequest, RegisterResponse y RetrofitClient están accesibles
                                    val request = RegisterRequest(
                                        nombre = nombre,
                                        apellido = apellido,
                                        telefono = telefono,
                                        correo = email,
                                        usuario = usuario,
                                        direccion = direccion,
                                        contrasena = password,
                                        rol = "user"
                                    )

                                    val response: Response<RegisterResponse> = withContext(Dispatchers.IO) {
                                        RetrofitClient.authService.registerUser(request)
                                    }

                                    if (response.isSuccessful) {
                                        val msg = response.body()?.message ?: "Registro exitoso. Ya puedes iniciar sesión."
                                        // Navegar al login y mostrar mensaje de éxito
                                        onRegisterSuccess(msg)
                                    } else {
                                        // Fallo de la API
                                        val code = response.code()
                                        val errorMsg = response.errorBody()?.string() ?: "Error al registrar"
                                        snackbarHostState.showSnackbar("Error de registro: $code. $errorMsg")
                                    }
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error de conexión: ${e.message}")
                                    e.printStackTrace()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF00E676), Color(0xFF00C853))
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Registrar", color = Color.White, fontSize = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { onBack() }) {
                        Text(text = "Volver al inicio de sesión", color = Color(0xFF00C853))
                    }
                }
            }
        }
    }
}