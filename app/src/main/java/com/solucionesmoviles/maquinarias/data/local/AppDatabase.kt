package com.solucionesmoviles.maquinarias.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.solucionesmoviles.maquinarias.data.local.dao.PlantaProduccionDao
import com.solucionesmoviles.maquinarias.data.local.entity.PlantaProduccionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [PlantaProduccionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun plantaProduccionDao(): PlantaProduccionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "maquinarias_textil.db"
                ).addCallback(DatabaseCallback(scope)).build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        val dao = database.plantaProduccionDao()
                        // Datos semilla oficiales requeridos en la guía
                        dao.insert(PlantaProduccionEntity("PLN-01", "Planta de Lavado Industrial", "A"))
                        dao.insert(PlantaProduccionEntity("PLN-02", "Planta de Tejido y Confección", "A"))
                        dao.insert(PlantaProduccionEntity("PLN-03", "Planta de Planchado y Acabado", "I"))
                        dao.insert(PlantaProduccionEntity("PLN-04", "Planta de Tintorería y Estampado", "A"))
                        dao.insert(PlantaProduccionEntity("PLN-05", "Planta Antigua de Hilandería", "*"))
                    }
                }
            }
        }
    }
}
