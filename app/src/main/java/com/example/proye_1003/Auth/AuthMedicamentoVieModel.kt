package com.example.proye_1003.Auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proye_1003.data.MedicamentoRepository
import com.example.proye_1003.models.Medicamento
import kotlinx.coroutines.launch


class AuthMedicamentoViewModel : ViewModel() {

    private val repository = MedicamentoRepository() // Aquí va tu repo

    // LiveData que la UI va a observar
    private val _medicamentos = MutableLiveData<List<Medicamento>>()
    val medicamentos: LiveData<List<Medicamento>> get() = _medicamentos

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Función para cargar los medicamentos desde la API
    fun cargarMedicamentos() {
        viewModelScope.launch {
            try {
                val result = repository.getMedicamentos()
                if (result != null) {
                    _medicamentos.postValue(result)
                    _error.postValue(null)
                } else {
                    _error.postValue("Error al cargar los medicamentos")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.postValue("Error de red: ${e.message}")
            }
        }
    }
}