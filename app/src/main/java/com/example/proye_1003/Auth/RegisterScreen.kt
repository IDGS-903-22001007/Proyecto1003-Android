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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.proye_1003.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

import com.example.proye_1003.models.RegisterRequest
import com.example.proye_1003.models.RegisterResponse
// Si tu API devuelve el usuario creado, también tienes este modelo.
// import com.example.proye_1003.models.Users
import com.example.proye_1003.services.RetrofitClient

@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onBack: () -> Unit
) {
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

    fun areFieldsValid(): Boolean {
        return nombre.isNotBlank() &&
                apellido.isNotBlank() &&
                telefono.isNotBlank() &&
                usuario.isNotBlank() &&
                direccion.isNotBlank() &&
                email.contains("@") &&
                password.length >= 6 &&
                password == confirmPassword
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
            Image(
                painter = painterResource(id = R.drawable.fondo_farmacia),
                contentDescription = "Fondo farmacia",
                modifier = Modifier
                    .matchParentSize()
                    .zIndex(0f),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            )

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
                        "Crea tu cuenta",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(20.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = apellido,
                        onValueChange = { apellido = it },
                        label = { Text("Apellido") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = usuario,
                        onValueChange = { usuario = it },
                        label = { Text("Usuario") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Dirección") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                if (!areFieldsValid()) {
                                    snackbarHostState.showSnackbar(
                                        "Revisa los campos y contraseñas (mín. 6 caracteres, deben coincidir)"
                                    )
                                    return@launch
                                }

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

                                try {
                                    // ⬇️ Si tu AuthApiService devuelve Users, cambia RegisterResponse por Users en la línea de abajo.
                                    val response: Response<RegisterResponse> = withContext(Dispatchers.IO) {
                                        RetrofitClient.authService.registerUsuario(request)
                                    }

                                    if (response.isSuccessful) {
                                        val body = response.body()
                                        val msg = when {
                                            body?.message?.isNotBlank() == true -> body.message!!
                                            // Si fuera Users, podrías usar body.nombre
                                            else -> "Registro exitoso. ¡Inicia sesión!"
                                        }
                                        onRegisterSuccess(msg)
                                    } else {
                                        val code = response.code()
                                        val error = try {
                                            response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                                        } catch (_: Exception) { null }
                                        snackbarHostState.showSnackbar(
                                            "Error ${code}: ${error ?: response.message()}"
                                        )
                                    }
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error de conexión: ${e.message}")
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
                            Text("Registrar", color = Color.White, fontSize = 18.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = onBack) {
                        Text("Volver al inicio de sesión", color = Color(0xFF00C853))
                    }
                }
            }
        }
    }
}
