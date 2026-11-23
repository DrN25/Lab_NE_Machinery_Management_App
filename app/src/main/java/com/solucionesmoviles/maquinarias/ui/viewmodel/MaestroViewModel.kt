package com.solucionesmoviles.maquinarias.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.solucionesmoviles.maquinarias.data.repository.MachineryRepository
import com.solucionesmoviles.maquinarias.ui.theme.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ModuloTipo(
    val titulo: String,
    val tituloCorto: String,
    val singular: String,
    val articuloSingular: String,
    val primaryColor: Color,
    val containerColor: Color,
    val selectedBgColor: Color
) {
    MAQUINARIAS(
        titulo = "Maestro de Maquinarias",
        tituloCorto = "Maquinarias",
        singular = "maquinaria",
        articuloSingular = "la maquinaria",
        primaryColor = MaquinariasPrimary,
        containerColor = MaquinariasContainer,
        selectedBgColor = MaquinariasSelectedBg
    ),
    PLANTAS(
        titulo = "Plantas de Producción",
        tituloCorto = "Plantas",
        singular = "planta",
        articuloSingular = "la planta",
        primaryColor = PlantasPrimary,
        containerColor = PlantasContainer,
        selectedBgColor = PlantasSelectedBg
    ),
    LINEAS(
        titulo = "Líneas de Producción",
        tituloCorto = "Líneas",
        singular = "línea",
        articuloSingular = "la línea",
        primaryColor = LineasPrimary,
        containerColor = LineasContainer,
        selectedBgColor = LineasSelectedBg
    ),
    UBICACIONES(
        titulo = "Ubicación en la Línea",
        tituloCorto = "Ubicaciones",
        singular = "ubicación",
        articuloSingular = "la ubicación",
        primaryColor = UbicacionesPrimary,
        containerColor = UbicacionesContainer,
        selectedBgColor = UbicacionesSelectedBg
    )
}

enum class TipoReferencia {
    LINEA, PLANTA, UBICACION
}

enum class RolUsuario(
    val nombreMostrar: String,
    val descripcionPermisos: String,
    val puedeCrear: Boolean,
    val puedeModificar: Boolean,
    val puedeCambiarEstado: Boolean,
    val puedeEliminar: Boolean,
    val estadosAdmitidos: Set<String>,
    val badgeColor: Color
) {
    ADMIN(
        nombreMostrar = "Administrador",
        descripcionPermisos = "Control total: crear, modificar, inactivar, reactivar y eliminar.",
        puedeCrear = true,
        puedeModificar = true,
        puedeCambiarEstado = true,
        puedeEliminar = true,
        estadosAdmitidos = setOf("A", "I", "*"),
        badgeColor = MaquinariasPrimary
    ),
    GERENTE(
        nombreMostrar = "Gerente de Planta",
        descripcionPermisos = "Puede crear, modificar, inactivar y reactivar. No puede eliminar.",
        puedeCrear = true,
        puedeModificar = true,
        puedeCambiarEstado = true,
        puedeEliminar = false,
        estadosAdmitidos = setOf("A", "I"),
        badgeColor = PlantasPrimary
    ),
    EMPLEADO(
        nombreMostrar = "Empleado / Operario",
        descripcionPermisos = "Puede consultar, crear y modificar activos. No cambia estados ni elimina.",
        puedeCrear = true,
        puedeModificar = true,
        puedeCambiarEstado = false,
        puedeEliminar = false,
        estadosAdmitidos = setOf("A"),
        badgeColor = LineasPrimary
    )
}

data class CatalogoReferencia(
    val codigo: String,
    val nombre: String
)

data class RegistroItem(
    val codigo: String,
    val nombre: String,
    val estadoRegistro: String,
    val codLinea: String? = null,
    val nomLinea: String? = null,
    val codPlanta: String? = null,
    val nomPlanta: String? = null,
    val codUbicacion: String? = null,
    val nomUbicacion: String? = null
)

sealed class DialogoConfirmacion {
    data class Inactivar(val item: RegistroItem, val modulo: ModuloTipo) : DialogoConfirmacion()
    data class Eliminar(val item: RegistroItem, val modulo: ModuloTipo) : DialogoConfirmacion()
    data class Reactivar(val item: RegistroItem, val modulo: ModuloTipo) : DialogoConfirmacion()
    object Salir : DialogoConfirmacion()
}

