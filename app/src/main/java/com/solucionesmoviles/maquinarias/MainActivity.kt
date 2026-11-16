package com.solucionesmoviles.maquinarias

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.solucionesmoviles.maquinarias.data.local.AppDatabase
import com.solucionesmoviles.maquinarias.data.repository.PlantaProduccionRepository
import com.solucionesmoviles.maquinarias.ui.screen.PlantaProduccionScreen
import com.solucionesmoviles.maquinarias.ui.theme.MaestroMaquinariasTheme
import com.solucionesmoviles.maquinarias.ui.viewmodel.PlantaProduccionViewModel
import com.solucionesmoviles.maquinarias.ui.viewmodel.PlantaViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: PlantaProduccionViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = PlantaProduccionRepository(db.plantaProduccionDao())
        PlantaViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaestroMaquinariasTheme {
                PlantaProduccionScreen(
                    viewModel = viewModel,
                    onSalir = {
                        Toast.makeText(this, "Saliendo del módulo...", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                )
            }
        }
    }
}
