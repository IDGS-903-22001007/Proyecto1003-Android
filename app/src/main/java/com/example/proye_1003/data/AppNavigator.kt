package com.example.proye_1003.data

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proye_1003.Auth.CitaCreateScreen
import com.example.proye_1003.Auth.CitasScreen
import com.example.proye_1003.Auth.LoginScreen
import com.example.proye_1003.Auth.MenuScreen
import com.example.proye_1003.Auth.RegisterScreen
import com.example.proye_1003.medicamentos.MedicamentoDetailScreen
import com.example.proye_1003.medicamentos.MedicamentosListScreen
import com.example.proye_1003.recordatorios.RecordatoriosViewModel
import com.example.proye_1003.recordatorios.RecordatoriosViewModelFactory
import com.example.proye_1003.recordatorios.data.AppDatabase
import com.example.proye_1003.recordatorios.domain.RecordatorioRepository
import com.example.proye_1003.recordatorios.ui.RecordatorioFormScreen
import com.example.proye_1003.recordatorios.ui.RecordatoriosListScreen

@Composable
fun AppNavigator(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // ---------------------------------------------------------
        // LOGIN
        // ---------------------------------------------------------
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
                onLoginSuccess = {
                    navController.navigate("menu") {
                        // Limpiamos stack hasta login y entramos “frescos”
                        popUpTo("login") { inclusive = true }
                    }
                },
                initialMessage = message,
                onMessageShown = {
                    navController.currentBackStackEntry
                        ?.arguments
                        ?.remove("message")
                }
            )
        }

        // ---------------------------------------------------------
        // REGISTRO
        // ---------------------------------------------------------
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

        // ---------------------------------------------------------
        // MENÚ PRINCIPAL
        // ---------------------------------------------------------
        composable("menu") {
            MenuScreen(nav = navController)
        }

        // ---------------------------------------------------------
        // CITAS
        // ---------------------------------------------------------
        composable("citas") {
            CitasScreen(
                navController = navController,
                onNuevaCita = { navController.navigate("citaCreate") }
            )
        }

        composable("citaCreate") {
            CitaCreateScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        // ---------------------------------------------------------
        // MEDICAMENTOS
        // ---------------------------------------------------------
        composable("meds") {
            MedicamentosListScreen(navController)
        }

        composable(
            route = "meds/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            MedicamentoDetailScreen(
                nav = navController,
                id = id
            )
        }

        // ---------------------------------------------------------
        // RECORDATORIOS (LISTA)
        // ---------------------------------------------------------
        composable("recordatorios") { backStackEntry ->

            val context = LocalContext.current

            // ⚠️ Ya NO usamos el id del Login: valor fijo 0
            val idUsuarioFijo = 0

            val dao = AppDatabase.getInstance(context).recordatorioDao()
            val repo = RecordatorioRepository(dao, context)

            val viewModel: RecordatoriosViewModel =
                viewModel(
                    backStackEntry,
                    factory = RecordatoriosViewModelFactory(repo, idUsuarioFijo)
                )

            RecordatoriosListScreen(
                recordatorios = viewModel.lista,
                onAdd = { navController.navigate("recordatorioForm/0") },
                onEdit = { r -> navController.navigate("recordatorioForm/${r.id}") },
                onDelete = { r -> viewModel.eliminar(r) },
                navController = navController
            )
        }

        // ---------------------------------------------------------
        // RECORDATORIOS (FORMULARIO)
        // ---------------------------------------------------------
        composable(
            route = "recordatorioForm/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.LongType }
            )
        ) { backStackEntry ->

            val context = LocalContext.current
            val idUsuarioFijo = 0

            val dao = AppDatabase.getInstance(context).recordatorioDao()
            val repo = RecordatorioRepository(dao, context)

            val viewModel: RecordatoriosViewModel =
                viewModel(
                    backStackEntry,
                    factory = RecordatoriosViewModelFactory(repo, idUsuarioFijo)
                )

            val id = backStackEntry.arguments?.getLong("id") ?: 0L

            RecordatorioFormScreen(
                idRecordatorio = id,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
