package com.example.proye_1003.medicamentos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
// Si quieres ser consistente con el resto, puedes usar NavHostController en vez de NavController.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(nav: NavController) {
    Scaffold(
        topBar = {
            TopAppBar( // Material 3
                title = { Text("Farmacia · Menú", fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¿Qué deseas gestionar?", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { nav.navigate("citas") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📅 Citas")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { nav.navigate("meds") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("💊 Medicamentos")
            }
        }
    }
}
