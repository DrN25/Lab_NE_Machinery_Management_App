package com.solucionesmoviles.maquinarias.data.local.dao

import androidx.room.*
import com.solucionesmoviles.maquinarias.data.local.entity.LineaProduccionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LineaProduccionDao {

    @Query("SELECT * FROM tbl_linea_produccion ORDER BY CAST(cod_linea AS INTEGER) ASC, cod_linea ASC")
    fun getAllOrderByCodigo(): Flow<List<LineaProduccionEntity>>

    @Query("SELECT * FROM tbl_linea_produccion ORDER BY nom_linea COLLATE NOCASE ASC")
    fun getAllOrderByNombre(): Flow<List<LineaProduccionEntity>>

    @Query("SELECT * FROM tbl_linea_produccion WHERE estado_registro = 'A' ORDER BY CAST(cod_linea AS INTEGER) ASC, cod_linea ASC")
    fun getActiveOnlyOrderByNombre(): Flow<List<LineaProduccionEntity>>

    @Query("SELECT * FROM tbl_linea_produccion WHERE cod_linea = :codigo LIMIT 1")
    suspend fun getByCodigo(codigo: String): LineaProduccionEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(linea: LineaProduccionEntity)

    @Update
    suspend fun update(linea: LineaProduccionEntity)

    @Query("UPDATE tbl_linea_produccion SET estado_registro = 'I' WHERE cod_linea = :codigo")
    suspend fun inactivar(codigo: String)

    @Query("UPDATE tbl_linea_produccion SET estado_registro = 'A' WHERE cod_linea = :codigo")
    suspend fun reactivar(codigo: String)

    @Query("UPDATE tbl_linea_produccion SET estado_registro = '*' WHERE cod_linea = :codigo")
    suspend fun eliminarLogico(codigo: String)
}
