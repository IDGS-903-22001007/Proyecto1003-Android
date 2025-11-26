package com.example.proye_1003.recordatorios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proye_1003.recordatorios.domain.RecordatorioRepository

class RecordatoriosViewModelFactory(
    private val repo: RecordatorioRepository,
    private val idUsuario: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordatoriosViewModel::class.java)) {
            return RecordatoriosViewModel(repo, idUsuario) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
