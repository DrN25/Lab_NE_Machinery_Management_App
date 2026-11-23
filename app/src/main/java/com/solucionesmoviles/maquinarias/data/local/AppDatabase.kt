package com.solucionesmoviles.maquinarias.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.solucionesmoviles.maquinarias.data.local.dao.LineaProduccionDao
import com.solucionesmoviles.maquinarias.data.local.dao.MaquinariaDao
import com.solucionesmoviles.maquinarias.data.local.dao.PlantaProduccionDao
import com.solucionesmoviles.maquinarias.data.local.dao.UbicacionLineaDao
import com.solucionesmoviles.maquinarias.data.local.entity.LineaProduccionEntity
import com.solucionesmoviles.maquinarias.data.local.entity.MaquinariaEntity
import com.solucionesmoviles.maquinarias.data.local.entity.PlantaProduccionEntity
import com.solucionesmoviles.maquinarias.data.local.entity.UbicacionLineaEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PlantaProduccionEntity::class,
        LineaProduccionEntity::class,
        UbicacionLineaEntity::class,
        MaquinariaEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun plantaProduccionDao(): PlantaProduccionDao
    abstract fun lineaProduccionDao(): LineaProduccionDao
    abstract fun ubicacionLineaDao(): UbicacionLineaDao
    abstract fun maquinariaDao(): MaquinariaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "maquinarias_textil.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                seedDatabase()
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                seedDatabase()
            }

            private fun seedDatabase() {
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        // 1. Plantas de Producción (12 registros reales)
                        val plantaDao = database.plantaProduccionDao()
                        plantaDao.insert(PlantaProduccionEntity("1", "Planta de Lavado", "A"))
                        plantaDao.insert(PlantaProduccionEntity("2", "Planta de Tejido", "A"))
                        plantaDao.insert(PlantaProduccionEntity("3", "Planta de Planchado", "A"))
                        plantaDao.insert(PlantaProduccionEntity("4", "Planta de Tintorería", "A"))
                        plantaDao.insert(PlantaProduccionEntity("5", "Planta de Hilandería", "A"))
                        plantaDao.insert(PlantaProduccionEntity("6", "Planta de Confección", "A"))
                        plantaDao.insert(PlantaProduccionEntity("7", "Planta de Acabados", "A"))
                        plantaDao.insert(PlantaProduccionEntity("8", "Planta de Estampado", "A"))
                        plantaDao.insert(PlantaProduccionEntity("9", "Planta de Corte", "I"))
                        plantaDao.insert(PlantaProduccionEntity("10", "Planta de Control de Calidad", "A"))
                        plantaDao.insert(PlantaProduccionEntity("11", "Planta Antigua Norte", "*"))
                        plantaDao.insert(PlantaProduccionEntity("12", "Planta de Embalaje", "I"))

                        // 2. Líneas de Producción (12 registros reales según guía + adicionales)
                        val lineaDao = database.lineaProduccionDao()
                        lineaDao.insert(LineaProduccionEntity("1", "Lavadora 1", "A"))
                        lineaDao.insert(LineaProduccionEntity("2", "Lavadora 2", "A"))
                        lineaDao.insert(LineaProduccionEntity("3", "Tejido grueso", "A"))
                        lineaDao.insert(LineaProduccionEntity("4", "Tejido fino", "A"))
                        lineaDao.insert(LineaProduccionEntity("5", "Teñido claro", "A"))
                        lineaDao.insert(LineaProduccionEntity("6", "Teñido oscuro", "A"))
                        lineaDao.insert(LineaProduccionEntity("7", "Planchado seco", "A"))
                        lineaDao.insert(LineaProduccionEntity("8", "Planchado vapor", "A"))
                        lineaDao.insert(LineaProduccionEntity("9", "Cardado y Peinado", "A"))
                        lineaDao.insert(LineaProduccionEntity("10", "Estampado rotativo", "I"))
                        lineaDao.insert(LineaProduccionEntity("11", "Corte automático", "I"))
                        lineaDao.insert(LineaProduccionEntity("12", "Lavado manual antiguo", "*"))

                        // 3. Ubicación en la Línea (12 estaciones/posiciones industriales descriptivas sin prefijo numérico)
                        val ubicacionDao = database.ubicacionLineaDao()
                        ubicacionDao.insert(UbicacionLineaEntity("1", "Estación Inicial (Ingreso)", "A"))
                        ubicacionDao.insert(UbicacionLineaEntity("2", "Estación de Preproceso", "A"))
                        ubicacionDao.insert(UbicacionLineaEntity("3", "Estación Central de Operación", "A"))
                        ubicacionDao.insert(UbicacionLineaEntity("4", "Estación de Acabado Intermedio", "A"))
                        ubicacionDao.insert(UbicacionLineaEntity("5", "Estación de Control y Revisión", "A"))
                        ubicacionDao.insert(UbicacionLineaEntity("6", "Estación de Descarga y Salida", "A"))
                        ubicacionDao.insert(UbicacionLineaEntity("7", "Bahía Lateral Derecha A", "A"))
                        ubicacionDao.insert(UbicacionLineaEntity("8", "Bahía Lateral Izquierda B", "A"))
                        ubicacionDao.insert(UbicacionLineaEntity("9", "Zona de Mantenimiento", "I"))
                        ubicacionDao.insert(UbicacionLineaEntity("10", "Módulo Auxiliar de Empaque", "A"))
                        ubicacionDao.insert(UbicacionLineaEntity("11", "Área de Reserva Temporal", "I"))
                        ubicacionDao.insert(UbicacionLineaEntity("12", "Tramo Posterior Clausurado", "*"))

                        // 4. Maestro de Maquinarias (15 equipos textiles con relación lógica Línea-Planta-Ubicación)
                        val maqDao = database.maquinariaDao()
                        maqDao.insert(MaquinariaEntity("1", "Lavadora Industrial 100 kg", "1", "1", "1", "A"))
                        maqDao.insert(MaquinariaEntity("2", "Centrífuga Extractora de Agua", "1", "1", "2", "A"))
                        maqDao.insert(MaquinariaEntity("3", "Lavadora de Tambor 80 kg", "2", "1", "1", "A"))
                        maqDao.insert(MaquinariaEntity("4", "Secadora Rotativa a Gas", "2", "1", "3", "A"))
                        maqDao.insert(MaquinariaEntity("5", "Telar Circular de Punto", "4", "2", "1", "A"))
                        maqDao.insert(MaquinariaEntity("6", "Máquina Rectilínea de Cuellos", "4", "2", "2", "A"))
                        maqDao.insert(MaquinariaEntity("7", "Telar Jacquard Doble Frontura", "3", "2", "1", "A"))
                        maqDao.insert(MaquinariaEntity("8", "Barca de Teñido Jet 200 kg", "5", "4", "1", "A"))
                        maqDao.insert(MaquinariaEntity("9", "Jigger de Tintura Continua", "6", "4", "2", "A"))
                        maqDao.insert(MaquinariaEntity("10", "Calandra de Planchado Seco", "7", "3", "1", "A"))
                        maqDao.insert(MaquinariaEntity("11", "Prensa Vaporizadora Neumática", "8", "3", "2", "A"))
                        maqDao.insert(MaquinariaEntity("12", "Carda Plana de Algodón", "9", "5", "1", "A"))
                        maqDao.insert(MaquinariaEntity("13", "Mesa de Estampado Pulpo 8 Col.", "10", "8", "4", "I"))
                        maqDao.insert(MaquinariaEntity("14", "Cortadora Vertical de Cuchilla", "11", "9", "9", "I"))
                        maqDao.insert(MaquinariaEntity("15", "Tina de Lavado Manual Antigua", "12", "11", "12", "*"))
                    }
                }
            }
        }
    }
}
