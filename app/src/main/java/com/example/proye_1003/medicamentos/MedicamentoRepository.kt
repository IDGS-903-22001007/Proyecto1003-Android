import com.example.proye_1003.models.Medicamento

import com.example.proye_1003.services.MedicamentoService


class MedicamentoRepository(private val api: MedicamentoService) {

    suspend fun listar(q: String? = null): List<Medicamento> {
        val res = api.getMedicamentos(q)
        return if (res.isSuccessful) {
            res.body() ?: emptyList()
        } else {
            throw Exception("Error: ${res.code()}")
        }
    }

    suspend fun detalle(id: Int): Medicamento {
        val res = api.getById(id)
        return if (res.isSuccessful) {
            res.body() ?: throw Exception("Sin datos")
        } else {
            throw Exception("Error detalle: ${res.code()}")
        }
    }
}

