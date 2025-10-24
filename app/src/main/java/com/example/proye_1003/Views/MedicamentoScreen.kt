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
    val medicamentos by viewModel.medicamentos.observeAsState(emptyList())
    val error by viewModel.error.observeAsState(null)

    var busqueda by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarMedicamentos()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar medicamento") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (error != null) {
            Text(text = error!!, color = Color.Red, fontWeight = FontWeight.Bold)
        }

        val listaFiltrada = medicamentos.filter {
            it.nombre.contains(busqueda, ignoreCase = true)
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(listaFiltrada) { med ->
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
                        // Imagen del medicamento
                        if (med.fotoUrl != null) {
                            Image(
                                painter = rememberAsyncImagePainter(med.fotoUrl),
                                contentDescription = med.nombre,
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = med.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Tipo: ${med.tipo}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Cantidad: ${med.cantidad}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Precio: $${med.precio}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = med.descripcion, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}












