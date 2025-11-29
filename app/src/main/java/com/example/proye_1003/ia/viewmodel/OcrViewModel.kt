package com.example.proye_1003.ia.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.ia.services.OCRService
import com.example.proye_1003.models.Medicamento
import com.example.proye_1003.services.MedicamentoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OcrViewModel(
    private val ocrService: OCRService,
    private val medicamentoService: MedicamentoService
) : ViewModel() {

    private val _textoDetectado = MutableStateFlow("")
    val textoDetectado: StateFlow<String> = _textoDetectado

    private val _medicamentos = MutableStateFlow<List<Medicamento>>(emptyList())
    val medicamentos: StateFlow<List<Medicamento>> = _medicamentos

    fun procesarImagen(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                val texto = ocrService.procesarImagen(bitmap)
                _textoDetectado.value = texto

                val palabras = texto
                    .split(" ", "\n", ",", ".", ";", ":")
                    .map { it.trim() }
                    .filter { it.length > 2 }

                val listaCompleta = medicamentoService.getMedicamentos()

                val encontrados = listaCompleta.filter { med ->
                    palabras.any { p ->
                        med.nombre?.contains(p, ignoreCase = true) == true
                    }
                }

                _medicamentos.value = encontrados

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
