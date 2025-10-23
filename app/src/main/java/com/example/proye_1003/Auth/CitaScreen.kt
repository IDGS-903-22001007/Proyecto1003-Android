package com.example.proye_1003.Auth


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proye_1003.Auth.CitaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasScreen(
    idPaciente: Int,
    onBack: () -> Unit
) {
    val viewModel: CitaViewModel = viewModel()
    val citas by viewModel.citas.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarCitas(idPaciente)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Citas del Paciente #$idPaciente") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(citas) { cita ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("📅 ${cita.fechaCita}")
                        Text("Tipo: ${cita.tipoConsulta}")
                        Text("Notas: ${cita.notas ?: "Sin notas"}")
                        Text("Estatus: ${cita.estatus}")
                    }
                }
            }
        }
    }
}
