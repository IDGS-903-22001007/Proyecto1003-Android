package com.example.proye_1003.medicamentos

import com.example.proye_1003.models.Medicamento
import com.example.proye_1003.services.MedicamentoService

class MedicamentoRepository(
    private val api: MedicamentoService
) {
    suspend fun listar(q: String? = null): List<Medicamento> =
        api.getMedicamentos(q = q)

    suspend fun detalle(id: Int): Medicamento =
        api.getById(id)
}
