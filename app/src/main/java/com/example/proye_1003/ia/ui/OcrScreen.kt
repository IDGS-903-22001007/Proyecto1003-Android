package com.example.proye_1003.ia.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.proye_1003.ia.viewmodel.OcrViewModel
import java.io.File

@Composable
fun OcrScreen(
    viewModel: OcrViewModel,
    navController: NavHostController
) {
    val ctx = LocalContext.current

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val textoDetectado by viewModel.textoDetectado.collectAsState()
    val medicamentos by viewModel.medicamentos.collectAsState()

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { bitmap = loadBitmap(ctx, it) }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { ok ->
        if (ok && imageUri != null) {
            bitmap = loadBitmap(ctx, imageUri!!)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text("🔍 OCR + IA + Medicamentos", style = MaterialTheme.typography.headlineSmall)
        }

        // BOTONES
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { pickImageLauncher.launch("image/*") }) {
                    Text("📁 Galería")
                }

                Button(onClick = {
                    val imgFile = File(ctx.cacheDir, "foto_temp.jpg")
                    val uri = FileProvider.getUriForFile(
                        ctx,
                        "${ctx.packageName}.provider",
                        imgFile
                    )
                    imageUri = uri
                    takePictureLauncher.launch(uri)
                }) {
                    Text("📸 Cámara")
                }
            }
        }

        // IMAGEN PREVISUALIZADA
        item {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }
        }

        // BOTÓN PROCESAR
        item {
            Button(
                onClick = { bitmap?.let { bm -> viewModel.procesarImagen(bm) } },
                enabled = bitmap != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔎 Procesar con OCR")
            }
        }

        // TEXTO DETECTADO
        if (textoDetectado.isNotEmpty()) {
            item {
                Text("📄 Texto detectado:", style = MaterialTheme.typography.titleMedium)
                Text(textoDetectado)
            }
        }

        // MEDICAMENTOS ENCONTRADOS
        if (medicamentos.isNotEmpty()) {
            item {
                Text("💊 Medicamentos encontrados:", style = MaterialTheme.typography.titleMedium)
            }

            items(medicamentos) { med ->
                Card(
                    onClick = { navController.navigate("meds/${med.id}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("💊 ${med.nombre}", style = MaterialTheme.typography.titleMedium)
                        Text("ID: ${med.id}")
                    }
                }
            }
        }
    }
}


private fun loadBitmap(context: android.content.Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}
