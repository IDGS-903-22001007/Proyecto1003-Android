package com.example.proye_1003.Auth

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
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

        // 🔔 Recordatorios
        NavigationBarItem(
            icon = { Text("🔔") },
            label = { Text("Recordatorios") },
            selected = false,
            onClick = { navController.navigate("recordatorios") }
        )

        // 🚪 Cerrar sesión
        NavigationBarItem(
            icon = { Text("🚪") },
            label = { Text("Salir") },
            selected = false,
            onClick = {
                // 1) Limpiar solo datos en memoria
                SesionUsuario.limpiarSesion()

                // 2) Volver a login limpiando el backstack
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
        )
    }
}
