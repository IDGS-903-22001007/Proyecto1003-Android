package com.example.proye_1003.ia.ui

import com.example.proye_1003.ia.data.ChatMessage
import com.example.proye_1003.ia.ui.ChatBubble
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// ------------------ PANTALLA PRINCIPAL ------------------
@Composable
fun ChatScreen(
    onSendToIA: suspend (String) -> String
) {

    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // autoscroll al final
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }
        }

        Divider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe algo…") },
                singleLine = false,
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (inputText.isNotBlank()) {

                        // usuario manda mensaje
                        messages = messages + ChatMessage(inputText.trim(), true)

                        // llamada a la IA correctamente
                        scope.launch {
                            try {
                                val aiResponse = onSendToIA(inputText.trim())
                                messages = messages + ChatMessage(aiResponse, false)
                            } catch (e: Exception) {
                                messages = messages + ChatMessage("Error IA: ${e.message}", false)
                            }
                        }

                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank()
            ) {
                Text("Enviar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}




