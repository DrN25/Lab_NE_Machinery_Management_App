package com.solucionesmoviles.maquinarias.data.repository

import com.solucionesmoviles.maquinarias.data.local.dao.PlantaProduccionDao
import com.solucionesmoviles.maquinarias.data.local.entity.PlantaProduccionEntity
import kotlinx.coroutines.flow.Flow

class PlantaProduccionRepository(private val dao: PlantaProduccionDao) {

    fun getPlantas(orderByNombre: Boolean): Flow<List<PlantaProduccionEntity>> {
        return if (orderByNombre) dao.getAllOrderByNombre() else dao.getAllOrderByCodigo()
    }

    fun getActivePlantas(): Flow<List<PlantaProduccionEntity>> = dao.getActiveOnlyOrderByNombre()

    suspend fun insertPlanta(codigo: String, nombre: String) {
        val entity = PlantaProduccionEntity(
            codPlanta = codigo.trim().uppercase(),
            nomPlanta = nombre.trim(),
            estadoRegistro = "A" // Requerimiento estricto: Todo registro nuevo recibe 'A'
        )
        dao.insert(entity)
    }

    suspend fun updatePlanta(codigo: String, nombre: String) {
        val actual = dao.getByCodigo(codigo) ?: return
        val modificado = actual.copy(nomPlanta = nombre.trim())
        dao.update(modificado)
    }

    suspend fun inactivar(codigo: String) = dao.inactivar(codigo)

    suspend fun reactivar(codigo: String) = dao.reactivar(codigo)

    suspend fun eliminarLogico(codigo: String) = dao.eliminarLogico(codigo)
}
