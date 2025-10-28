package com.example.proye_1003.Views

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.proye_1003.models.Medicamento
import com.example.proye_1003.models.OcrViewModel
import com.example.proye_1003.Auth.RecomendacionViewModel
import java.io.File

@Composable
fun OcrScreen(
    ocrViewModel: OcrViewModel = viewModel(),
    recomendacionViewModel: RecomendacionViewModel = viewModel()
) {
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var textoDetectado by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var busqueda by remember { mutableStateOf("") }

    val listaRecomendaciones by recomendacionViewModel.recomendaciones.collectAsState(initial = emptyList())
    val errorRecomendacion by recomendacionViewModel.error.collectAsState(initial = "")

    // Selector de imagen
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            val file = uriToFile(it, context)
            ocrViewModel.procesarImagen(
                file,
                onSuccess = { texto ->
                    textoDetectado = texto
                    // Mandar el texto a recomendaciones
                    recomendacionViewModel.obtenerRecomendaciones(texto)
                },
                onError = { e -> error = e }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sube tu receta o medicamento", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(16.dp))

        Button(onClick = { imagePicker.launch("image/*") }) {
            Text("Seleccionar Imagen")
        }

        Spacer(Modifier.height(16.dp))

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(16.dp))

        if (error.isNotEmpty()) Text("⚠️ Error OCR: $error", color = MaterialTheme.colorScheme.error)
        if (textoDetectado.isNotEmpty()) Text("Texto detectado: $textoDetectado")

        Spacer(Modifier.height(16.dp))

        // This is more idiomatic and easier to read.
        if (!errorRecomendacion.isNullOrEmpty()) {
            Text("⚠️ Error Recomendación: $errorRecomendacion", color = MaterialTheme.colorScheme.error)
        }


        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(listaRecomendaciones.filter { it.nombre.contains(busqueda, ignoreCase = true) }) { med ->
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
                        med.fotoUrl?.let { url ->
                            Image(
                                painter = rememberAsyncImagePainter(url),
                                contentDescription = med.nombre,
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(4.dp),
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

// Función para convertir Uri a File
fun uriToFile(uri: Uri, context: Context): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, "temp_image.jpg")
    inputStream?.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return file
}
