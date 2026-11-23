package com.solucionesmoviles.maquinarias.data.repository

import com.solucionesmoviles.maquinarias.data.local.dao.LineaProduccionDao
import com.solucionesmoviles.maquinarias.data.local.dao.MaquinariaDao
import com.solucionesmoviles.maquinarias.data.local.dao.PlantaProduccionDao
import com.solucionesmoviles.maquinarias.data.local.dao.UbicacionLineaDao
import com.solucionesmoviles.maquinarias.data.local.entity.LineaProduccionEntity
import com.solucionesmoviles.maquinarias.data.local.entity.MaquinariaDetalle
import com.solucionesmoviles.maquinarias.data.local.entity.MaquinariaEntity
import com.solucionesmoviles.maquinarias.data.local.entity.PlantaProduccionEntity
import com.solucionesmoviles.maquinarias.data.local.entity.UbicacionLineaEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio unificado para el Módulo 12: Maestro de Maquinarias y sus 3 tablas referenciales.
 */
class MachineryRepository(
    private val plantaDao: PlantaProduccionDao,
    private val lineaDao: LineaProduccionDao,
    private val ubicacionDao: UbicacionLineaDao,
    private val maquinariaDao: MaquinariaDao
) {
    // --- Plantas ---
    fun getPlantas(orderByNombre: Boolean): Flow<List<PlantaProduccionEntity>> =
        if (orderByNombre) plantaDao.getAllOrderByNombre() else plantaDao.getAllOrderByCodigo()

    fun getActivePlantas(): Flow<List<PlantaProduccionEntity>> = plantaDao.getActiveOnlyOrderByNombre()

    suspend fun existsPlanta(codigo: String): Boolean = plantaDao.getByCodigo(codigo) != null

    suspend fun insertPlanta(codigo: String, nombre: String) {
        plantaDao.insert(PlantaProduccionEntity(codigo, nombre.trim(), "A"))
    }

    suspend fun updatePlanta(codigo: String, nombre: String) {
        val actual = plantaDao.getByCodigo(codigo) ?: return
        plantaDao.update(actual.copy(nomPlanta = nombre.trim()))
    }

    suspend fun inactivarPlanta(codigo: String) = plantaDao.inactivar(codigo)
    suspend fun reactivarPlanta(codigo: String) = plantaDao.reactivar(codigo)
    suspend fun eliminarPlanta(codigo: String) = plantaDao.eliminarLogico(codigo)

    // --- Líneas ---
    fun getLineas(orderByNombre: Boolean): Flow<List<LineaProduccionEntity>> =
        if (orderByNombre) lineaDao.getAllOrderByNombre() else lineaDao.getAllOrderByCodigo()

    fun getActiveLineas(): Flow<List<LineaProduccionEntity>> = lineaDao.getActiveOnlyOrderByNombre()

    suspend fun existsLinea(codigo: String): Boolean = lineaDao.getByCodigo(codigo) != null

    suspend fun insertLinea(codigo: String, nombre: String) {
        lineaDao.insert(LineaProduccionEntity(codigo, nombre.trim(), "A"))
    }

    suspend fun updateLinea(codigo: String, nombre: String) {
        val actual = lineaDao.getByCodigo(codigo) ?: return
        lineaDao.update(actual.copy(nomLinea = nombre.trim()))
    }

    suspend fun inactivarLinea(codigo: String) = lineaDao.inactivar(codigo)
    suspend fun reactivarLinea(codigo: String) = lineaDao.reactivar(codigo)
    suspend fun eliminarLinea(codigo: String) = lineaDao.eliminarLogico(codigo)

    // --- Ubicaciones ---
    fun getUbicaciones(orderByNombre: Boolean): Flow<List<UbicacionLineaEntity>> =
        if (orderByNombre) ubicacionDao.getAllOrderByNombre() else ubicacionDao.getAllOrderByCodigo()

    fun getActiveUbicaciones(): Flow<List<UbicacionLineaEntity>> = ubicacionDao.getActiveOnlyOrderByNombre()

    suspend fun existsUbicacion(codigo: String): Boolean = ubicacionDao.getByCodigo(codigo) != null

    suspend fun insertUbicacion(codigo: String, nombre: String) {
        ubicacionDao.insert(UbicacionLineaEntity(codigo, nombre.trim(), "A"))
    }

    suspend fun updateUbicacion(codigo: String, nombre: String) {
        val actual = ubicacionDao.getByCodigo(codigo) ?: return
        ubicacionDao.update(actual.copy(nomUbicacion = nombre.trim()))
    }

    suspend fun inactivarUbicacion(codigo: String) = ubicacionDao.inactivar(codigo)
    suspend fun reactivarUbicacion(codigo: String) = ubicacionDao.reactivar(codigo)
    suspend fun eliminarUbicacion(codigo: String) = ubicacionDao.eliminarLogico(codigo)

    // --- Maquinarias ---
    fun getMaquinarias(orderByNombre: Boolean): Flow<List<MaquinariaDetalle>> =
        if (orderByNombre) maquinariaDao.getAllDetalleOrderByNombre() else maquinariaDao.getAllDetalleOrderByCodigo()

    suspend fun existsMaquinaria(codigo: String): Boolean = maquinariaDao.getByCodigo(codigo) != null

    suspend fun insertMaquinaria(
        codigo: String,
        nombre: String,
        codLinea: String,
        codPlanta: String,
        codUbicacion: String
    ) {
        maquinariaDao.insert(
            MaquinariaEntity(
                codMaquinaria = codigo,
                nomMaquinaria = nombre.trim(),
                codLinea = codLinea,
                codPlanta = codPlanta,
                codUbicacion = codUbicacion,
                estadoRegistro = "A"
            )
        )
    }

    suspend fun updateMaquinaria(
        codigo: String,
        nombre: String,
        codLinea: String,
        codPlanta: String,
        codUbicacion: String
    ) {
        val actual = maquinariaDao.getByCodigo(codigo) ?: return
        maquinariaDao.update(
            actual.copy(
                nomMaquinaria = nombre.trim(),
                codLinea = codLinea,
                codPlanta = codPlanta,
                codUbicacion = codUbicacion
            )
        )
    }

    suspend fun inactivarMaquinaria(codigo: String) = maquinariaDao.inactivar(codigo)
    suspend fun reactivarMaquinaria(codigo: String) = maquinariaDao.reactivar(codigo)
    suspend fun eliminarMaquinaria(codigo: String) = maquinariaDao.eliminarLogico(codigo)
}
