package com.example.proye_1003.Views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.proye_1003.Auth.AuthMedicamentoViewModel
import com.example.proye_1003.models.Medicamento

@Composable
fun MedicamentoScreen(viewModel: AuthMedicamentoViewModel = viewModel()) {

    val medicamento by viewModel.medicamentos.observeAsState(emptyList<Medicamento>())
    val error by viewModel.error.observeAsState()
    var busqueda by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        viewModel.cargarMedicamentos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar medicamento") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        error?.let {
            Text(
                text = it,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }

        val listaFiltrada = remember(busqueda, medicamento) {
            medicamento.filter { it.nombre?.contains(busqueda, true) == true }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(listaFiltrada) { med ->
                MedicamentoCard(med)
            }
        }
    }
}

@Composable
fun MedicamentoCard(med: Medicamento) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            if (!med.fotoUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(model = med.fotoUrl),
                    contentDescription = med.nombre ?: "",
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {

                Text(
                    text = med.nombre ?: "Sin nombre",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text("Tipo: ${med.tipo ?: "N/A"}")
                Text("Cantidad: ${med.cantidad ?: 0}")
                Text("Precio: $${med.precio ?: 0.0}")

                med.descripcion?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
