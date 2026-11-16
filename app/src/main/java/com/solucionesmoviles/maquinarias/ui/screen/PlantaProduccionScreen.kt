package com.solucionesmoviles.maquinarias.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solucionesmoviles.maquinarias.data.local.entity.PlantaProduccionEntity
import com.solucionesmoviles.maquinarias.ui.theme.*
import com.solucionesmoviles.maquinarias.ui.viewmodel.PlantaProduccionViewModel

/**
 * Pantalla de Mantenimiento de Planta de Producción bajo el diseño oficial del Layout P2:
 *  - Barra superior con título y botón Salir.
 *  - Buscador predictivo en vivo con botón para limpiar.
 *  - Conmutador de ordenamiento (Código / Nombre).
 *  - Chips de conteo de estado en tiempo real (Todos, Activos, Inact., Elim.).
 *  - Tarjetas de registros con badges cromáticos semánticos.
 *  - Panel Inferior de Acciones Rápidas (Modificar, Inactivar, Reactivar, Eliminar).
 *  - Modal BottomSheet para Adicionar / Modificar sin perder contexto de la lista.
 *  - Barra de navegación inferior (Maquinarias, Plantas, Líneas, Ubicaciones).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantaProduccionScreen(
    viewModel: PlantaProduccionViewModel,
    onSalir: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Filtrado reactivo en memoria
    val plantasFiltradas = remember(state.plantas, state.filtroEstado, state.textoBusqueda) {
        state.plantas.filter { p ->
            val coincideEstado = when (state.filtroEstado) {
                "A" -> p.estadoRegistro == "A"
                "I" -> p.estadoRegistro == "I"
                "*" -> p.estadoRegistro == "*"
                else -> true
            }
            val coincideTexto = p.nomPlanta.contains(state.textoBusqueda, ignoreCase = true) ||
                                p.codPlanta.contains(state.textoBusqueda, ignoreCase = true)
            coincideEstado && coincideTexto
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planta de Producción", fontWeight = FontWeight.Bold, color = Color.Black) },
                actions = {
                    TextButton(onClick = onSalir) {
                        Text("Salir", color = IndustrialBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Navegar a Maquinarias */ },
                    icon = { Icon(Icons.Default.PrecisionManufacturing, contentDescription = "Maquinarias") },
                    label = { Text("Maquinarias", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Vista actual */ },
                    icon = { Icon(Icons.Default.Factory, contentDescription = "Plantas") },
                    label = { Text("Plantas", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = IndustrialBlue, selectedTextColor = IndustrialBlue)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Navegar a Líneas */ },
                    icon = { Icon(Icons.Default.LinearScale, contentDescription = "Líneas") },
                    label = { Text("Líneas", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Navegar a Ubicaciones */ },
                    icon = { Icon(Icons.Default.Place, contentDescription = "Ubicaciones") },
                    label = { Text("Ubicaciones", fontSize = 11.sp) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(SurfaceBg)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Buscador con ícono de lupa y limpiar
            OutlinedTextField(
                value = state.textoBusqueda,
                onValueChange = { viewModel.setBusqueda(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar código o nombre...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.Gray) },
                trailingIcon = {
                    if (state.textoBusqueda.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setBusqueda("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = Color.Gray)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = IndustrialBlue,
                    unfocusedBorderColor = CardBorder
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Fila de Ordenamiento y Botón Adicionar (Layout P2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("ORDENAR:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    FilterChip(
                        selected = !state.ordenarPorNombre,
                        onClick = { if (state.ordenarPorNombre) viewModel.toggleOrdenamiento() },
                        label = { Text("Código", fontSize = 12.sp) }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    FilterChip(
                        selected = state.ordenarPorNombre,
                        onClick = { if (!state.ordenarPorNombre) viewModel.toggleOrdenamiento() },
                        label = { Text("Nombre", fontSize = 12.sp) }
                    )
                }

                Button(
                    onClick = { viewModel.abrirAdicion() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndustrialBlue),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adicionar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chips Horizontales de Conteo de Estados en Tiempo Real
            val conteoTodos = state.plantas.size
            val conteoActivos = state.plantas.count { it.estadoRegistro == "A" }
            val conteoInactivos = state.plantas.count { it.estadoRegistro == "I" }
            val conteoEliminados = state.plantas.count { it.estadoRegistro == "*" }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = state.filtroEstado == "TODOS",
                    onClick = { viewModel.setFiltroEstado("TODOS") },
                    label = { Text("Todos $conteoTodos", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IndustrialBlue,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = state.filtroEstado == "A",
                    onClick = { viewModel.setFiltroEstado("A") },
                    label = { Text("Activos $conteoActivos", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IndustrialBlue,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = state.filtroEstado == "I",
                    onClick = { viewModel.setFiltroEstado("I") },
                    label = { Text("Inact. $conteoInactivos", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IndustrialBlue,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = state.filtroEstado == "*",
                    onClick = { viewModel.setFiltroEstado("*") },
                    label = { Text("Elim. $conteoEliminados", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IndustrialBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lista Desplazable de Tarjetas
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(plantasFiltradas) { planta ->
                    val isSelected = state.plantaSeleccionada?.codPlanta == planta.codPlanta
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.seleccionarPlanta(planta) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, IndustrialBlue) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = planta.codPlanta,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IndustrialBlue
                                )
                                Text(
                                    text = planta.nomPlanta,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (planta.estadoRegistro == "*") Color.Gray else Color.Black
                                )
                            }
                            EstadoPillBadge(planta.estadoRegistro)
                        }
                    }
                }
            }

            // Panel Inferior de Acciones Rápidas (Propuesta 2)
            state.plantaSeleccionada?.let { seleccionada ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${seleccionada.codPlanta} • ${seleccionada.nomPlanta}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                EstadoPillBadge(seleccionada.estadoRegistro)
                            }
                            IconButton(
                                onClick = { viewModel.deseleccionar() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.abrirModificacion(seleccionada) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = IndustrialContainer,
                                    contentColor = IndustrialBlue
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Modificar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            if (seleccionada.estadoRegistro == "A") {
                                Button(
                                    onClick = { viewModel.inactivar(seleccionada.codPlanta) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = StatusInactiveBg,
                                        contentColor = StatusInactiveText
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Inactivar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (seleccionada.estadoRegistro == "I") {
                                Button(
                                    onClick = { viewModel.reactivar(seleccionada.codPlanta) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = StatusActiveBg,
                                        contentColor = StatusActiveText
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Reactivar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (seleccionada.estadoRegistro != "*") {
                                Button(
                                    onClick = { viewModel.eliminar(seleccionada.codPlanta) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = StatusDeletedBg,
                                        contentColor = StatusDeletedText
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Eliminar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal BottomSheet para Alta y Edición
    if (state.mostrarSheetRegistro) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.cerrarSheet() },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            PlantaFormSheetContent(
                planta = state.plantaSeleccionada,
                modoEdicion = state.modoEdicion,
                error = state.mensajeError,
                onGuardar = { cod, nom -> viewModel.guardar(cod, nom) },
                onCancelar = { viewModel.cerrarSheet() }
            )
        }
    }
}

@Composable
fun EstadoPillBadge(estado: String) {
    val (fondo, texto, etiqueta) = when (estado) {
        "A" -> Triple(StatusActiveBg, StatusActiveText, "A • Activo")
        "I" -> Triple(StatusInactiveBg, StatusInactiveText, "I • Inactivo")
        else -> Triple(StatusDeletedBg, StatusDeletedText, "* • Eliminado")
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = fondo,
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = etiqueta,
            color = texto,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun PlantaFormSheetContent(
    planta: PlantaProduccionEntity?,
    modoEdicion: Boolean,
    error: String?,
    onGuardar: (String, String) -> Unit,
    onCancelar: () -> Unit
) {
    var codigo by remember { mutableStateOf(planta?.codPlanta ?: "") }
    var nombre by remember { mutableStateOf(planta?.nomPlanta ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (modoEdicion) "Modificar Planta de Producción" else "Adicionar Planta de Producción",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color.Black
            )
            TextButton(onClick = onCancelar) {
                Text("Salir", color = Color.Gray, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Campo Código (Bloqueado con candado en modo edición según regla de negocio)
        Text("Código", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = codigo,
            onValueChange = { if (!modoEdicion) codigo = it },
            placeholder = { Text("Ej: PLN-06") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !modoEdicion,
            trailingIcon = {
                if (modoEdicion) Icon(Icons.Default.Lock, contentDescription = "Bloqueado", tint = Color.Gray)
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo Nombre
        Text("Nombre de Planta", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = { Text("Ingrese nombre de planta...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo Estado de Registro
        Text("Estado de Registro", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        EstadoPillBadge(if (modoEdicion) (planta?.estadoRegistro ?: "A") else "A")

        if (error != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancelar", fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = { onGuardar(codigo, nombre) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndustrialBlue)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}
