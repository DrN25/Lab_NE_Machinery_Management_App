package com.solucionesmoviles.maquinarias.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de persistencia local en SQLite para la tabla referencial: Planta de Producción.
 * Valores de estado_registro:
 *  'A' = Activo (por defecto en inserción)
 *  'I' = Inactivo
 *  '*' = Eliminado (borrado lógico)
 */
@Entity(tableName = "tbl_planta_produccion")
data class PlantaProduccionEntity(
    @PrimaryKey
    @ColumnInfo(name = "cod_planta")
    val codPlanta: String,

    @ColumnInfo(name = "nom_planta")
    val nomPlanta: String,

    @ColumnInfo(name = "estado_registro", defaultValue = "A")
    val estadoRegistro: String = "A"
)
