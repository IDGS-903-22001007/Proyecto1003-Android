package com.example.proye_1003.Auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proye_1003.models.SesionUsuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(nav: NavController) {
    val nombre = SesionUsuario.nombre ?: "Usuario"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmacia · Menú", fontWeight = FontWeight.SemiBold) }
            )
        },
        bottomBar = { BottomNavBar(navController = nav) } // ✅ Barra inferior visible en todas las pantallas
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡Bienvenido, $nombre! 👋",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Selecciona una opción en la barra inferior para continuar.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
