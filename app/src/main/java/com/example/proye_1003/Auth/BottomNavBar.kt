package com.example.proye_1003.Auth

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar {
        // 🏠 Inicio
        NavigationBarItem(
            icon = { Text("🏠") },
            label = { Text("Inicio") },
            selected = false,
            onClick = {
                navController.navigate("menu") {
                    popUpTo("menu") { inclusive = true }
                }
            }
        )

        // 📅 Citas
        NavigationBarItem(
            icon = { Text("📅") },
            label = { Text("Citas") },
            selected = false,
            onClick = { navController.navigate("citas") }
        )

        // 💊 Medicamentos
        NavigationBarItem(
            icon = { Text("💊") },
            label = { Text("Medicamentos") },
            selected = false,
            onClick = { navController.navigate("meds") }
        )

        // 🚪 Cerrar sesión
        NavigationBarItem(
            icon = { Text("🚪") },
            label = { Text("Salir") },
            selected = false,
            onClick = {
                navController.navigate("login") {
                    popUpTo("menu") { inclusive = true }
                }
            }
        )
    }
}
