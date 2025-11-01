package com.example.proye_1003.data

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import android.net.Uri

import com.example.proye_1003.Auth.RegisterScreen
import com.example.proye_1003.Auth.LoginScreen

@Composable
fun AppNavigator(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Login con mensaje opcional
        composable(
            route = "login?message={message}",
            arguments = listOf(navArgument("message") {
                nullable = true
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val message = backStackEntry.arguments?.getString("message")

            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    navController.navigate("testScreen")  // Navegación a tu TestScreen local
                },
                initialMessage = message,
                onMessageShown = {
                    navController.currentBackStackEntry?.arguments?.remove("message")
                }
            )
        }

        // Registro
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

    }
}
