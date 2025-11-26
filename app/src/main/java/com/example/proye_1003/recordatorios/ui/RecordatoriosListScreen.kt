package com.example.proye_1003.recordatorios.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proye_1003.Auth.BottomNavBar
import com.example.proye_1003.recordatorios.data.RecordatorioEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordatoriosListScreen(
    recordatorios: List<RecordatorioEntity>,
    onAdd: () -> Unit,
    onEdit: (RecordatorioEntity) -> Unit,
    onDelete: (RecordatorioEntity) -> Unit,
    navController: NavController   // 👈 agregado
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recordatorios · Medicamentos") }
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)   // 👈 igual que en MenuScreen
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar recordatorio")
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(recordatorios) { r ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onEdit(r) }
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(r.nombreMedicamento, style = MaterialTheme.typography.titleMedium)
                            Text("Dosis: ${r.dosis}")
                            Text("Cada ${r.frecuenciaHoras} horas")
                            Text("Duración: ${r.duracionDias} días")

                            Spacer(Modifier.height(8.dp))

                            TextButton(onClick = { onDelete(r) }) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}
