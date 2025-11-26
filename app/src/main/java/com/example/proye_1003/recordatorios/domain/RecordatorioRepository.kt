package com.example.proye_1003.recordatorios.domain

import android.content.Context
import androidx.work.*
import com.example.proye_1003.recordatorios.data.RecordatorioDao
import com.example.proye_1003.recordatorios.data.RecordatorioEntity
import com.example.proye_1003.recordatorios.workers.RecordatorioWorker
import java.util.concurrent.TimeUnit
import kotlin.math.max

class RecordatorioRepository(
    private val dao: RecordatorioDao,
    private val context: Context
) {

    suspend fun getActivosPorUsuario(idUsuario: Int): List<RecordatorioEntity> =
        dao.getActivosPorUsuario(idUsuario)

    suspend fun agregar(recordatorio: RecordatorioEntity): Long {
        val id = dao.insert(recordatorio)
        programarWork(id, recordatorio)
        return id
    }

    suspend fun actualizar(recordatorio: RecordatorioEntity) {
        dao.update(recordatorio)
        cancelarWork(recordatorio.id)
        programarWork(recordatorio.id, recordatorio)
    }

    suspend fun eliminar(recordatorio: RecordatorioEntity) {
        dao.update(recordatorio.copy(activo = false))
        cancelarWork(recordatorio.id)
    }

    private fun programarWork(id: Long, r: RecordatorioEntity) {
        val ahora = System.currentTimeMillis()
        val delayInicial = max(0L, r.fechaInicioMillis - ahora)

        val data = workDataOf(
            "idRecordatorio" to id,
            "nombreMedicamento" to r.nombreMedicamento,
            "dosis" to r.dosis,
            "fechaInicioMillis" to r.fechaInicioMillis,
            "duracionDias" to r.duracionDias
        )

        val work = PeriodicWorkRequestBuilder<RecordatorioWorker>(
            r.frecuenciaHoras.toLong(), TimeUnit.HOURS
        )
            .setInputData(data)
            .setInitialDelay(delayInicial, TimeUnit.MILLISECONDS)
            .addTag("recordatorio_$id")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "recordatorio_$id",
            ExistingPeriodicWorkPolicy.REPLACE,
            work
        )
    }

    private fun cancelarWork(id: Long) {
        WorkManager.getInstance(context).cancelAllWorkByTag("recordatorio_$id")
    }

    suspend fun reprogramarTodos(idUsuario: Int) {
        val lista = dao.getActivosPorUsuario(idUsuario)

        lista.forEach { recordatorio ->
            WorkManager.getInstance(context)
                .cancelAllWorkByTag("recordatorio_${recordatorio.id}")
        }

        lista.forEach { recordatorio ->
            programarWork(recordatorio.id, recordatorio)
        }
    }

}
