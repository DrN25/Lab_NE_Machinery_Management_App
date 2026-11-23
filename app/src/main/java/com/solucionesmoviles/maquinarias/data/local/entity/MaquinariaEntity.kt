package com.solucionesmoviles.maquinarias.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad principal en SQLite para el Maestro de Maquinarias (Módulo 12).
 * Referencia a Línea de Producción, Planta de Producción y Ubicación en la Línea.
 */
@Entity(
    tableName = "tbl_maquinaria",
    foreignKeys = [
        ForeignKey(
            entity = LineaProduccionEntity::class,
            parentColumns = ["cod_linea"],
            childColumns = ["cod_linea"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = PlantaProduccionEntity::class,
            parentColumns = ["cod_planta"],
            childColumns = ["cod_planta"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = UbicacionLineaEntity::class,
            parentColumns = ["cod_ubicacion"],
            childColumns = ["cod_ubicacion"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["cod_linea"]),
        Index(value = ["cod_planta"]),
        Index(value = ["cod_ubicacion"])
    ]
)
data class MaquinariaEntity(
    @PrimaryKey
    @ColumnInfo(name = "cod_maquinaria")
    val codMaquinaria: String,

    @ColumnInfo(name = "nom_maquinaria")
    val nomMaquinaria: String,

    @ColumnInfo(name = "cod_linea")
    val codLinea: String,

    @ColumnInfo(name = "cod_planta")
    val codPlanta: String,

    @ColumnInfo(name = "cod_ubicacion")
    val codUbicacion: String,

    @ColumnInfo(name = "estado_registro", defaultValue = "A")
    val estadoRegistro: String = "A"
)

/**
 * Proyección con nombres resueltos de las tablas referenciales para mostrar en UI.
 */
data class MaquinariaDetalle(
    @ColumnInfo(name = "cod_maquinaria") val codMaquinaria: String,
    @ColumnInfo(name = "nom_maquinaria") val nomMaquinaria: String,
    @ColumnInfo(name = "cod_linea") val codLinea: String,
    @ColumnInfo(name = "nom_linea") val nomLinea: String?,
    @ColumnInfo(name = "cod_planta") val codPlanta: String,
    @ColumnInfo(name = "nom_planta") val nomPlanta: String?,
    @ColumnInfo(name = "cod_ubicacion") val codUbicacion: String,
    @ColumnInfo(name = "nom_ubicacion") val nomUbicacion: String?,
    @ColumnInfo(name = "estado_registro") val estadoRegistro: String
)
