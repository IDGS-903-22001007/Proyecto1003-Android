package com.example.proye_1003.Auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proye_1003.models.Cita
import com.example.proye_1003.models.SesionUsuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasScreen(
    navController: NavController,
    onNuevaCita: () -> Unit
) {
    val viewModel: CitaViewModel = viewModel()
    val citas by viewModel.citas.collectAsState(initial = emptyList())

    // 🔹 Obtener el id del usuario logueado desde la sesión global
    val idPaciente = SesionUsuario.idUsuario ?: 0

    LaunchedEffect(Unit) {
        if (idPaciente != 0) {
            viewModel.cargarCitas(idPaciente)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Citas de ${SesionUsuario.nombre ?: "Usuario"}") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevaCita,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Cita")
            }
        },
        bottomBar = { BottomNavBar(navController = navController) } // 👈 Barra inferior
    ) { padding ->
        if (citas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay citas registradas aún")
            }
        } else {
            LazyColumn(contentPadding = padding) {
                items(items = citas, key = { it.idCita ?: it.hashCode() }) { cita: Cita ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("📅 Fecha: ${cita.fechaCita}")
                            Text("Tipo: ${cita.tipoConsulta}")
                            Text("Notas: ${cita.notas ?: "Sin notas"}")
                            Text("Estatus: ${cita.estatus ?: "A"}")
                        }
                    }
                }
            }
        }
    }
}
