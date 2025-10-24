package com.example.proye_1003.Views

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import android.net.Uri

@Composable
fun AppNavigator(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // Pantalla de Login
        composable(
            route = "login?message={message}",
            arguments = listOf(navArgument("message") {
                nullable = true
                type = NavType.StringType
            })
        ) { backStackEntry ->

            val message = backStackEntry.arguments?.getString("message")

            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                initialMessage = message,
                onMessageShown = { navController.currentBackStackEntry?.arguments?.remove("message") },
                onLoginSuccess = {
                    // Navegar directamente a OCRScreen y limpiar el backstack de login
                    navController.navigate("ocr") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Registro
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { message ->
                    val encoded = Uri.encode(message ?: "Registro exitoso")
                    navController.navigate("login?message=$encoded") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de OCR
        composable("ocr") {
            OcrScreen()
        }
    }
}
