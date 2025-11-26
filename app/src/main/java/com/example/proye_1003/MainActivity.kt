package com.example.proye_1003

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.proye_1003.data.AppNavigator

// 🔔 FUNCIÓN QUE CREA EL CANAL DE NOTIFICACIONES
fun crearCanalRecordatorios(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Recordatorios de Medicamentos"
        val description = "Notificaciones para recordar tomas de medicamentos"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(
            CHANNEL_RECORDATORIOS, // <- constante desde Constants.kt
            name,
            importance
        ).apply {
            this.description = description
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔔 1) Crear el canal de notificaciones
        crearCanalRecordatorios(this)

        // 🛑 2) Solicitar permiso POST_NOTIFICATIONS (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }

        // 🎨 3) Renderizar UI (tu contenido original)
        setContent {
            MaterialTheme {
                Surface {
                    val nav = rememberNavController()
                    AppNavigator(nav)
                }
            }
        }
    }
}
