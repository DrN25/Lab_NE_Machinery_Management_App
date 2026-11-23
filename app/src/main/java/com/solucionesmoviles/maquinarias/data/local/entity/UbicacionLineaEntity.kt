package com.solucionesmoviles.maquinarias.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de persistencia local en SQLite para la tabla referencial: Ubicación en la Línea.
 */
@Entity(tableName = "tbl_ubicacion_linea")
data class UbicacionLineaEntity(
    @PrimaryKey
    @ColumnInfo(name = "cod_ubicacion")
    val codUbicacion: String,

    @ColumnInfo(name = "nom_ubicacion")
    val nomUbicacion: String,

    @ColumnInfo(name = "estado_registro", defaultValue = "A")
    val estadoRegistro: String = "A"
)
