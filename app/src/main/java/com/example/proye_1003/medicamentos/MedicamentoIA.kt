package com.example.proye_1003.medicamentos

import android.util.Log
import com.example.proye_1003.models.Medicamento
import com.example.proye_1003.services.MedicamentoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object MedicamentoIA {

    private val medicamentosEnMemoria = mutableListOf<Medicamento>()

    // Carga la lista desde el endpoint existente
    suspend fun cargarDesdeApi(api: MedicamentoService) {
        try {
            val respuesta = withContext(Dispatchers.IO) {
                api.getMedicamentos() // mismo endpoint que ya tienes
            }

            if (respuesta.isSuccessful) {
                medicamentosEnMemoria.clear()
                respuesta.body()?.let { medicamentosEnMemoria.addAll(it) }
            } else {
                Log.e("MedicamentoIA", "Error al cargar medicamentos: ${respuesta.code()}")
            }
        } catch (e: Exception) {
            Log.e("MedicamentoIA", "Excepción al cargar medicamentos: ${e.message}")
        }
    }

    // Busca coincidencias por nombre (para la IA)
    fun buscarSimilares(nombre: String): List<Medicamento> {
        return medicamentosEnMemoria.filter {
            it.nombre?.contains(nombre, ignoreCase = true) == true
        }
    }

    // Devuelve todos los medicamentos en memoria
    fun getTodos(): List<Medicamento> = medicamentosEnMemoria.toList()
}