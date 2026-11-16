package com.solucionesmoviles.maquinarias.data.local.dao

import androidx.room.*
import com.solucionesmoviles.maquinarias.data.local.entity.PlantaProduccionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantaProduccionDao {

    @Query("SELECT * FROM tbl_planta_produccion ORDER BY cod_planta ASC")
    fun getAllOrderByCodigo(): Flow<List<PlantaProduccionEntity>>

    @Query("SELECT * FROM tbl_planta_produccion ORDER BY nom_planta ASC")
    fun getAllOrderByNombre(): Flow<List<PlantaProduccionEntity>>

    @Query("SELECT * FROM tbl_planta_produccion WHERE estado_registro = 'A' ORDER BY nom_planta ASC")
    fun getActiveOnlyOrderByNombre(): Flow<List<PlantaProduccionEntity>>

    @Query("SELECT * FROM tbl_planta_produccion WHERE cod_planta = :codigo LIMIT 1")
    suspend fun getByCodigo(codigo: String): PlantaProduccionEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(planta: PlantaProduccionEntity)

    @Update
    suspend fun update(planta: PlantaProduccionEntity)

    // Inactivación lógica: conmuta a 'I'
    @Query("UPDATE tbl_planta_produccion SET estado_registro = 'I' WHERE cod_planta = :codigo")
    suspend fun inactivar(codigo: String)

    // Reactivación lógica: conmuta a 'A'
    @Query("UPDATE tbl_planta_produccion SET estado_registro = 'A' WHERE cod_planta = :codigo")
    suspend fun reactivar(codigo: String)

    // Eliminación lógica: conmuta a '*'
    @Query("UPDATE tbl_planta_produccion SET estado_registro = '*' WHERE cod_planta = :codigo")
    suspend fun eliminarLogico(codigo: String)
}
