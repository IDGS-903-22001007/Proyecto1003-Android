// Kotlin
package com.example.proye_1003.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var currentScreen by remember { mutableStateOf("login") }

                    var pendingMessage by remember { mutableStateOf<String?>(null) }

                    when (currentScreen) {
                        "login" -> LoginScreen(
                            onNavigateToRegister = { currentScreen = "register" },
                            initialMessage = pendingMessage,
                            onMessageShown = { pendingMessage = null }
                        )
                        "register" -> RegisterScreen(
                            onRegisterSuccess = { msg ->
                                pendingMessage = msg
                                currentScreen = "login"
                            },
                            onBack = { currentScreen = "login" }
                        )
                    }
                }
            }
        }
    }
}