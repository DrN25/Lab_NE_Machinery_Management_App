package com.solucionesmoviles.maquinarias

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.solucionesmoviles.maquinarias.data.local.AppDatabase
import com.solucionesmoviles.maquinarias.data.repository.MachineryRepository
import com.solucionesmoviles.maquinarias.ui.screen.MaestroScreen
import com.solucionesmoviles.maquinarias.ui.theme.MaestroMaquinariasTheme
import com.solucionesmoviles.maquinarias.ui.viewmodel.MaestroViewModel
import com.solucionesmoviles.maquinarias.ui.viewmodel.MaestroViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: MaestroViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = MachineryRepository(
            plantaDao = db.plantaProduccionDao(),
            lineaDao = db.lineaProduccionDao(),
            ubicacionDao = db.ubicacionLineaDao(),
            maquinariaDao = db.maquinariaDao()
        )
        MaestroViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MaestroMaquinariasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8FAFC)
                ) {
                    MaestroScreen(
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
}
