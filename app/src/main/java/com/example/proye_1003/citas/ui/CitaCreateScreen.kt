package com.example.proye_1003.citas.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proye_1003.Auth.BottomNavBar
import com.example.proye_1003.citas.viewmodel.CitaCreateViewModel
import com.example.proye_1003.citas.viewmodel.CitaHorariosViewModel
import com.example.proye_1003.models.Cita
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaCreateScreen(
    viewModel: CitaCreateViewModel = viewModel(),
    onBack: () -> Unit = {},
    navController: NavController
) {
    val idPaciente = SesionUsuario.idUsuario ?: 0

    var fechaTexto by remember { mutableStateOf("") }
    var fechaSeleccionadaMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    var hora by remember { mutableStateOf("") }
    var tipoConsulta by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current

    val viewModelHoras: CitaHorariosViewModel = viewModel()

    val today = LocalDate.now()
    val maxDate = today.plusMonths(4)

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val fecha = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                return !fecha.isBefore(today) && !fecha.isAfter(maxDate)
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {

                        val epochDay = millis / (24L * 60 * 60 * 1000)
                        val fechaLocal = LocalDate.ofEpochDay(epochDay)

                        fechaSeleccionadaMillis = millis

                        fechaTexto = "%02d/%02d/%04d".format(
                            fechaLocal.dayOfMonth,
                            fechaLocal.monthValue,
                            fechaLocal.year
                        )
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LaunchedEffect(fechaSeleccionadaMillis) {
        if (fechaSeleccionadaMillis != null) {

            val epochDay = fechaSeleccionadaMillis!! / (24L * 60 * 60 * 1000)
            val fechaLocal = LocalDate.ofEpochDay(epochDay)

            val fechaISO = "%04d-%02d-%02d".format(
                fechaLocal.year,
                fechaLocal.monthValue,
                fechaLocal.dayOfMonth
            )

            viewModelHoras.cargarHorasDisponibles(fechaISO)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🩺 Registrar nueva cita") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("🔙") }
                }
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { pad ->

        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = fechaTexto,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de la cita") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                }
            )

            var expandedHoras by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedHoras,
                onExpandedChange = { expandedHoras = it },
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = hora,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora disponible") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedHoras) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedHoras,
                    onDismissRequest = { expandedHoras = false }
                ) {
                    when {
                        viewModelHoras.cargando.value -> {
                            DropdownMenuItem(
                                text = { Text("Cargando horarios…") },
                                onClick = {}
                            )
                        }
                        viewModelHoras.error.value != null -> {
                            DropdownMenuItem(
                                text = { Text("Error al cargar horarios") },
                                onClick = {}
                            )
                        }
                        viewModelHoras.horas.value.isEmpty() -> {
                            DropdownMenuItem(
                                text = { Text("No hay horarios disponibles") },
                                onClick = {}
                            )
                        }
                        else -> {
                            viewModelHoras.horas.value.forEach { h ->
                                DropdownMenuItem(
                                    text = { Text(h) },
                                    onClick = {
                                        hora = h
                                        expandedHoras = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            var expandedTipo by remember { mutableStateOf(false) }

            val opcionesTipoConsulta = listOf(
                "Consulta general",
                "Control",
                "Receta / Seguimiento"
            )

            ExposedDropdownMenuBox(
                expanded = expandedTipo,
                onExpandedChange = { expandedTipo = it },
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = tipoConsulta,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de consulta") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expandedTipo)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expandedTipo,
                    onDismissRequest = { expandedTipo = false }
                ) {
                    opcionesTipoConsulta.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                tipoConsulta = opcion
                                expandedTipo = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {

                    if (fechaSeleccionadaMillis == null || hora.isBlank() || tipoConsulta.isBlank()) {
                        Toast.makeText(context, "Completa todos los campos requeridos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // 🔥 Recuperar fecha sin desfase
                    val epochDay = fechaSeleccionadaMillis!! / (24L * 60 * 60 * 1000)
                    val fechaLocal = LocalDate.ofEpochDay(epochDay)

                    // 🔥 Recuperar hora local segura
                    val partes = hora.split(":")
                    val horaInt = partes[0].toInt()
                    val minutoInt = partes[1].toInt()

                    val horaLocal = LocalTime.of(horaInt, minutoInt)

                    // 🔥 Crear LocalDateTime final EXACTO
                    val fechaHoraLocal: LocalDateTime = fechaLocal.atTime(horaLocal)

                    // 🔥 Convertir a ISO para .NET (sin restar días)
                    val apiDate = fechaHoraLocal.toString() + ":00"

                    val nuevaCita = Cita(
                        idPaciente = SesionUsuario.idUsuario ?: 0,
                        fechaHora = apiDate,
                        tipoConsulta = tipoConsulta,
                        notas = notas.ifBlank { null },
                        estatus = "A",
                        duracionMin = 30,
                        nombrePaciente = "",
                        observaciones = null,
                        diagnostico = null,
                        medicamentos = null

                    )

                    viewModel.registrarCita(nuevaCita)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cita 💾")
            }

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
