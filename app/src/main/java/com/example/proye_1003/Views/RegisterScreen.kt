package com.example.proye_1003.Views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.proye_1003.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

// Importar los modelos y el cliente de API (Ajusta la ruta si es necesario)
import com.example.proye_1003.models.RegisterRequest
import com.example.proye_1003.models.Users // Importamos Users para la respuesta
import com.example.proye_1003.services.RetrofitClient


@Composable
fun RegisterScreen(onRegisterSuccess: (String) -> Unit, onBack: () -> Unit) {
    // Campos del modelo RegisterRequest
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Función de ayuda para validar campos básicos
    fun areFieldsValid(): Boolean {
        return nombre.isNotBlank() && apellido.isNotBlank() && telefono.isNotBlank() &&
                email.contains("@") && usuario.isNotBlank() && direccion.isNotBlank() &&
                password.length >= 6 && password == confirmPassword
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Fondo y Scrim (omito contenido para brevedad)
            Image(
                painter = painterResource(id = R.drawable.fondo_farmacia),
                contentDescription = "Fondo farmacia",
                modifier = Modifier.matchParentSize().zIndex(0f),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.45f)))

            // Contenedor de registro
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

                    // Campos de texto
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = usuario, onValueChange = { usuario = it }, label = { Text("Usuario") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo (correo)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirmar Contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(18.dp))

                    // Botón de registro con LÓGICA DE API
                    Button(
                        onClick = {
                            scope.launch {
                                if (!areFieldsValid()) {
                                    snackbarHostState.showSnackbar("Revisa todos los campos y contraseñas (mín. 6 caracteres, deben coincidir)")
                                    return@launch
                                }
                                try {
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

                                    // 🚨 CORRECCIÓN 1: Declaración de respuesta correcta: Response<Users>
                                    val response: Response<Users> = withContext(Dispatchers.IO) {
                                        RetrofitClient.authService.registerUsuario(request)
                                    }

                                    if (response.isSuccessful) {
                                        // 🚨 CORRECCIÓN 2: Lógica de éxito simple, asumiendo que 2xx es registro exitoso
                                        val body = response.body()

                                        if (body != null) {
                                            val nombreUsuario = body.nombre ?: "Usuario"
                                            val msg = "¡Registro exitoso! Bienvenido, $nombreUsuario."
                                            onRegisterSuccess(msg) // Navega a Login
                                        } else {
                                            // 201 Created sin body -> Asumimos éxito por el código HTTP
                                            onRegisterSuccess("Registro exitoso. ¡Inicia sesión!")
                                        }

                                    } else {
                                        // La API regresó 4xx o 5xx (ej. 409 Conflict o 400 Bad Request)
                                        val errorBody = response.errorBody()?.string() ?: "Error de servidor"
                                        snackbarHostState.showSnackbar("Error de API ${response.code()}: $errorBody")
                                    }
                                } catch (e: Exception) {
                                    // Fallo de red (timeout, conexión perdida, etc.)
                                    snackbarHostState.showSnackbar("Error de conexión: ${e.message}")
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
                                    brush = Brush.horizontalGradient(colors = listOf(Color(0xFF00E676), Color(0xFF00C853))),
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