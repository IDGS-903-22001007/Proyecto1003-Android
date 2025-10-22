package com.example.proye_1003.data

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proye_1003.Auth.LoginScreen
import com.example.proye_1003.Auth.RegisterScreen


@Composable
fun AppNavigator(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { message ->
                    navController.navigate("login?message=$message") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

    }
}