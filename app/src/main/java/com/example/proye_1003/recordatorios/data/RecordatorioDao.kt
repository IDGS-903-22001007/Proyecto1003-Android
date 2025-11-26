package com.example.proye_1003.recordatorios.data

import androidx.room.*

@Dao
interface RecordatorioDao {

    @Query("SELECT * FROM recordatorios WHERE idUsuario = :idUsuario AND activo = 1 ORDER BY id DESC")
    suspend fun getActivosPorUsuario(idUsuario: Int): List<RecordatorioEntity>

    @Insert
    suspend fun insert(r: RecordatorioEntity): Long

    @Update
    suspend fun update(r: RecordatorioEntity)

    @Delete
    suspend fun delete(r: RecordatorioEntity)
}
