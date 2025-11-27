import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proye_1003.models.OcrViewModel
import com.example.proye_1003.services.GeminiService
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.proye_1003.medicamentos.MedicamentoIA
import com.example.proye_1003.models.Medicamento
import com.example.proye_1003.services.MedicamentoService

// ------------------ MODELOS ------------------
data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

data class MessageForAI(
    val role: String, // "user" o "assistant"
    val content: String
)


// ------------------------------ Este es el modulo principal de la IA -------------------------

@Composable
fun OcrScreen(
    navController: NavController,
    ocrViewModel: OcrViewModel = viewModel(),
    medicamentoService: MedicamentoService
) {
    val context = LocalContext.current
    val texto by ocrViewModel.textoDetectado
    val error by ocrViewModel.error

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var conversationHistory by remember { mutableStateOf(mutableListOf<MessageForAI>()) }
    var inputText by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Carga los medicamentos en memoria
    LaunchedEffect(Unit) {
        MedicamentoIA.cargarDesdeApi(medicamentoService)
    }

    // ================= Launchers =================
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                ocrViewModel.procesarImagen(uriToFile(imageUri!!, context))
            }
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                val (file, uri) = createTempFile(context)
                imageUri = uri
                cameraLauncher.launch(uri)
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageUri = it
                ocrViewModel.procesarImagen(uriToFile(it, context))
            }
        }

    // ================= OCR detecta texto =================
    LaunchedEffect(texto) {
        if (texto.isNotEmpty()) {
            messages = messages + ChatMessage(texto, true)
            conversationHistory.add(MessageForAI("user", texto))
        }
    }

    // ================= UI =================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { navController.popBackStack() },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4E4E4E),
                contentColor = Color.White
            )
        ) {
            Text("Regresar")
        }

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    val (file, uri) = createTempFile(context)
                    imageUri = uri
                    cameraLauncher.launch(uri)
                } else {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }) { Text("Cámara") }

            Button(onClick = { galleryLauncher.launch("image/*") }) { Text("Galería") }
        }

        Spacer(Modifier.height(10.dp))

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier.size(220.dp),
                contentScale = ContentScale.Crop
            )
        }

        if (error.isNotEmpty()) Text("⚠ $error", color = Color.Red)

        // ================= Chat =================
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { msg ->
                ChatBubble(message = msg)
            }
        }

        Spacer(Modifier.height(8.dp))

        // ================= Input =================
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF7F7F7),
                    unfocusedContainerColor = Color(0xFFF1F1F1),
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                placeholder = { Text("Escribe algo…") }
            )


            Button(onClick = {
                if (inputText.isNotBlank()) {
                    messages = messages + ChatMessage(inputText, true)
                    conversationHistory.add(MessageForAI("user", inputText))

                    scope.launch {
                        try {
                            val similares = MedicamentoIA.buscarSimilares(inputText)

                            val prompt = buildString {
                                append("Eres un asistente experto en medicamentos.\n")
                                if (similares.isNotEmpty()) {
                                    append("Estos son los medicamentos encontrados:\n")
                                    similares.forEach { med ->
                                        append("- Nombre: ${med.nombre}, Descripción: ${med.descripcion}, Precio: ${med.precio}, Cantidad: ${med.cantidad}\n")
                                    }
                                } else {
                                    append("No se encontraron medicamentos similares.\n")
                                }
                                append("\nUsuario pregunta: \"$inputText\"\n")
                                append("Responde explicando los medicamentos encontrados o indicando que no hay resultados.")
                            }

                            val aiResponse = GeminiService().enviarConversacion(
                                conversationHistory + MessageForAI("user", prompt)
                            )

                            Log.d("OcrScreen", "Respuesta Gemini: $aiResponse")

                            messages = messages + ChatMessage(aiResponse, false)
                            conversationHistory.add(MessageForAI("assistant", aiResponse))

                        } catch (e: Exception) {
                            Log.e("OcrScreen", "Error al comunicarse con Gemini: ${e.message}")
                            val fallback = "No se encontraron medicamentos similares."
                            messages = messages + ChatMessage(fallback, false)
                            conversationHistory.add(MessageForAI("assistant", fallback))
                        }
                    }

                    inputText = ""
                }
            }) { Text("Enviar") }
        }
    }
}

// ------------------ BUBBLE ------------------

@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleColor =
        if (message.isUser) Color(0xFFB2FFC8) else Color(0xFFFFFFFF)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement =
            if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .padding(3.dp),
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                message.text,
                modifier = Modifier.padding(12.dp),
                color = Color.Black
            )
        }
    }
}

// ------------------ UTILIDADES ------------------
fun createTempFile(context: Context): Pair<File, Uri> {
    val file = File.createTempFile("captura_", ".jpg", context.cacheDir)
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    return file to uri
}

fun uriToFile(uri: Uri, context: Context): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw Exception("No se pudo abrir InputStream")
    val tempFile = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")
    tempFile.outputStream().use { output -> inputStream.copyTo(output) }
    return tempFile
}
