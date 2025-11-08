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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import retrofit2.Response
import com.example.proye_1003.models.RegisterRequest
import com.example.proye_1003.models.Users
import com.example.proye_1003.services.RetrofitClient

@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onBack: () -> Unit
) {
    // Campos del formulario
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

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun areFieldsValid(): Boolean {
        return nombre.isNotBlank() && apellido.isNotBlank() && telefono.isNotBlank() &&
                usuario.isNotBlank() && direccion.isNotBlank() &&
                password.length >= 6 && password == confirmPassword
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Fondo con imagen
            Image(
                painter = painterResource(id = R.drawable.fondo_farmacia),
                contentDescription = "Fondo farmacia",
                modifier = Modifier.matchParentSize().zIndex(0f),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.45f)))

            // Contenedor principal
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
                    Text(
                        "Crea tu cuenta",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // Campos de texto
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { input ->
                            // Acepta solo dígitos y máximo 10 caracteres
                            if (input.all { it.isDigit() } && input.length <= 10) {
                                telefono = input
                            }
                        },
                        label = { Text("Teléfono") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(value = usuario, onValueChange = { usuario = it }, label = { Text("Usuario") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirmar contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(18.dp))

                    // Botón de registro
                    Button(
                        onClick = {
                            scope.launch {
                                if (!isEmailValid(email)) {
                                    snackbarHostState.showSnackbar("El formato del correo no es válido.")
                                    return@launch
                                }
                                if (!areFieldsValid()) {
                                    snackbarHostState.showSnackbar("Revisa todos los campos y contraseñas (mín. 6 caracteres, deben coincidir).")
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

                                    val response: Response<Users> = withContext(Dispatchers.IO) {
                                        RetrofitClient.authService.registerUsuario(request)
                                    }

                                    if (response.isSuccessful) {
                                        val body = response.body()
                                        if (body != null) {
                                            val nombreUsuario = body.nombre ?: "Usuario"
                                            onRegisterSuccess("¡Registro exitoso! Bienvenido, $nombreUsuario.")
                                        } else {
                                            onRegisterSuccess("Registro exitoso. ¡Inicia sesión!")
                                        }
                                    } else {
                                        val errorBody = response.errorBody()?.string() ?: "Error de servidor"
                                        snackbarHostState.showSnackbar("Error ${response.code()}: $errorBody")
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
                        Text("Volver al inicio de sesión", color = Color(0xFF00C853))
                    }
                }
            }
        }
    }
}
