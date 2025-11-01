package com.example.proye_1003.medicamentos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proye_1003.models.Medicamento
import com.example.proye_1003.services.ApiUrls
import com.example.proye_1003.services.RetrofitClient
import com.example.proye_1003.Auth.BottomNavBar // 👈 Importamos la barra inferior

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicamentosListScreen(navController: NavController) {

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var items by remember { mutableStateOf(listOf<Medicamento>()) }

    // Carga inicial de medicamentos desde la API
    LaunchedEffect(Unit) {
        try {
            val data = RetrofitClient.medicamentoService.getMedicamentos()
            items = data
        } catch (e: Exception) {
            error = e.message ?: "Error de red"
        } finally {
            loading = false
        }
    }

    // Filtro de búsqueda
    val filtered = remember(items, query) {
        val t = query.trim()
        if (t.isEmpty()) items
        else items.filter { m ->
            val nombre = m.nombre ?: ""
            val desc = m.descripcion ?: ""
            (nombre + " " + desc).contains(t, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("💊 Medicamentos") }) },
        bottomBar = { BottomNavBar(navController = navController) } // 👈 Barra inferior global
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .padding(12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar medicamento...") },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            when {
                loading -> {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
                error != null -> {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(filtered, key = { it.id ?: it.hashCode() }) { m ->
                            MedicamentoRow(
                                item = m,
                                onClick = {
                                    val id = m.id ?: return@MedicamentoRow
                                    navController.navigate("meds/$id")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicamentoRow(item: Medicamento, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(Modifier.padding(12.dp)) {
            AsyncImage(
                model = ApiUrls.foto(item.fotoUrl),
                contentDescription = item.nombre ?: "Medicamento",
                modifier = Modifier.size(72.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    item.nombre ?: "Sin nombre",
                    style = MaterialTheme.typography.titleMedium
                )

                if (!item.descripcion.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        item.descripcion!!,
                        maxLines = 2,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    item.precio?.let { precio ->
                        AssistChip(onClick = {}, label = { Text("💲 ${"%.2f".format(precio)}") })
                    }

                    val qty = item.cantidad ?: item.cantidad
                    qty?.let { cantidad ->
                        AssistChip(onClick = {}, label = { Text("📦 $cantidad") })
                    }
                }
            }
        }
    }
}
