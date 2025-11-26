package com.example.proye_1003.recordatorios.workers

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import com.example.proye_1003.R
import com.example.proye_1003.CHANNEL_RECORDATORIOS

class RecordatorioWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    @Suppress("MissingPermission")
    override fun doWork(): Result {

        val nombre = inputData.getString("nombreMedicamento") ?: "Medicamento"
        val dosis = inputData.getString("dosis") ?: ""
        val fechaInicio = inputData.getLong("fechaInicioMillis", 0L)
        val duracionDias = inputData.getInt("duracionDias", 0)
        val idRecordatorio = inputData.getLong("idRecordatorio", -1L)

        val duracionMillis = duracionDias * 24L * 60L * 60L * 1000L
        val fechaFin = fechaInicio + duracionMillis

        // Evita notificaciones después de terminar el tratamiento
        if (System.currentTimeMillis() > fechaFin && idRecordatorio != -1L) {
            WorkManager.getInstance(applicationContext)
                .cancelAllWorkByTag("recordatorio_$idRecordatorio")
            return Result.success()
        }

        val mensaje = if (dosis.isBlank()) {
            "Es hora de tomar: $nombre"
        } else {
            "Es hora de tomar: $nombre ($dosis)"
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_RECORDATORIOS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Recordatorio de medicamento")
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(System.currentTimeMillis().toInt(), notification)

        return Result.success()
    }
}
