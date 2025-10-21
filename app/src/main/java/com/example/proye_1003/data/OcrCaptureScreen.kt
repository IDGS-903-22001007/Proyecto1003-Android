package com.example.proye_1003.data

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.proye_1003.models.OcrViewModel
import java.io.File
import java.io.InputStream
import java.io.OutputStream

@Composable
fun OcrCaptureScreen(viewModel: OcrViewModel = viewModel()) {
    val context = LocalContext.current

    var textoDetectado by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var fileSeleccionado by remember { mutableStateOf<File?>(null) }

    // Launcher para seleccionar imagen de la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val file = File(context.cacheDir, "receta.jpg")
                context.contentResolver.openInputStream(uri).use { input: InputStream? ->
                    file.outputStream().use { output: OutputStream ->
                        input?.copyTo(output)
                    }
                }
                fileSeleccionado = file
                error = ""
            } catch (e: Exception) {
                error = "No se pudo cargar la imagen: ${e.message}"
            }
        }
    }

    // Launcher para tomar foto con cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val file = File(context.cacheDir, "receta_camara.jpg")
            val fos = file.outputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            fos.close()
            fileSeleccionado = file
            error = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Captura tu receta",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Text("Galería")
            }

            Button(onClick = { cameraLauncher.launch(null) }) {
                Text("Cámara")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        fileSeleccionado?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Imagen seleccionada",
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                fileSeleccionado?.let { file ->
                    cargando = true
                    textoDetectado = ""
                    error = ""
                    viewModel.procesarImagen(
                        file,
                        onSuccess = { texto ->
                            textoDetectado = texto
                            cargando = false
                        },
                        onError = { err ->
                            error = err
                            cargando = false
                        }
                    )
                } ?: run {
                    error = "Primero selecciona o captura una imagen"
                }
            }
        ) {
            Text("Procesar OCR")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (cargando) {
            CircularProgressIndicator()
        }

        if (error.isNotEmpty()) {
            Text("Error: $error", color = Color.Red)
        }

        if (textoDetectado.isNotEmpty()) {
            Text("Texto detectado: $textoDetectado", color = Color.Green)
        }
    }
}
