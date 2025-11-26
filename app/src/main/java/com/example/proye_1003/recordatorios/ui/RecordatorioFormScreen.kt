package com.example.proye_1003.recordatorios.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proye_1003.recordatorios.RecordatoriosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordatorioFormScreen(
    idRecordatorio: Long,
    navController: NavController,
    viewModel: RecordatoriosViewModel
) {
    val inicial = viewModel.lista.find { it.id == idRecordatorio }

    var nombre by remember { mutableStateOf(inicial?.nombreMedicamento ?: "") }
    var dosis by remember { mutableStateOf(inicial?.dosis ?: "") }
    var frecuencia by remember { mutableStateOf((inicial?.frecuenciaHoras ?: 8).toString()) }
    var duracion by remember { mutableStateOf((inicial?.duracionDias ?: 1).toString()) }
    val fechaInicio = inicial?.fechaInicioMillis ?: System.currentTimeMillis()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (inicial == null) "Nuevo recordatorio"
                        else "Editar recordatorio"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { pad ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del medicamento") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = dosis,
                onValueChange = { dosis = it },
                label = { Text("Dosis") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = frecuencia,
                onValueChange = { frecuencia = it },
                label = { Text("Frecuencia (cada X horas)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = duracion,
                onValueChange = { duracion = it },
                label = { Text("Duración (días)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val f = frecuencia.toIntOrNull() ?: 8
                    val d = duracion.toIntOrNull() ?: 1

                    if (inicial == null) {
                        viewModel.agregar(
                            nombre,
                            f,
                            fechaInicio,
                            d,
                            dosis
                        )
                    } else {
                        viewModel.actualizar(
                            inicial.copy(
                                nombreMedicamento = nombre,
                                dosis = dosis,
                                frecuenciaHoras = f,
                                duracionDias = d
                            )
                        )
                    }

                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (inicial == null) "Guardar" else "Actualizar")
            }
        }
    }
}
