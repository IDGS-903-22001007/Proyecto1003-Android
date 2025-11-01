package com.example.proye_1003.medicamentos

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proye_1003.models.Medicamento
import com.example.proye_1003.services.ApiUrls
import com.example.proye_1003.services.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicamentoDetailScreen(nav: NavController, id: Int) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var med by remember { mutableStateOf<Medicamento?>(null) }

    LaunchedEffect(id) {
        try {
            error = null
            med = RetrofitClient.medicamentoService.getById(id)
        } catch (e: Exception) {
            error = e.message ?: "Error al cargar"
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(med?.nombre ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                loading -> {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
                error != null -> {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                }
                med != null -> {
                    val m = med!!

                    // Imagen (acepta fotoUrl o imagenUrl)
                    AsyncImage(
                        model = ApiUrls.foto(m.fotoUrl ?: m.fotoUrl),
                        contentDescription = m.nombre ?: "Medicamento",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )

                    Text(
                        text = m.nombre ?: "Medicamento",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        m.precio?.let { p ->
                            AssistChip(onClick = {}, label = { Text("💲 ${"%.2f".format(p)}") })
                        }
                        val qty = m.cantidad ?: m.cantidad
                        qty?.let { c ->
                            AssistChip(onClick = {}, label = { Text("📦 $c") })
                        }
                    }

                    // Campos largos opcionales
                    m.descripcion?.takeIf { it.isNotBlank() }?.let {
                        Text("Descripción", style = MaterialTheme.typography.titleMedium)
                        Text(it)
                    }
                    m.beneficios?.takeIf { it.isNotBlank() }?.let {
                        Text("Beneficios", style = MaterialTheme.typography.titleMedium)
                        Text(it)
                    }
                    m.instrucciones?.takeIf { it.isNotBlank() }?.let {
                        Text("Instrucciones", style = MaterialTheme.typography.titleMedium)
                        Text(it)
                    }
                    m.advertencias?.takeIf { it.isNotBlank() }?.let {
                        Text("Advertencias", style = MaterialTheme.typography.titleMedium)
                        Text(it)
                    }
                }
                else -> {
                    Text("No se encontró el medicamento.")
                }
            }
        }
    }
}
