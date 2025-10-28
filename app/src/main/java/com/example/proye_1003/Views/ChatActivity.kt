package com.example.proye_1003.views

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proye_1003.Auth.ChatViewModel
import com.example.proye_1003.Auth.ChatViewModelFactory
import com.example.proye_1003.data.Mensaje
import com.example.proye_1003.services.GeminiService
import java.io.InputStream

// Función para cargar la API key desde assets
fun loadServiceAccount(context: Context): InputStream {
    return context.assets.open("service_account.json")
}

@Composable
fun ChatScreen() {
    val context = LocalContext.current

    // Inicializa ViewModel usando Factory y GeminiService con el JSON de assets
    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(GeminiService(loadServiceAccount(context)))
    )

    val mensajes by viewModel.mensajes.collectAsState()
    var texto by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)) {

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(10.dp)
        ) {
            items(mensajes) { msg ->
                BurbujaMensaje(msg)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = texto,
                onValueChange = { texto = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (texto.isNotBlank()) {
                    viewModel.enviarMensaje(texto)
                    texto = ""
                }
            }) {
                Text("Enviar")
            }
        }
    }
}

@Composable
fun BurbujaMensaje(mensaje: Mensaje) {
    val alignment = if (mensaje.esUsuario) Alignment.End else Alignment.Start
    val color = if (mensaje.esUsuario) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val textColor = if (mensaje.esUsuario) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mensaje.esUsuario) Arrangement.End else Arrangement.Start
    ) {
        Text(
            text = mensaje.texto,
            color = textColor,
            modifier = Modifier
                .background(color, shape = MaterialTheme.shapes.medium)
                .padding(10.dp)
        )
    }
}
