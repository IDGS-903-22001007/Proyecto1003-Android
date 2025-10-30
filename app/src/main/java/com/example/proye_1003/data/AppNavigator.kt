package com.example.proye_1003.data

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Importa tus pantallas
import com.example.proye_1003.Auth.LoginScreen
import com.example.proye_1003.Auth.RegisterScreen
import com.example.proye_1003.medicamentos.MenuScreen
import com.example.proye_1003.medicamentos.MedicamentosListScreen
import com.example.proye_1003.medicamentos.MedicamentoDetailScreen
import com.example.proye_1003.Auth.CitaCreateScreen

// ===================================================
// 🚀 Helper de navegación segura desde Login → Menú
// ===================================================
private fun NavHostController.navigateToMenuFromLogin() {
    val route = currentBackStackEntry?.destination?.route
    try {
        navigate("menu") {
            when {
                route?.startsWith("login?") == true ->
                    popUpTo("login?message={message}") { inclusive = true }
                route == "login" ->
                    popUpTo("login") { inclusive = true }
                else ->
                    popUpTo(graph.startDestinationId) { inclusive = true }
            }
            launchSingleTop = true
        }
    } catch (_: IllegalArgumentException) {
        navigate("menu") { launchSingleTop = true }
    }
}

// ===================================================
// 🧭 Navegador principal de la app
// ===================================================
@Composable
fun AppNavigator(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // -------- LOGIN (sin mensaje inicial) --------
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                initialMessage = null,
                onMessageShown = { /* sin uso aquí */ },
                onLoginSuccess = { navController.navigateToMenuFromLogin() }
            )
        }

        // -------- LOGIN (cuando regresa del registro) --------
        composable(
            route = "login?message={message}",
            arguments = listOf(
                navArgument("message") {
                    nullable = true
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val message = backStackEntry.arguments?.getString("message")
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                initialMessage = message,
                onMessageShown = {
                    navController.currentBackStackEntry?.arguments?.remove("message")
                },
                onLoginSuccess = { navController.navigateToMenuFromLogin() }
            )
        }

        // -------- REGISTER --------
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { message ->
                    val encoded = Uri.encode(message ?: "Registro exitoso")
                    navController.navigate("login?message=$encoded") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // -------- MENÚ PRINCIPAL --------
        composable("menu") {
            MenuScreen(navController)
        }

        // -------- CITAS --------
        composable("citas") {
            CitaCreateScreen()
        }

        // -------- LISTA DE MEDICAMENTOS --------
        composable("meds") {
            MedicamentosListScreen(navController)
        }

        // -------- DETALLE DE MEDICAMENTO --------
        composable(
            route = "meds/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            if (id != null) {
                MedicamentoDetailScreen(navController, id)
            } else {
                navController.popBackStack()
            }
        }
    }
}
