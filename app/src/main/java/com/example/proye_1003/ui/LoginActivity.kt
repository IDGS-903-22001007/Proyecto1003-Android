// Kotlin
package com.example.proye_1003.ui

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.proye_1003.data.model.LoginRequest
import com.example.proye_1003.data.model.LoginResponse
import com.example.proye_1003.R
import com.example.proye_1003.data.api.RetrofitClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

// Asegúrate de que R.drawable.fondo_farmacia sea accesible
// y que las clases de la API (LoginRequest, LoginResponse, RetrofitClient)
// estén importadas o definidas correctamente.

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    initialMessage: String? = null,
    onMessageShown: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(initialMessage) {
        initialMessage?.let {
            snackbarHostState.showSnackbar(it)
            onMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Fondo de imagen
            Image(
                painter = painterResource(id = R.drawable.fondo_farmacia),
                contentDescription = "Fondo farmacia",
                modifier = Modifier
                    .matchParentSize()
                    .zIndex(0f),
                contentScale = ContentScale.Crop
            )

            // Scrim (capa de oscurecimiento)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            )

            // Contenedor principal de Login
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.Center)
                    .zIndex(1f)
                    .shadow(12.dp, RoundedCornerShape(20.dp))
                    .background(
                        color = Color.White.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(30.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Bienvenido a ",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Farmacia",
                        color = Color(0xFF00C853),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(25.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Usuario") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00C853),
                            focusedLabelColor = Color(0xFF00C853)
                        )
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00C853),
                            focusedLabelColor = Color(0xFF00C853)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón iniciar sesión con llamada a API usando Retrofit
                    Button(
                        onClick = {
                            scope.launch {
                                val emailValue = email
                                val passwordValue = password

                                if (emailValue.isBlank() || passwordValue.isBlank()) {
                                    snackbarHostState.showSnackbar("Introduce correo y contraseña")
                                    return@launch
                                }

                                try {
                                    // Asume que LoginRequest, LoginResponse y RetrofitClient están accesibles
                                    val request =
                                        LoginRequest(user = emailValue, contrasena = passwordValue)

                                    val response: Response<LoginResponse> = withContext(Dispatchers.IO) {
                                        RetrofitClient.authService.login(request)
                                    }

                                    if (response.isSuccessful && response.body() != null) {
                                        val loginResponse = response.body()!!
                                        // Éxito:
                                        snackbarHostState.showSnackbar("¡Bienvenido, ${loginResponse.message ?: "Inicio exitoso"}! Token recibido.")
                                        // TODO: Navegar a la pantalla principal después de un login exitoso
                                    } else {
                                        // Fallo de la API: (ej. credenciales incorrectas)
                                        val code = response.code()
                                        snackbarHostState.showSnackbar("Error de inicio de sesión: Código $code")
                                    }
                                } catch (e: Exception) {
                                    // Fallo de conexión (red, timeout, etc.):
                                    snackbarHostState.showSnackbar("Error de conexión: ${e.message}")
                                    e.printStackTrace()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
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
                            Text(
                                text = "Iniciar sesión",
                                color = Color.White,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { onNavigateToRegister() }) {
                        Text(text = "¿No tienes cuenta? Crear cuenta", color = Color(0xFF00C853))
                    }
                }
            }
        }
    }
}