data class MaestroUiState(
    val rolActual: RolUsuario? = null, // null = Pantalla de Login Simulado (selección de rol)
    val moduloActual: ModuloTipo = ModuloTipo.MAQUINARIAS,
    val registros: List<RegistroItem> = emptyList(),
    val filtroEstado: String = "A", // Por defecto "A" (Activos); opciones: "A", "I", "*", "TODOS"
    val textoBusqueda: String = "",
    val ordenarPorNombre: Boolean = false,
    val registroSeleccionado: RegistroItem? = null,
    val mostrarSheetRegistro: Boolean = false,
    val modoEdicion: Boolean = false,
    val codigoSugerido: String = "1",
    val mensajeError: String? = null,
    val dialogoConfirmacion: DialogoConfirmacion? = null,
    val lineasActivas: List<CatalogoReferencia> = emptyList(),
    val plantasActivas: List<CatalogoReferencia> = emptyList(),
    val ubicacionesActivas: List<CatalogoReferencia> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class MaestroViewModel(
    private val repository: MachineryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaestroUiState())
    val uiState: StateFlow<MaestroUiState> = _uiState.asStateFlow()

    init {
        observarRegistrosDelModulo()
        observarReferenciasActivas()
    }

    private fun observarRegistrosDelModulo() {
        viewModelScope.launch {
            _uiState
                .map { it.moduloActual to it.ordenarPorNombre }
                .distinctUntilChanged()
                .flatMapLatest { (modulo, porNombre) ->
                    when (modulo) {
                        ModuloTipo.MAQUINARIAS -> repository.getMaquinarias(porNombre).map { lista ->
                            lista.map {
                                RegistroItem(
                                    codigo = it.codMaquinaria,
                                    nombre = it.nomMaquinaria,
                                    estadoRegistro = it.estadoRegistro,
                                    codLinea = it.codLinea,
                                    nomLinea = it.nomLinea,
                                    codPlanta = it.codPlanta,
                                    nomPlanta = it.nomPlanta,
                                    codUbicacion = it.codUbicacion,
                                    nomUbicacion = it.nomUbicacion
                                )
                            }
                        }
                        ModuloTipo.PLANTAS -> repository.getPlantas(porNombre).map { lista ->
                            lista.map { RegistroItem(it.codPlanta, it.nomPlanta, it.estadoRegistro) }
                        }
                        ModuloTipo.LINEAS -> repository.getLineas(porNombre).map { lista ->
                            lista.map { RegistroItem(it.codLinea, it.nomLinea, it.estadoRegistro) }
                        }
                        ModuloTipo.UBICACIONES -> repository.getUbicaciones(porNombre).map { lista ->
                            lista.map { RegistroItem(it.codUbicacion, it.nomUbicacion, it.estadoRegistro) }
                        }
                    }
                }
                .collect { lista ->
                    _uiState.update { state ->
                        // Mantener sincronizado el registro seleccionado si sigue existiendo
                        val actualizado = state.registroSeleccionado?.let { sel ->
                            lista.find { it.codigo == sel.codigo }
                        }
                        state.copy(
                            registros = lista,
                            registroSeleccionado = actualizado,
                            codigoSugerido = calcularSiguienteCodigoLibre(lista)
                        )
                    }
                }
        }
    }

    private fun calcularSiguienteCodigoLibre(lista: List<RegistroItem>): String {
        val usados = lista.mapNotNull { it.codigo.toIntOrNull() }.toHashSet()
        for (n in 1..9999) {
            if (n !in usados) return n.toString()
        }
        return ""
    }

    private fun observarReferenciasActivas() {
        viewModelScope.launch {
            repository.getActiveLineas().collect { lista ->
                _uiState.update {
                    it.copy(lineasActivas = lista.map { l -> CatalogoReferencia(l.codLinea, l.nomLinea) })
                }
            }
        }
        viewModelScope.launch {
            repository.getActivePlantas().collect { lista ->
                _uiState.update {
                    it.copy(plantasActivas = lista.map { p -> CatalogoReferencia(p.codPlanta, p.nomPlanta) })
                }
            }
        }
        viewModelScope.launch {
            repository.getActiveUbicaciones().collect { lista ->
                _uiState.update {
                    it.copy(ubicacionesActivas = lista.map { u -> CatalogoReferencia(u.codUbicacion, u.nomUbicacion) })
                }
            }
        }
    }

    fun iniciarSesion(rol: RolUsuario) {
        _uiState.update {
            it.copy(
                rolActual = rol,
                moduloActual = ModuloTipo.MAQUINARIAS,
                filtroEstado = "A",
                textoBusqueda = "",
                registroSeleccionado = null,
                mostrarSheetRegistro = false,
                mensajeError = null,
                dialogoConfirmacion = null
            )
        }
    }

    fun cerrarSesion() {
        _uiState.update {
            it.copy(
                rolActual = null,
                registroSeleccionado = null,
                mostrarSheetRegistro = false,
                mensajeError = null,
                dialogoConfirmacion = null
            )
        }
    }

    fun cambiarModulo(modulo: ModuloTipo) {
        if (_uiState.value.moduloActual == modulo) return
        _uiState.update {
            it.copy(
                moduloActual = modulo,
                filtroEstado = "A",
                textoBusqueda = "",
                registroSeleccionado = null,
                mostrarSheetRegistro = false,
                mensajeError = null,
                dialogoConfirmacion = null
            )
        }
    }

    fun abrirAdicion() {
        _uiState.update {
            it.copy(
                registroSeleccionado = null,
                mostrarSheetRegistro = true,
                modoEdicion = false,
                mensajeError = null
            )
        }
    }

    fun abrirModificacion(item: RegistroItem) {
        _uiState.update {
            it.copy(
                registroSeleccionado = item,
                mostrarSheetRegistro = true,
                modoEdicion = true,
                mensajeError = null
            )
        }
    }

    fun guardar(
        codigoRaw: String,
        nombreRaw: String,
        codLinea: String = "",
        codPlanta: String = "",
        codUbicacion: String = ""
    ) {
        val modulo = _uiState.value.moduloActual
        val modoEdicion = _uiState.value.modoEdicion

        val codigoTrim = codigoRaw.trim()
        val nombreTrim = nombreRaw.trim()

        // Validaciones obligatorias de código numérico sin prefijo (máx. 4 dígitos)
        if (codigoTrim.isEmpty()) {
            _uiState.update { it.copy(mensajeError = "Debe ingresar un código numérico.") }
            return
        }
        if (!codigoTrim.all { it.isDigit() }) {
            _uiState.update { it.copy(mensajeError = "El código debe contener solamente números.") }
            return
        }
        if (codigoTrim.length > 4) {
            _uiState.update { it.copy(mensajeError = "El código no puede exceder los 4 dígitos (máximo 9999).") }
            return
        }
        val codigoNumero = codigoTrim.toIntOrNull()
        if (codigoNumero == null || codigoNumero <= 0) {
            _uiState.update { it.copy(mensajeError = "El código debe ser un número mayor a 0 (entre 1 y 9999).") }
            return
        }
        val codigoFinal = if (modoEdicion) codigoTrim else codigoNumero.toString()

        // Validación de nombre
        if (nombreTrim.isEmpty()) {
            _uiState.update { it.copy(mensajeError = "Debe ingresar el nombre o descripción.") }
            return
        }
        if (nombreTrim.length < 2) {
            _uiState.update { it.copy(mensajeError = "El nombre debe tener al menos 2 caracteres.") }
            return
        }

        // Validaciones referenciales para Maquinaria
        if (modulo == ModuloTipo.MAQUINARIAS) {
            if (codLinea.isBlank()) {
                _uiState.update { it.copy(mensajeError = "Debe seleccionar una Línea de Producción activa.") }
                return
            }
            if (codPlanta.isBlank()) {
                _uiState.update { it.copy(mensajeError = "Debe seleccionar una Planta de Producción activa.") }
                return
            }
            if (codUbicacion.isBlank()) {
                _uiState.update { it.copy(mensajeError = "Debe seleccionar una Ubicación en la línea activa.") }
                return
            }
        }

        viewModelScope.launch {
            try {
                if (!modoEdicion) {
                    val existe = when (modulo) {
                        ModuloTipo.MAQUINARIAS -> repository.existsMaquinaria(codigoFinal)
                        ModuloTipo.PLANTAS -> repository.existsPlanta(codigoFinal)
                        ModuloTipo.LINEAS -> repository.existsLinea(codigoFinal)
                        ModuloTipo.UBICACIONES -> repository.existsUbicacion(codigoFinal)
                    }
                    if (existe) {
                        _uiState.update {
                            it.copy(mensajeError = "El código $codigoFinal ya existe. Por favor ingrese otro número de código.")
                        }
                        return@launch
                    }
                    when (modulo) {
                        ModuloTipo.MAQUINARIAS -> repository.insertMaquinaria(codigoFinal, nombreTrim, codLinea, codPlanta, codUbicacion)
                        ModuloTipo.PLANTAS -> repository.insertPlanta(codigoFinal, nombreTrim)
                        ModuloTipo.LINEAS -> repository.insertLinea(codigoFinal, nombreTrim)
                        ModuloTipo.UBICACIONES -> repository.insertUbicacion(codigoFinal, nombreTrim)
                    }
                } else {
                    when (modulo) {
                        ModuloTipo.MAQUINARIAS -> repository.updateMaquinaria(codigoFinal, nombreTrim, codLinea, codPlanta, codUbicacion)
                        ModuloTipo.PLANTAS -> repository.updatePlanta(codigoFinal, nombreTrim)
                        ModuloTipo.LINEAS -> repository.updateLinea(codigoFinal, nombreTrim)
                        ModuloTipo.UBICACIONES -> repository.updateUbicacion(codigoFinal, nombreTrim)
                    }
                }
                cerrarSheet()
                deseleccionar()
            } catch (e: Exception) {
                _uiState.update { it.copy(mensajeError = "Error al guardar en base de datos: ${e.message}") }
            }
        }
    }

    fun solicitarInactivar(item: RegistroItem) {
        _uiState.update { it.copy(dialogoConfirmacion = DialogoConfirmacion.Inactivar(item, it.moduloActual)) }
    }

    fun solicitarEliminar(item: RegistroItem) {
        _uiState.update { it.copy(dialogoConfirmacion = DialogoConfirmacion.Eliminar(item, it.moduloActual)) }
    }

    fun solicitarReactivar(item: RegistroItem) {
        _uiState.update { it.copy(dialogoConfirmacion = DialogoConfirmacion.Reactivar(item, it.moduloActual)) }
    }

    fun solicitarSalir() {
        _uiState.update { it.copy(dialogoConfirmacion = DialogoConfirmacion.Salir) }
    }

    fun descartarDialogo() {
        _uiState.update { it.copy(dialogoConfirmacion = null) }
    }

    fun confirmarInactivar(codigo: String, modulo: ModuloTipo) {
        if (_uiState.value.rolActual?.puedeCambiarEstado != true) {
            descartarDialogo()
            return
        }
        viewModelScope.launch {
            when (modulo) {
                ModuloTipo.MAQUINARIAS -> repository.inactivarMaquinaria(codigo)
                ModuloTipo.PLANTAS -> repository.inactivarPlanta(codigo)
                ModuloTipo.LINEAS -> repository.inactivarLinea(codigo)
                ModuloTipo.UBICACIONES -> repository.inactivarUbicacion(codigo)
            }
            descartarDialogo()
            deseleccionar()
        }
    }

    fun confirmarReactivar(codigo: String, modulo: ModuloTipo) {
        if (_uiState.value.rolActual?.puedeCambiarEstado != true) {
            descartarDialogo()
            return
        }
        viewModelScope.launch {
            when (modulo) {
                ModuloTipo.MAQUINARIAS -> repository.reactivarMaquinaria(codigo)
                ModuloTipo.PLANTAS -> repository.reactivarPlanta(codigo)
                ModuloTipo.LINEAS -> repository.reactivarLinea(codigo)
                ModuloTipo.UBICACIONES -> repository.reactivarUbicacion(codigo)
            }
            descartarDialogo()
            deseleccionar()
        }
    }

    fun confirmarEliminar(codigo: String, modulo: ModuloTipo) {
        if (_uiState.value.rolActual?.puedeEliminar != true) {
            descartarDialogo()
            return
        }
        viewModelScope.launch {
            when (modulo) {
                ModuloTipo.MAQUINARIAS -> repository.eliminarMaquinaria(codigo)
                ModuloTipo.PLANTAS -> repository.eliminarPlanta(codigo)
                ModuloTipo.LINEAS -> repository.eliminarLinea(codigo)
                ModuloTipo.UBICACIONES -> repository.eliminarUbicacion(codigo)
            }
            descartarDialogo()
            deseleccionar()
        }
    }

    fun setBusqueda(query: String) {
        _uiState.update { it.copy(textoBusqueda = query) }
    }

    fun setOrdenPorNombre(ordenPorNombre: Boolean) {
        _uiState.update { it.copy(ordenarPorNombre = ordenPorNombre) }
    }

    fun setFiltroEstado(estado: String) {
        _uiState.update { it.copy(filtroEstado = estado) }
    }

    fun seleccionarRegistro(item: RegistroItem) {
        if (_uiState.value.registroSeleccionado?.codigo == item.codigo) {
            deseleccionar()
        } else {
            _uiState.update { it.copy(registroSeleccionado = item) }
        }
    }

    fun deseleccionar() {
        _uiState.update { it.copy(registroSeleccionado = null) }
    }

    fun cerrarSheet() {
        _uiState.update { it.copy(mostrarSheetRegistro = false, mensajeError = null) }
    }
}

class MaestroViewModelFactory(
    private val repository: MachineryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MaestroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MaestroViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
