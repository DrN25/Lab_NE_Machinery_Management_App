package com.solucionesmoviles.maquinarias.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de persistencia local en SQLite para la tabla referencial: Línea de Producción.
 */
@Entity(tableName = "tbl_linea_produccion")
data class LineaProduccionEntity(
    @PrimaryKey
    @ColumnInfo(name = "cod_linea")
    val codLinea: String,

    @ColumnInfo(name = "nom_linea")
    val nomLinea: String,

    @ColumnInfo(name = "estado_registro", defaultValue = "A")
    val estadoRegistro: String = "A"
)
