package com.example.proye_1003.data

import com.example.proye_1003.models.Medicamento
import com.example.proye_1003.services.RetrofitClient


class MedicamentoRepository {

    suspend fun getMedicamentos(): List<Medicamento>? {
        return try {
            val response = RetrofitClient.medicamentoService.getMedicamentos()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}