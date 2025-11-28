package com.example.proye_1003.Auth

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController


@Composable
fun BottomNavBar(navController: NavController) {

    NavigationBar {

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

        NavigationBarItem(
            icon = { Text("📅") },
            label = { Text("Citas") },
            selected = false,
            onClick = { navController.navigate("citas") }
        )

        NavigationBarItem(
            icon = { Text("💊") },
            label = { Text("Medicamentos") },
            selected = false,
            onClick = { navController.navigate("meds") }
        )

        NavigationBarItem(
            icon = { Text("🔔") },
            label = { Text("Recordatorios") },
            selected = false,
            onClick = { navController.navigate("recordatorios") }
        )

        NavigationBarItem(
            icon = { Text("🚪") },
            label = { Text("Salir") },
            selected = false,
            onClick = {

                SesionUsuario.limpiarSesion()

                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
        )
    }
}
