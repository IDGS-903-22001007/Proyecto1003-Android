package com.example.proye_1003.Views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proye_1003.models.OcrViewModel
import java.io.File
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OcrScreen(viewModel: OcrViewModel = viewModel()) {
    var textoDetectado by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val file = File("") // aquí metes la ruta de prueba
            viewModel.procesarImagen(
                file,
                onSuccess = { textoDetectado = it },
                onError = { error = it }
            )
        }) {
            Text("Procesar Imagen")
        }

        Spacer(Modifier.height(20.dp))

        if (error.isNotEmpty()) Text(" $error")
        if (textoDetectado.isNotEmpty()) Text(" Texto: $textoDetectado")
    }
}
