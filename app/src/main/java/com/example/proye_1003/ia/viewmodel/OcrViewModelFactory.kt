package com.example.proye_1003.ia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proye_1003.ia.services.GeminiService
import com.example.proye_1003.ia.services.OCRService
import com.example.proye_1003.services.MedicamentoService

class OcrViewModelFactory(
    private val ocrService: OCRService,
    private val medicamentoService: MedicamentoService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OcrViewModel(ocrService, medicamentoService) as T
    }
}

