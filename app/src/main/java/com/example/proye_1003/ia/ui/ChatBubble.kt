package com.example.proye_1003.ia.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proye_1003.ia.data.ChatMessage

@Composable
fun ChatBubble(message: ChatMessage) {

    val bubbleColor =
        if (message.isUser) Color(0xFFD1E7FF) else Color(0xFFEAEAEA)

    val horizontal = if (message.isUser)
        Alignment.End
    else
        Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = horizontal
    ) {
        Box(
            modifier = Modifier
                .background(
                    bubbleColor,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(12.dp)
        ) {
            Text(text = message.text)
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}
