
import OcrScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import android.net.Uri


import com.example.proye_1003.Auth.RegisterScreen
import com.example.proye_1003.Auth.LoginScreen
import com.example.proye_1003.Auth.MenuScreen
import com.example.proye_1003.Auth.CitasScreen
import com.example.proye_1003.Auth.CitaCreateScreen

import com.example.proye_1003.models.SesionUsuario
import com.example.proye_1003.medicamentos.MedicamentosListScreen
import com.example.proye_1003.services.RetrofitClient


@Composable
fun AppNavigator(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "Login"
    ) {

        // 🔹 Login
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
                onLoginSuccess = {
                    navController.navigate("menu") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                initialMessage = message,
                onMessageShown = {
                    navController.currentBackStackEntry?.arguments?.remove("message")
                }
            )
        }

        // 🔹 Registro
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { message ->
                    val encoded = Uri.encode(message ?: "Registro exitoso")
                    navController.navigate("login?message=$encoded") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 🔹 Menú principal
        composable("menu") {
            MenuScreen(nav = navController)
        }

        // Citas (Lista)
        composable(route = "citas") {
            CitasScreen(
                navController = navController,
                onNuevaCita = { navController.navigate("citaCreate") }
            )
        }


        composable("ocr") {
            OcrScreen(
                navController = navController,
                medicamentoService = RetrofitClient.medicamentoService
            )
        }



        // 🔹 Crear cita
        composable("citaCreate") {
            CitaCreateScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }


        // Medicamentos (Lista)
        composable(route = "meds") {
            MedicamentosListScreen(navController = navController)
        }

    }
}
