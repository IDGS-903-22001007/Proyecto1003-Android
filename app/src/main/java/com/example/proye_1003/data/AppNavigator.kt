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

// -------- AUTH --------
import com.example.proye_1003.Auth.LoginScreen
import com.example.proye_1003.Auth.MenuScreen
import com.example.proye_1003.Auth.RegisterScreen

// -------- CITAS --------
import com.example.proye_1003.citas.ui.CitaCreateScreen
import com.example.proye_1003.citas.ui.CitasScreen
import com.example.proye_1003.citas.ui.CitaDetailScreen
import com.example.proye_1003.citas.viewmodel.CitaViewModel

// -------- MEDICAMENTOS --------
import com.example.proye_1003.medicamentos.MedicamentoDetailScreen
import com.example.proye_1003.medicamentos.MedicamentosListScreen

// -------- RECORDATORIOS --------
import com.example.proye_1003.recordatorios.RecordatoriosViewModel
import com.example.proye_1003.recordatorios.RecordatoriosViewModelFactory
import com.example.proye_1003.recordatorios.data.AppDatabase
import com.example.proye_1003.recordatorios.domain.RecordatorioRepository
import com.example.proye_1003.recordatorios.ui.RecordatorioFormScreen
import com.example.proye_1003.recordatorios.ui.RecordatoriosListScreen

// -------- OCR --------
import com.example.proye_1003.ia.ui.OcrScreen

// -------- IA (Chat Gemini) --------
import com.example.proye_1003.ia.ui.ChatScreen
import com.example.proye_1003.ia.viewmodel.ChatViewModel
import com.example.proye_1003.ia.viewmodel.ChatViewModelFactory
import com.example.proye_1003.ia.services.GeminiService
import com.example.proye_1003.ia.services.OCRService
import com.example.proye_1003.ia.viewmodel.OcrViewModel
import com.example.proye_1003.ia.viewmodel.OcrViewModelFactory
import com.example.proye_1003.services.RetrofitClient


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

        composable(
            route = "citaDetalle/{idCita}",
            arguments = listOf(
                navArgument("idCita") { type = NavType.IntType }
            )
        ) { entry ->
            val id = entry.arguments?.getInt("idCita") ?: 0
            val viewModel: CitaViewModel = viewModel()

            CitaDetailScreen(
                idCita = id,
                navController = navController,
                viewModel = viewModel
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
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            MedicamentoDetailScreen(nav = navController, id = id)
        }

        // ---------------------------------------------------------
        // RECORDATORIOS
        // ---------------------------------------------------------
        composable("recordatorios") { backStackEntry ->

            val context = LocalContext.current
            val dao = AppDatabase.getInstance(context).recordatorioDao()
            val repo = RecordatorioRepository(dao, context)

            val vm: RecordatoriosViewModel = viewModel(
                backStackEntry,
                factory = RecordatoriosViewModelFactory(repo, 0)
            )

            RecordatoriosListScreen(
                recordatorios = vm.lista,
                onAdd = { navController.navigate("recordatorioForm/0") },
                onEdit = { r -> navController.navigate("recordatorioForm/${r.id}") },
                onDelete = { r -> vm.eliminar(r) },
                navController = navController
            )
        }

        composable(
            route = "recordatorioForm/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->

            val context = LocalContext.current
            val dao = AppDatabase.getInstance(context).recordatorioDao()
            val repo = RecordatorioRepository(dao, context)

            val vm: RecordatoriosViewModel = viewModel(
                backStackEntry,
                factory = RecordatoriosViewModelFactory(repo, 0)
            )

            val id = backStackEntry.arguments?.getLong("id") ?: 0L

            RecordatorioFormScreen(
                idRecordatorio = id,
                navController = navController,
                viewModel = vm
            )
        }

        // ---------------------------------------------------------
        // OCR
        // ---------------------------------------------------------
        composable("ocr") { backStackEntry ->
            val vm: OcrViewModel = viewModel(
                backStackEntry,
                factory = OcrViewModelFactory(
                    ocrService = OCRService(),
                    medicamentoService = RetrofitClient.medicamentoService
                )
            )

            OcrScreen(
                viewModel = vm,
                navController = navController
            )
        }


        // ---------------------------------------------------------
        // IA (Gemini Chat)
        // ---------------------------------------------------------
        composable("ia") { backStackEntry ->

            val service = GeminiService()

            val vm: ChatViewModel = viewModel(
                backStackEntry,
                factory = ChatViewModelFactory(service)
            )

            ChatScreen(
                onSendToIA = { texto ->
                    vm.enviarMensaje(texto)
                    service.enviarTextoAI(texto)
                }
            )
        }
    }
}
