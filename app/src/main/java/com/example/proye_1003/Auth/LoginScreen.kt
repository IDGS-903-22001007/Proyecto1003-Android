package com.example.proye_1003.Auth

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

// Importaciones
import com.example.proye_1003.R
import com.example.proye_1003.models.LoginRequest // Contiene 'user' y 'contrasena'
import com.example.proye_1003.models.Users
import com.example.proye_1003.services.RetrofitClient
import okhttp3.ResponseBody

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    initialMessage: String? = null,
    onMessageShown: () -> Unit = {}
) {
    // Variables de estado ajustadas a LoginRequest
    var user by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 🚨 CORRECCIÓN CLAVE: Lógica para mostrar el mensaje de registro (Snackbar)
    LaunchedEffect(initialMessage) {
        initialMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Long) // Mostrar la Snackbar
            onMessageShown() // Notificar al NavHost para que limpie el argumento
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, // Contenedor del Snackbar
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            // ... (Fondo, Scrim, etc.)
            Image(
                painter = painterResource(id = R.drawable.fondo_farmacia),
                contentDescription = "Fondo farmacia",
                modifier = Modifier.matchParentSize().zIndex(0f),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.45f)))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.Center)
                    .zIndex(1f)
                    .shadow(12.dp, RoundedCornerShape(20.dp))
                    .background(color = Color.White.copy(alpha = 0.95f), shape = RoundedCornerShape(20.dp))
                    .padding(30.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Bienvenido a ", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Farmacia", color = Color(0xFF00C853), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(25.dp))

                    OutlinedTextField(
                        value = user,
                        onValueChange = { user = it },
                        label = { Text("Usuario") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF00C853), focusedLabelColor = Color(0xFF00C853))
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF00C853), focusedLabelColor = Color(0xFF00C853))
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón iniciar sesión con LÓGICA DE API
                    Button(
                        onClick = {
                            scope.launch {
                                if (user.isBlank() || contrasena.isBlank()) {
                                    snackbarHostState.showSnackbar("Introduce usuario y contraseña")
                                    return@launch
                                }
                                try {
                                    val request = LoginRequest(user = user, contrasena = contrasena)

                                    val response: Response<Users> = withContext(Dispatchers.IO) {
                                        RetrofitClient.authService.login(request)
                                    }

                                    if (response.isSuccessful && response.body() != null) {
                                        val usuarioRespuesta = response.body()!!
                                        snackbarHostState.showSnackbar("¡Bienvenido, ${usuarioRespuesta.nombre ?: "Usuario"}! Inicio exitoso.", duration = SnackbarDuration.Long)
                                        // TODO: Navegar a la pantalla principal aquí
                                    } else {
                                        val errorMsg = response.errorBody()?.string() ?: "Credenciales inválidas o error desconocido"
                                        snackbarHostState.showSnackbar("Error ${response.code()}: $errorMsg", duration = SnackbarDuration.Long)
                                    }
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error de conexión: ${e.message}", duration = SnackbarDuration.Long)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(
                                brush = Brush.horizontalGradient(colors = listOf(Color(0xFF00E676), Color(0xFF00C853))),
                                shape = RoundedCornerShape(10.dp)
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Iniciar sesión", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
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