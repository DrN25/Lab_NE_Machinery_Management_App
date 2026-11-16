package com.solucionesmoviles.maquinarias.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.solucionesmoviles.maquinarias.data.local.entity.PlantaProduccionEntity
import com.solucionesmoviles.maquinarias.data.repository.PlantaProduccionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PlantaUiState(
    val plantas: List<PlantaProduccionEntity> = emptyList(),
    val filtroEstado: String = "TODOS", // "TODOS", "A", "I", "*"
    val textoBusqueda: String = "",
    val ordenarPorNombre: Boolean = false,
    val plantaSeleccionada: PlantaProduccionEntity? = null,
    val mostrarSheetRegistro: Boolean = false,
    val modoEdicion: Boolean = false,
    val mensajeError: String? = null
)

class PlantaProduccionViewModel(
    private val repository: PlantaProduccionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlantaUiState())
    val uiState: StateFlow<PlantaUiState> = _uiState.asStateFlow()

    init {
        cargarPlantas()
    }

    private fun cargarPlantas() {
        viewModelScope.launch {
            _uiState.map { it.ordenarPorNombre }
                .distinctUntilChanged()
                .flatMapLatest { porNombre -> repository.getPlantas(porNombre) }
                .collect { lista ->
                    _uiState.update { it.copy(plantas = lista) }
                }
        }
    }

    // 1. Función Adición (Abre formulario modal en blanco)
    fun abrirAdicion() {
        _uiState.update {
            it.copy(
                plantaSeleccionada = null,
                mostrarSheetRegistro = true,
                modoEdicion = false,
                mensajeError = null
            )
        }
    }

    // 2. Función Modificación (Carga el registro seleccionado en la hoja modal)
    fun abrirModificacion(planta: PlantaProduccionEntity) {
        _uiState.update {
            it.copy(
                plantaSeleccionada = planta,
                mostrarSheetRegistro = true,
                modoEdicion = true,
                mensajeError = null
            )
        }
    }

    // 3. Función Guardar (Valida y persiste en SQLite)
    fun guardar(codigo: String, nombre: String) {
        if (codigo.isBlank() || nombre.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Código y nombre son obligatorios.") }
            return
        }
        viewModelScope.launch {
            try {
                if (_uiState.value.modoEdicion) {
                    repository.updatePlanta(codigo, nombre)
                } else {
                    repository.insertPlanta(codigo, nombre)
                }
                cerrarSheet()
            } catch (e: Exception) {
                _uiState.update { it.copy(mensajeError = "Error al persistir: ${e.message}") }
            }
        }
    }

    // 4. Función Inactivar (Pasa estado a 'I')
    fun inactivar(codigo: String) {
        viewModelScope.launch {
            repository.inactivar(codigo)
            deseleccionar()
        }
    }

    // 5. Función Reactivar (Restaura estado a 'A')
    fun reactivar(codigo: String) {
        viewModelScope.launch {
            repository.reactivar(codigo)
            deseleccionar()
        }
    }

    // 6. Función Eliminación Lógica (Pasa estado a '*')
    fun eliminar(codigo: String) {
        viewModelScope.launch {
            repository.eliminarLogico(codigo)
            deseleccionar()
        }
    }

    // 7. Función Buscar (Filtro en tiempo real)
    fun setBusqueda(query: String) {
        _uiState.update { it.copy(textoBusqueda = query) }
    }

    // Alternar Criterio de Ordenamiento (Código / Nombre)
    fun toggleOrdenamiento() {
        _uiState.update { it.copy(ordenarPorNombre = !it.ordenarPorNombre) }
    }

    // Filtrar por Chips de Estado (Todos, Activos, Inact., Elim.)
    fun setFiltroEstado(estado: String) {
        _uiState.update { it.copy(filtroEstado = estado) }
    }

    // Selección de tarjeta para desplegar el Panel Inferior de Acciones Rápidas (Layout P2)
    fun seleccionarPlanta(planta: PlantaProduccionEntity) {
        _uiState.update { it.copy(plantaSeleccionada = planta) }
    }

    fun deseleccionar() {
        _uiState.update { it.copy(plantaSeleccionada = null) }
    }

    // 8. Función Cancelar
    fun cerrarSheet() {
        _uiState.update { it.copy(mostrarSheetRegistro = false, mensajeError = null) }
    }
}

class PlantaViewModelFactory(
    private val repository: PlantaProduccionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantaProduccionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlantaProduccionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
