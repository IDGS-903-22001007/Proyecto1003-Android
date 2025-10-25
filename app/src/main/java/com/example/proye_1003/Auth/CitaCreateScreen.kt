package com.example.proye_1003.Auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proye_1003.models.Cita

@Composable
fun CitaCreateScreen(
    viewModel: CitaCreateViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit = {}
) {
    var idPaciente by remember { mutableStateOf("") }
    var fechaCita by remember { mutableStateOf("") }
    var tipoConsulta by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Registrar Nueva Cita", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = idPaciente,
            onValueChange = { idPaciente = it },
            label = { Text("ID del Paciente") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fechaCita,
            onValueChange = { fechaCita = it },
            label = { Text("Fecha de la cita (YYYY-MM-DDTHH:mm:ss)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tipoConsulta,
            onValueChange = { tipoConsulta = it },
            label = { Text("Tipo de consulta") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notas,
            onValueChange = { notas = it },
            label = { Text("Notas (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (idPaciente.isNotBlank() && fechaCita.isNotBlank() && tipoConsulta.isNotBlank()) {
                    viewModel.registrarCita(
                        Cita(
                            idPaciente = idPaciente.toInt(),
                            fechaCita = fechaCita,
                            tipoConsulta = tipoConsulta,
                            notas = notas
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Cita")
        }

        estado?.let {
            Text(it, color = if (it.contains("Error")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
