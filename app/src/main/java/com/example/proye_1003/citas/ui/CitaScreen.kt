package com.example.proye_1003.citas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proye_1003.Auth.BottomNavBar
import com.example.proye_1003.citas.viewmodel.CitaViewModel
import com.example.proye_1003.models.Cita

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasScreen(
    navController: NavController,
    onNuevaCita: () -> Unit
) {
    val viewModel: CitaViewModel = viewModel()
    val citas by viewModel.citas.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.cargarCitas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Citas de ${SesionUsuario.nombre ?: "Usuario"}") }
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
        bottomBar = { BottomNavBar(navController = navController) }
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = padding
            ) {

                items(citas, key = { it.idCita ?: it.hashCode() }) { cita ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .shadow(4.dp, shape = RoundedCornerShape(16.dp))
                            .clickable {
                                if (cita.estatus == "T") {
                                    navController.navigate("citaDetalle/${cita.idCita}")
                                }
                            },
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {

                            // 🟦 Fecha destacada
                            Text(
                                text = "📅 ${cita.fechaHora}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.height(8.dp))

                            Text("Tipo de consulta: ${cita.tipoConsulta}")
                            Text("Notas: ${cita.notas ?: "Sin notas"}")

                            Spacer(Modifier.height(8.dp))

                            // 🏷️ Chip de estatus
                            val colorStatus = when (cita.estatus) {
                                "T" -> MaterialTheme.colorScheme.secondary
                                "C" -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.primary
                            }

                            AssistChip(
                                onClick = { },
                                label = {
                                    Text(
                                        when (cita.estatus) {
                                            "T" -> "Terminada"
                                            "C" -> "Cancelada"
                                            else -> "Activa"
                                        }
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    labelColor = colorStatus,
                                    leadingIconContentColor = colorStatus
                                )
                            )

                            Spacer(Modifier.height(12.dp))

                            // 🔎 Botón “Ver resultado”
                            if (cita.estatus == "T") {
                                Button(
                                    onClick = {
                                        navController.navigate("citaDetalle/${cita.idCita}")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Ver resultado")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
