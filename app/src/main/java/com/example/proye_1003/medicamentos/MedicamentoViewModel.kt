package com.example.proye_1003.medicamentos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.models.Medicamento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MedsUiState {
    object Loading : MedsUiState()
    data class Error(val message: String) : MedsUiState()
    data class Data(val items: List<Medicamento>) : MedsUiState()
}

class MedicamentoViewModel(
    private val repo: MedicamentoRepository
) : ViewModel() {

    private val _state = MutableStateFlow<MedsUiState>(MedsUiState.Loading)
    val state: StateFlow<MedsUiState> = _state

    private val _detalle = MutableStateFlow<Medicamento?>(null)
    val detalle: StateFlow<Medicamento?> = _detalle

    fun cargarListado(q: String? = null) = viewModelScope.launch {
        _state.value = MedsUiState.Loading
        try {
            val items = repo.listar(q)
            _state.value = MedsUiState.Data(items)
        } catch (e: Exception) {
            _state.value = MedsUiState.Error(e.message ?: "Error de red")
        }
    }

    fun cargarDetalle(id: Int) = viewModelScope.launch {
        _detalle.value = null
        try {
            _detalle.value = repo.detalle(id)
        } catch (e: Exception) {
            _state.value = MedsUiState.Error(e.message ?: "Error al cargar detalle")
        }
    }
}
