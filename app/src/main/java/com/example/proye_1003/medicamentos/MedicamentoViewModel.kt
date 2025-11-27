package com.example.proye_1003.medicamentos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.data.MedicamentoRepository
import com.example.proye_1003.models.Medicamento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MedsUiState {
    object Loading : MedsUiState()
    data class Error(val message: String) : MedsUiState()
    data class Data(val items: Unit) : MedsUiState()

}
class MedicamentoViewModel(private val repo: MedicamentoRepository) : ViewModel() {

    private val _state = MutableStateFlow<MedsUiState>(MedsUiState.Loading)
    val state: StateFlow<MedsUiState> = _state

    private val _detalle = MutableStateFlow<Medicamento?>(null)
    val detalle: StateFlow<Medicamento?> = _detalle

    init {
        cargarMedicamentos()
    }

    fun cargarMedicamentos(q: String? = null) {
        viewModelScope.launch {
            _state.value = MedsUiState.Loading
            try {
                val lista = repo.listar(q)
                _state.value = MedsUiState.Data(lista)
            } catch (e: Exception) {
                _state.value = MedsUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private fun MedicamentoRepository.listar(q: String?) {}


}
