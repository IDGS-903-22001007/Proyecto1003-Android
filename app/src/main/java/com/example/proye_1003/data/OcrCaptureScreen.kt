package com.example.proye_1003.data

import android.graphics.Bitmap
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
import com.example.proye_1003.services.GeminiService
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@Composable
fun OcrCaptureScreen(
    viewModel: OcrViewModel = viewModel()
) {
    val context = LocalContext.current
    val textoDetectado by viewModel.textoDetectado
    val error by viewModel.error

    var fileSeleccionado by remember { mutableStateOf<File?>(null) }
    var cargandoIA by remember { mutableStateOf(false) }
    var respuestaIA by remember { mutableStateOf("") }
    var prompt by remember { mutableStateOf("") }

    val geminiService = remember { GeminiService() }

    suspend fun enviarAI() {
        cargandoIA = true
        try {
            respuestaIA = geminiService.enviarTextoAI(
                "$prompt\n\nTexto detectado OCR:\n$textoDetectado"
            )
        } catch (e: Exception) {
            respuestaIA = "Error IA: ${e.message}"
        }
        cargandoIA = false
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val file = File(context.cacheDir, "receta.jpg")
            context.contentResolver.openInputStream(uri).use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }
            fileSeleccionado = file
            viewModel.procesarImagen(file)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val file = File(context.cacheDir, "receta_camara.jpg")
            file.outputStream().use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            }
            fileSeleccionado = file
            viewModel.procesarImagen(file)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //---------------------------
        // Botones captura
        //---------------------------
        Row {
            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Text("Galería")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { cameraLauncher.launch(null) }) {
                Text("Cámara")
            }
        }

        Spacer(Modifier.height(12.dp))

        //---------------------------
        // INPUT del PROMPT + botón
        //---------------------------
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Tu prompt") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.viewModelScope.launch {
                    enviarAI()
                }
            },
            enabled = textoDetectado.isNotEmpty() && prompt.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar a IA")
        }

        Spacer(Modifier.height(16.dp))

        //---------------------------
        // Imagen seleccionada
        //---------------------------
        fileSeleccionado?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier.height(200.dp).fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }

        //---------------------------
        // Texto detectado por OCR
        //---------------------------
        if (textoDetectado.isNotEmpty()) {
            Text("Texto detectado:")
            Text(textoDetectado)
        }

        if (cargandoIA) {
            CircularProgressIndicator()
            Text("Procesando IA...")
        }

        //---------------------------
        // Respuesta IA
        //---------------------------
        if (respuestaIA.isNotEmpty()) {
            Text("Respuesta IA:")
            Text(respuestaIA)
        }
    }
}
