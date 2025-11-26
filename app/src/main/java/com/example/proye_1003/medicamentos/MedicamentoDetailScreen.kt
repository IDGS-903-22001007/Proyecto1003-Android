package com.example.proye_1003.medicamentos

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
            med = RetrofitClient.medicamentoService.getById(id)
        } catch (e: Exception) {
            error = e.message
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(med?.nombre ?: "Detalle del medicamento") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { pad ->

        when {
            loading -> LinearProgressIndicator(
                Modifier
                    .fillMaxWidth()
                    .padding(pad)
            )

            error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)

            med != null -> {
                val m = med!!

                LazyColumn(
                    modifier = Modifier
                        .padding(pad)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    // ---------- IMAGEN ----------
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .clip(RoundedCornerShape(24.dp))
                        ) {
                            // Fondo elegante para disimular los bordes de la imagen original
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.surfaceVariant,
                                                MaterialTheme.colorScheme.surface
                                            )
                                        )
                                    )
                            )

                            // Imagen principal del medicamento sobre el fondo bonito
                            AsyncImage(
                                model = ApiUrls.foto(m.fotoUrl),
                                contentDescription = m.nombre,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp)
                                    .height(200.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    // ---------- NOMBRE ----------
                    item {
                        Text(
                            text = m.nombre ?: "Medicamento",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    // ---------- PRECIO & STOCK ----------
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            m.precio?.let { p ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text("💲 ${"%.2f".format(p)}") }
                                )
                            }
                            m.cantidad?.let { c ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text("📦 $c disponibles") }
                                )
                            }
                        }
                    }

                    // ---------- SECCIONES COLAPSABLES ----------
                    item { colapsable("Descripción", m.descripcion) }
                    item { colapsable("Beneficios", m.beneficios) }
                    item { colapsable("Instrucciones", m.instrucciones) }
                    item { colapsable("Advertencias", m.advertencias) }
                }
            }
        }
    }
}

@Composable
fun colapsable(titulo: String, contenido: String?) {
    if (contenido.isNullOrBlank()) return

    var expandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable { expandido = !expandido }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    titulo,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expandido) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
            }

            if (expandido) {
                Spacer(Modifier.height(8.dp))
                Text(contenido, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
