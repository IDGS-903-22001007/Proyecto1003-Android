package com.example.proye_1003.Auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proye_1003.models.Cita
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CitaCreateScreen(
    viewModel: CitaCreateViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val idPaciente = 1
    var dia by remember { mutableStateOf("") }
    var mes by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var minuto by remember { mutableStateOf("") }
    var tipoConsulta by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Registrar Nueva Cita", style = MaterialTheme.typography.titleLarge)

        // Fecha dividida en tres campos
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = dia,
                onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) dia = it },
                label = { Text("DD") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = mes,
                onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) mes = it },
                label = { Text("MM") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = anio,
                onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) anio = it },
                label = { Text("YYYY") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(2f)
            )
        }

        // Hora dividida en HH y MM
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = hora,
                onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) hora = it },
                label = { Text("HH") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = minuto,
                onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) minuto = it },
                label = { Text("MM") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

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
                // Validar campos requeridos
                if (
                    dia.length == 2 && mes.length == 2 && anio.length == 4 &&
                    hora.length in 1..2 && minuto.length == 2 &&
                    tipoConsulta.isNotBlank()
                ) {
                    val fechaHoraStr = "$dia/$mes/$anio $hora:$minuto"
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val fechaHora = runCatching { sdf.parse(fechaHoraStr) }.getOrNull()

                    if (fechaHora == null) {
                        Toast.makeText(context, "Fecha u hora inválida", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (fechaHora.before(Date())) {
                        Toast.makeText(context, "No se puede seleccionar una fecha pasada", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val horaInt = hora.toIntOrNull() ?: 0
                    if (horaInt !in 8..18) {
                        Toast.makeText(context, "La hora debe estar entre 08:00 y 18:00", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Formato para la API / almacenamiento
                    val apiDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(fechaHora)

                    viewModel.registrarCitaLocal(
                        context = context,
                        cita = Cita(
                            idPaciente = idPaciente,
                            fechaCita = apiDate,
                            tipoConsulta = tipoConsulta,
                            notas = notas
                        )
                    )
                } else {
                    Toast.makeText(context, "Completa todos los campos requeridos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Cita")
        }

        estado?.let {
            Text(
                it,
                color = if (it.contains("Error")) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
            )
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
