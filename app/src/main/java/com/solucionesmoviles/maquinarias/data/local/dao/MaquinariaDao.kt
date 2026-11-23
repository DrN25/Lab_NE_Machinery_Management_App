package com.solucionesmoviles.maquinarias.data.local.dao

import androidx.room.*
import com.solucionesmoviles.maquinarias.data.local.entity.MaquinariaDetalle
import com.solucionesmoviles.maquinarias.data.local.entity.MaquinariaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaquinariaDao {

    @Query(
        """
        SELECT m.cod_maquinaria, m.nom_maquinaria, m.cod_linea, l.nom_linea,
               m.cod_planta, p.nom_planta, m.cod_ubicacion, u.nom_ubicacion, m.estado_registro
        FROM tbl_maquinaria m
        LEFT JOIN tbl_linea_produccion l ON m.cod_linea = l.cod_linea
        LEFT JOIN tbl_planta_produccion p ON m.cod_planta = p.cod_planta
        LEFT JOIN tbl_ubicacion_linea u ON m.cod_ubicacion = u.cod_ubicacion
        ORDER BY CAST(m.cod_maquinaria AS INTEGER) ASC, m.cod_maquinaria ASC
        """
    )
    fun getAllDetalleOrderByCodigo(): Flow<List<MaquinariaDetalle>>

    @Query(
        """
        SELECT m.cod_maquinaria, m.nom_maquinaria, m.cod_linea, l.nom_linea,
               m.cod_planta, p.nom_planta, m.cod_ubicacion, u.nom_ubicacion, m.estado_registro
        FROM tbl_maquinaria m
        LEFT JOIN tbl_linea_produccion l ON m.cod_linea = l.cod_linea
        LEFT JOIN tbl_planta_produccion p ON m.cod_planta = p.cod_planta
        LEFT JOIN tbl_ubicacion_linea u ON m.cod_ubicacion = u.cod_ubicacion
        ORDER BY m.nom_maquinaria COLLATE NOCASE ASC
        """
    )
    fun getAllDetalleOrderByNombre(): Flow<List<MaquinariaDetalle>>

    @Query("SELECT * FROM tbl_maquinaria WHERE cod_maquinaria = :codigo LIMIT 1")
    suspend fun getByCodigo(codigo: String): MaquinariaEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(maquinaria: MaquinariaEntity)

    @Update
    suspend fun update(maquinaria: MaquinariaEntity)

    @Query("UPDATE tbl_maquinaria SET estado_registro = 'I' WHERE cod_maquinaria = :codigo")
    suspend fun inactivar(codigo: String)

    @Query("UPDATE tbl_maquinaria SET estado_registro = 'A' WHERE cod_maquinaria = :codigo")
    suspend fun reactivar(codigo: String)

    @Query("UPDATE tbl_maquinaria SET estado_registro = '*' WHERE cod_maquinaria = :codigo")
    suspend fun eliminarLogico(codigo: String)
}
