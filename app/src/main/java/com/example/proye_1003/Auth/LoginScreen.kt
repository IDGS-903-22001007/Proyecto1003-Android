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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proye_1003.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    initialMessage: String?,
    onMessageShown: () -> Unit,
    onLoginSuccess: () -> Unit,
    vm: AuthViewLogin = viewModel()
) {
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var loggingIn by remember { mutableStateOf(false) }

    val loginState by vm.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()               // <- para usar launch en onClick

    // Mensaje que llega desde Register
    LaunchedEffect(initialMessage) {
        if (!initialMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(initialMessage)
            onMessageShown()
        }
    }

    // Muestra errores del VM
    LaunchedEffect(loginState) {
        val msg = loginState
        if (!msg.isNullOrBlank() && (msg.startsWith("Error") || msg.contains("incorrecta", true))) {
            snackbarHostState.showSnackbar(msg)
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
                modifier = Modifier.matchParentSize().zIndex(0f),
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
                    .padding(26.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Iniciar sesión",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = user,
                        onValueChange = { user = it },
                        label = { Text("Usuario o correo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = pass,
                        onValueChange = { pass = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (loggingIn) return@Button
                            if (user.isBlank() || pass.isBlank()) {
                                // ✅ Usar scope.launch en lugar de LaunchedEffect dentro del onClick
                                scope.launch {
                                    snackbarHostState.showSnackbar("Ingresa usuario y contraseña")
                                }
                                return@Button
                            }

                            loggingIn = true
                            vm.login(
                                user = user.trim(),
                                password = pass.trim()
                            ) {
                                loggingIn = false
                                onLoginSuccess()
                            }
                        },
                        enabled = !loggingIn,
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
                                if (loggingIn) "Ingresando..." else "Ingresar",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    if (loginState == "loading") {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                    }

                    Spacer(Modifier.height(8.dp))

                    TextButton(onClick = onNavigateToRegister) {
                        Text("Crear cuenta", color = Color(0xFF00C853))
                    }
                }
            }
        }
    }
}
