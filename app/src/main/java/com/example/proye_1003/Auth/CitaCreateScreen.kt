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
import com.example.proye_1003.models.SesionUsuario
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaCreateScreen(
    viewModel: CitaCreateViewModel = viewModel(),
    onBack: () -> Unit = {},
    navController: androidx.navigation.NavController
) {
    val idPaciente = SesionUsuario.idUsuario ?: 0

    // Campos de formulario
    var dia by remember { mutableStateOf("") }
    var mes by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var minuto by remember { mutableStateOf("") }
    var tipoConsulta by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🩺 Registrar nueva cita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("🔙")
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 📅 Fecha dividida en 3 campos (DD/MM/YYYY)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dia,
                    onValueChange = { if (it.length <= 2 && it.all(Char::isDigit)) dia = it },
                    label = { Text("DD") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = mes,
                    onValueChange = { if (it.length <= 2 && it.all(Char::isDigit)) mes = it },
                    label = { Text("MM") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = anio,
                    onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) anio = it },
                    label = { Text("YYYY") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(2f)
                )
            }

            // ⏰ Hora dividida en HH:MM
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = hora,
                    onValueChange = { if (it.length <= 2 && it.all(Char::isDigit)) hora = it },
                    label = { Text("HH") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = minuto,
                    onValueChange = { if (it.length <= 2 && it.all(Char::isDigit)) minuto = it },
                    label = { Text("MM") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            // Tipo de consulta
            OutlinedTextField(
                value = tipoConsulta,
                onValueChange = { tipoConsulta = it },
                label = { Text("Tipo de consulta") },
                modifier = Modifier.fillMaxWidth()
            )

            // Notas opcionales
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Botón de guardado
            Button(
                onClick = {
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

                        // Formato ISO para enviar al backend (.NET)
                        val apiDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                            .format(fechaHora)

                        val nuevaCita = Cita(
                            idPaciente = idPaciente,
                            fechaHora = apiDate,
                            tipoConsulta = tipoConsulta,
                            notas = notas.ifBlank { null },
                            estatus = "A",
                            duracionMin = 30
                        )

                        viewModel.registrarCita(nuevaCita)

                    } else {
                        Toast.makeText(context, "Completa todos los campos requeridos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cita 💾")
            }

            // Mostrar estado de la API
            estado?.let {
                Text(
                    text = it,
                    color = if (it.contains("Error", true)) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
