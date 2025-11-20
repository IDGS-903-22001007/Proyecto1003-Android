package com.example.proye_1003.Auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    // Estados de los campos
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Estados de error
    var errorNombre by remember { mutableStateOf(false) }
    var errorApellido by remember { mutableStateOf(false) }
    var errorTelefono by remember { mutableStateOf(false) }
    var errorEmail by remember { mutableStateOf(false) }
    var errorUsuario by remember { mutableStateOf(false) }
    var errorDireccion by remember { mutableStateOf(false) }
    var errorPassword by remember { mutableStateOf(false) }
    var errorConfirmPassword by remember { mutableStateOf(false) }

    val regex = Regex("^[A-Za-z0-9 ]*$") // Solo letras, números y espacios
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
            // Fondo
            Image(
                painter = painterResource(id = R.drawable.fondo_farmacia),
                contentDescription = "Fondo",
                modifier = Modifier.matchParentSize().zIndex(0f),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            )

            // Contenedor
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.Center)
                    .shadow(12.dp, RoundedCornerShape(20.dp))
                    .background(
                        color = Color.White.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(30.dp)
            ) {

                // 🔥🔥🔥 AQUI VA EL SCROLL 🔥🔥🔥
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {

                    Text(
                        "Crea tu cuenta",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // ======================================
                    //  Nombre
                    // ======================================
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { input ->
                            if (input.matches(regex)) {
                                nombre = input
                                errorNombre = false
                            }
                        },
                        label = { Text("Nombre") },
                        isError = errorNombre,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorNombre) Text("El nombre es obligatorio", color = Color.Red, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // ======================================
                    //  Apellido
                    // ======================================
                    OutlinedTextField(
                        value = apellido,
                        onValueChange = { input ->
                            if (input.matches(regex)) {
                                apellido = input
                                errorApellido = false
                            }
                        },
                        label = { Text("Apellido") },
                        isError = errorApellido,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorApellido) Text("El apellido es obligatorio", color = Color.Red, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // ======================================
                    // Teléfono (solo números)
                    // ======================================
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() } && input.length <= 10) {
                                telefono = input
                                errorTelefono = false
                            }
                        },
                        label = { Text("Teléfono") },
                        isError = errorTelefono,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorTelefono) Text("El teléfono es obligatorio", color = Color.Red, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // ======================================
                    //  Usuario
                    // ======================================
                    OutlinedTextField(
                        value = usuario,
                        onValueChange = { input ->
                            if (input.matches(regex)) {
                                usuario = input
                                errorUsuario = false
                            }
                        },
                        label = { Text("Usuario") },
                        isError = errorUsuario,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorUsuario) Text("El usuario es obligatorio", color = Color.Red, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // ======================================
                    //  Dirección
                    // ======================================
                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { input ->
                            if (input.matches(regex)) {
                                direccion = input
                                errorDireccion = false
                            }
                        },
                        label = { Text("Dirección") },
                        isError = errorDireccion,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorDireccion) Text("La dirección es obligatoria", color = Color.Red, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // ======================================
                    //  Email
                    // ======================================
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorEmail = false
                        },
                        label = { Text("Correo electrónico") },
                        isError = errorEmail,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorEmail) Text("Correo inválido", color = Color.Red, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // ======================================
                    //  Contraseña
                    // ======================================
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorPassword = false
                        },
                        label = { Text("Contraseña (mín. 6 caracteres)") },
                        isError = errorPassword,
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorPassword) Text("Contraseña demasiado corta", color = Color.Red, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // ======================================
                    //  Confirmar contraseña
                    // ======================================
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            errorConfirmPassword = false
                        },
                        label = { Text("Confirmar contraseña") },
                        isError = errorConfirmPassword,
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorConfirmPassword) Text("Las contraseñas no coinciden", color = Color.Red, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(20.dp))

                    // ======================================
                    //  Botón Registrar
                    // ======================================
                    Button(
                        onClick = {
                            // Activar errores visuales
                            errorNombre = nombre.isBlank()
                            errorApellido = apellido.isBlank()
                            errorTelefono = telefono.isBlank()
                            errorUsuario = usuario.isBlank()
                            errorDireccion = direccion.isBlank()
                            errorEmail = !isEmailValid(email)
                            errorPassword = password.length < 6
                            errorConfirmPassword = confirmPassword != password

                            if (errorNombre || errorApellido || errorTelefono || errorUsuario ||
                                errorDireccion || errorEmail || errorPassword || errorConfirmPassword
                            ) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Revisa los campos marcados en rojo.")
                                }
                                return@Button
                            }

                            // Si todo está correcto → enviar registro
                            scope.launch {
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
                                        val nombreUsuario = body?.nombre ?: "Usuario"
                                        onRegisterSuccess("¡Registro exitoso! Bienvenido, $nombreUsuario.")
                                    } else {
                                        snackbarHostState.showSnackbar("Error ${response.code()}: ${response.errorBody()?.string()}")
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

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = onBack) {
                        Text("Volver al inicio de sesión", color = Color(0xFF00C853))
                    }
                }
            }
        }
    }
}
