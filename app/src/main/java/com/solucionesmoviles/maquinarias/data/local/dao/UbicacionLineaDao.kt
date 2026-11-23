package com.solucionesmoviles.maquinarias.data.local.dao

import androidx.room.*
import com.solucionesmoviles.maquinarias.data.local.entity.UbicacionLineaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UbicacionLineaDao {

    @Query("SELECT * FROM tbl_ubicacion_linea ORDER BY CAST(cod_ubicacion AS INTEGER) ASC, cod_ubicacion ASC")
    fun getAllOrderByCodigo(): Flow<List<UbicacionLineaEntity>>

    @Query("SELECT * FROM tbl_ubicacion_linea ORDER BY nom_ubicacion COLLATE NOCASE ASC")
    fun getAllOrderByNombre(): Flow<List<UbicacionLineaEntity>>

    @Query("SELECT * FROM tbl_ubicacion_linea WHERE estado_registro = 'A' ORDER BY nom_ubicacion COLLATE NOCASE ASC")
    fun getActiveOnlyOrderByNombre(): Flow<List<UbicacionLineaEntity>>

    @Query("SELECT * FROM tbl_ubicacion_linea WHERE cod_ubicacion = :codigo LIMIT 1")
    suspend fun getByCodigo(codigo: String): UbicacionLineaEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(ubicacion: UbicacionLineaEntity)

    @Update
    suspend fun update(ubicacion: UbicacionLineaEntity)

    @Query("UPDATE tbl_ubicacion_linea SET estado_registro = 'I' WHERE cod_ubicacion = :codigo")
    suspend fun inactivar(codigo: String)

    @Query("UPDATE tbl_ubicacion_linea SET estado_registro = 'A' WHERE cod_ubicacion = :codigo")
    suspend fun reactivar(codigo: String)

    @Query("UPDATE tbl_ubicacion_linea SET estado_registro = '*' WHERE cod_ubicacion = :codigo")
    suspend fun eliminarLogico(codigo: String)
}
