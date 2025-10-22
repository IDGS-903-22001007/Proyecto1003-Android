// Kotlin
package com.example.proye_1003

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.proye_1003.LoginRequest
import com.example.proye_1003.LoginResponse
import com.example.proye_1003.RetrofitClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var currentScreen by remember { mutableStateOf("login") }
                    var pendingMessage by remember { mutableStateOf<String?>(null) }

                    when (currentScreen) {
                        "login" -> LoginScreen(
                            onNavigateToRegister = { currentScreen = "register" },
                            initialMessage = pendingMessage,
                            onMessageShown = { pendingMessage = null }
                        )
                        "register" -> RegisterScreen(
                            onRegisterSuccess = { msg ->
                                pendingMessage = msg
                                currentScreen = "login"
                            },
                            onBack = { currentScreen = "login" }
                        )
                    }
                }
            }
        }
    }
}

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
                                val emailValue = email // El valor del campo de email
                                val passwordValue = password // El valor del campo de contraseña

                                if (emailValue.isBlank() || passwordValue.isBlank()) {
                                    snackbarHostState.showSnackbar("Introduce correo y contraseña")
                                    return@launch
                                }

                                try {
                                    // *** APLICACIÓN DE LA CORRECCIÓN DE NOMBRES DE CAMPOS AQUÍ ***
                                    // Usamos 'user' y 'contrasena' para coincidir con LoginRequest
                                    val request = LoginRequest(user = emailValue, contrasena = passwordValue)

                                    // Realizar la llamada en el ámbito de IO (Input/Output)
                                    val response: Response<LoginResponse> = withContext(Dispatchers.IO) {
                                        // *** APLICACIÓN DE LA CORRECCIÓN DEL NOMBRE DEL CLIENTE AQUÍ ***
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
                                        // Opcionalmente puedes intentar leer response.errorBody()?.string() si tu API lo devuelve
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
// RegisterScreen: Barra superior (TopBar) eliminada
@Composable
fun RegisterScreen(onRegisterSuccess: (String) -> Unit, onBack: () -> Unit) {
    // Necesitas campos adicionales para nombre, apellido, teléfono, usuario, dirección, etc.
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    // Los campos existentes de email, password y confirmPassword
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
            // ... (Fondo de imagen y Scrim)

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

                    // -----------------------------------------------------
                    // Campos de Registro (Añade los que faltan: nombre, tel, etc.)
                    // -----------------------------------------------------

                    // Nombre
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))

                    // Apellido
                    OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))

                    // Teléfono
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it.filter { ch -> ch.isDigit() } }, // permite solo dígitos
                        label = { Text("Teléfono") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // teclado numérico
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Usuario (el campo de LoginRequest)
                    OutlinedTextField(value = usuario, onValueChange = { usuario = it }, label = { Text("Nombre de Usuario") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))

                    // Dirección
                    OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))

                    // Correo (campo que ya tenías)
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))

                    // Contraseña (campo que ya tenías)
                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))

                    // Confirmar Contraseña (campo que ya tenías)
                    OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirmar contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(18.dp))

                    // -----------------------------------------------------
                    // Botón y Lógica de Registro con API
                    // -----------------------------------------------------
                    Button(
                        onClick = {
                            val passwordsMatch = password == confirmPassword

                            scope.launch {
                                if (!passwordsMatch) {
                                    snackbarHostState.showSnackbar("Las contraseñas no coinciden")
                                    return@launch
                                }
                                // Puedes agregar más validaciones aquí (longitud, formato, campos vacíos)

                                try {
                                    val request = RegisterRequest(
                                        nombre = nombre,
                                        apellido = apellido,
                                        telefono = telefono,
                                        correo = email, // Usamos el campo email del formulario
                                        usuario = usuario,
                                        direccion = direccion,
                                        contrasena = password,
                                        rol = "user"
                                    )

                                    // Realizar la llamada en el hilo de IO
                                    val response: Response<RegisterResponse> = withContext(Dispatchers.IO) {
                                        RetrofitClient.authService.registerUser(request)
                                    }

                                    if (response.isSuccessful) {
                                        val msg = response.body()?.message ?: "Registro exitoso. Ya puedes iniciar sesión."
                                        // Navegar al login y mostrar mensaje de éxito
                                        onRegisterSuccess(msg)
                                    } else {
                                        // Fallo de la API (ej. correo ya registrado, validación del servidor)
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
                        // ... (Contenido del botón Registrar)
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