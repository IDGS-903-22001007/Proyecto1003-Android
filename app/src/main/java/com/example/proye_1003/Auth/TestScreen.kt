package com.example.proye_1003.Auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TestScreen() {
    // Aquí llamamos directamente a tu CitasScreen con un ID de prueba
    CitasScreen(
        idPaciente = 1,
        onBack = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTestScreen() {
    TestScreen()
}
