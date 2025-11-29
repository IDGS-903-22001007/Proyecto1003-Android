package com.example.proye_1003.citas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proye_1003.Auth.BottomNavBar
import com.example.proye_1003.citas.viewmodel.CitaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaDetailScreen(
    idCita: Int,
    navController: NavController,
    viewModel: CitaViewModel
) {
    val cita by viewModel.citaDetalle.collectAsState()

    LaunchedEffect(idCita) {
        viewModel.cargarCitaPorId(idCita)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Cita") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←")
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { pad ->

        Box(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            if (cita == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                return@Box
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    "Información Registrada por el Paciente",
                    style = MaterialTheme.typography.titleMedium
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF1F8E9)
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {

                        DetailItem("📅 Fecha de la cita", cita!!.fechaHora)
                        DetailItem("🩺 Tipo de consulta", cita!!.tipoConsulta)
                        DetailItem("📝 Notas del paciente", cita!!.notas ?: "Sin notas registradas")
                    }
                }

                Text(
                    "Información del Médico",
                    style = MaterialTheme.typography.titleMedium
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {

                        DetailItem("🩻 Diagnóstico", cita!!.diagnostico ?: "Sin diagnóstico registrado")
                        DetailItem("🔍 Observaciones", cita!!.observaciones ?: "Sin observaciones")
                        DetailItem("💊 Medicamentos", cita!!.medicamentos ?: "Sin medicamentos recetados")
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(titulo: String, valor: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {

        Text(
            text = titulo,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF2E7D32)
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge
        )

        Divider(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        )
    }
}
