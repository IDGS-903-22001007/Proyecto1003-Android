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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proye_1003.R
import com.example.proye_1003.models.Users
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authViewModel: AuthViewLogin= viewModel(),
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: (Users) -> Unit = {},
    initialMessage: String? = null,
    onMessageShown: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Mostrar mensaje inicial si viene de registro
    LaunchedEffect(initialMessage) {
        initialMessage?.let {
            snackbarHostState.showSnackbar(it)
            onMessageShown()
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                contentDescription = "Fondo farmacia",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )

            // Scrim oscuro
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            )

            // Caja del formulario
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
                        text = "Bienvenido a",
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
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
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

                    Button(
                        onClick = {
                            val validEmail = email.contains("@")
                            val validPassword = password.length >= 6

                            if (!validEmail) {
                                scope.launch { snackbarHostState.showSnackbar("Correo inválido") }
                            } else if (!validPassword) {
                                scope.launch { snackbarHostState.showSnackbar("Contraseña demasiado corta") }
                            } else {

                                authViewModel.login(email) { usuario ->
                                    onLoginSuccess(usuario) // Usuario correcto
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

                    TextButton(onClick = onNavigateToRegister) {
                        Text(
                            text = "¿No tienes cuenta? Crear cuenta",
                            color = Color(0xFF00C853)
                        )
                    }
                }
            }
        }
    }
}
