package com.solucionesmoviles.maquinarias.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solucionesmoviles.maquinarias.ui.theme.CardBorder
import com.solucionesmoviles.maquinarias.ui.theme.SurfaceBg
import com.solucionesmoviles.maquinarias.ui.viewmodel.MaestroViewModel
import com.solucionesmoviles.maquinarias.ui.viewmodel.ModuloTipo
import com.solucionesmoviles.maquinarias.ui.viewmodel.RegistroItem
import com.solucionesmoviles.maquinarias.ui.viewmodel.TipoReferencia

/**
 * Pantalla unificada para los 4 módulos del Módulo 12:
 * Maestro de Maquinarias, Plantas de Producción, Líneas de Producción y Ubicación en la Línea.
 * Diseñada bajo Layout Propuesta 2 (P2) con ergonomía accesible (adultos mayores) y paleta distintiva por módulo.
 */
@Composable
fun MaestroScreen(
    viewModel: MaestroViewModel,
    onSalir: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val modulo = state.moduloActual
    val estadosAdmitidos = state.rolActual?.estadosAdmitidos ?: setOf("A")

    // Primero restringir registros a los estados permitidos para el rol actual
    val registrosAdmitidos = remember(state.registros, estadosAdmitidos) {
        state.registros.filter { it.estadoRegistro in estadosAdmitidos }
    }

    // Filtrado reactivo en memoria (Estado seleccionado + Búsqueda por código o nombre)
    val registrosFiltrados = remember(registrosAdmitidos, state.filtroEstado, state.textoBusqueda) {
        registrosAdmitidos.filter { item ->
            val coincideEstado = when (state.filtroEstado) {
                "A" -> item.estadoRegistro == "A"
                "I" -> item.estadoRegistro == "I"
                "*" -> item.estadoRegistro == "*"
                else -> true
            }
            val coincideTexto = item.nombre.contains(state.textoBusqueda, ignoreCase = true) ||
                item.codigo.contains(state.textoBusqueda, ignoreCase = true)
            coincideEstado && coincideTexto
        }
    }

    var lookupActivo by remember { mutableStateOf<TipoReferencia?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Surface(
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
                        // Franja sutil superior del color del módulo para identificación inmediata
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(modulo.primaryColor)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = modulo.containerColor,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = iconoModulo(modulo),
                                            contentDescription = null,
                                            tint = modulo.primaryColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = modulo.titulo,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    state.rolActual?.let { rol ->
                                        Text(
                                            text = "Perfil: ${rol.nombreMostrar}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = modulo.primaryColor,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            SalirButton(
                                primaryColor = modulo.primaryColor,
                                onClick = { viewModel.solicitarSalir() }
                            )
                        }
                    }
                }
            },
            bottomBar = {
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 0.dp,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        ModuloTipo.entries.forEach { tab ->
                            val selected = modulo == tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { viewModel.cambiarModulo(tab) },
                                icon = {
                                    Icon(
                                        imageVector = iconoModulo(tab),
                                        contentDescription = tab.tituloCorto
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.tituloCorto,
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = tab.primaryColor,
                                    selectedTextColor = tab.primaryColor,
                                    indicatorColor = tab.containerColor,
                                    unselectedIconColor = Color(0xFF64748B),
                                    unselectedTextColor = Color(0xFF64748B)
                                )
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(SurfaceBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // 1. Buscador Predictivo en Tiempo Real
                    OutlinedTextField(
                        value = state.textoBusqueda,
                        onValueChange = { viewModel.setBusqueda(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Buscar por código o nombre...",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color(0xFF64748B))
                        },
                        trailingIcon = {
                            if (state.textoBusqueda.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setBusqueda("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = Color(0xFF64748B))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = modulo.primaryColor,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Fila compacta de Conmutador de Ordenamiento ("Ordenar:") y Botón + Adicionar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Text(
                                text = "Ordenar:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569),
                                maxLines = 1,
                                softWrap = false
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(if (!state.ordenarPorNombre) modulo.primaryColor else Color.Transparent)
                                            .clickable { viewModel.setOrdenPorNombre(false) }
                                            .padding(horizontal = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Código",
                                            fontSize = 12.sp,
                                            fontWeight = if (!state.ordenarPorNombre) FontWeight.Bold else FontWeight.Medium,
                                            color = if (!state.ordenarPorNombre) Color.White else Color(0xFF64748B),
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(if (state.ordenarPorNombre) modulo.primaryColor else Color.Transparent)
                                            .clickable { viewModel.setOrdenPorNombre(true) }
                                            .padding(horizontal = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Nombre",
                                            fontSize = 12.sp,
                                            fontWeight = if (state.ordenarPorNombre) FontWeight.Bold else FontWeight.Medium,
                                            color = if (state.ordenarPorNombre) Color.White else Color(0xFF64748B),
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { viewModel.abrirAdicion() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = modulo.primaryColor),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Adicionar", fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Chips Horizontales de Filtrado por Estado (solo estados admitidos por el rol)
                    val (conteoTodos, conteoActivos, conteoInactivos, conteoEliminados) = remember(registrosAdmitidos) {
                        var activos = 0
                        var inactivos = 0
                        var eliminados = 0
                        for (item in registrosAdmitidos) {
                            when (item.estadoRegistro) {
                                "A" -> activos++
                                "I" -> inactivos++
                                "*" -> eliminados++
                            }
                        }
                        listOf(registrosAdmitidos.size, activos, inactivos, eliminados)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FiltroChipItem(
                            texto = "Activos $conteoActivos",
                            seleccionado = state.filtroEstado == "A",
                            primaryColor = modulo.primaryColor,
                            onClick = { viewModel.setFiltroEstado("A") }
                        )
                        if ("I" in estadosAdmitidos) {
                            FiltroChipItem(
                                texto = "Inact. $conteoInactivos",
                                seleccionado = state.filtroEstado == "I",
                                primaryColor = modulo.primaryColor,
                                onClick = { viewModel.setFiltroEstado("I") }
                            )
                        }
                        if ("*" in estadosAdmitidos) {
                            FiltroChipItem(
                                texto = "Elim. $conteoEliminados",
                                seleccionado = state.filtroEstado == "*",
                                primaryColor = modulo.primaryColor,
                                onClick = { viewModel.setFiltroEstado("*") }
                            )
                        }
                        if (estadosAdmitidos.size > 1) {
                            FiltroChipItem(
                                texto = "Todos $conteoTodos",
                                seleccionado = state.filtroEstado == "TODOS",
                                primaryColor = modulo.primaryColor,
                                onClick = { viewModel.setFiltroEstado("TODOS") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. Lista Desplazable de Tarjetas
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = if (state.registroSeleccionado != null) 156.dp else 16.dp)
                    ) {
                        if (registrosFiltrados.isEmpty()) {
                            item {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, CardBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(22.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SearchOff,
                                            contentDescription = null,
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        if (state.textoBusqueda.isNotBlank()) {
                                            val textoLimpio = state.textoBusqueda.trim()
                                            Text(
                                                text = "No se encontraron coincidencias con \"$textoLimpio\"",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF0F172A),
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "¿Quiere crear un nuevo registro con ese nombre?",
                                                fontSize = 13.sp,
                                                color = Color(0xFF64748B),
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(14.dp))
                                            Button(
                                                onClick = { viewModel.abrirAdicion() },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = modulo.primaryColor),
                                                modifier = Modifier.height(44.dp)
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Crear \"${textoLimpio.take(22)}\"",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = "No se encontraron registros",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF475569)
                                            )
                                            Text(
                                                text = "Pruebe cambiar el filtro o presione \"+ Adicionar\".",
                                                fontSize = 13.sp,
                                                color = Color(0xFF94A3B8),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            items(
                                items = registrosFiltrados,
                                key = { it.codigo },
                                contentType = { "registro_card" }
                            ) { item ->
                                val isSelected = state.registroSeleccionado?.codigo == item.codigo
                                Card(
                                    onClick = { viewModel.seleccionarRegistro(item) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) modulo.selectedBgColor else Color.White
                                    ),
                                    border = if (isSelected) BorderStroke(1.8.dp, modulo.primaryColor) else BorderStroke(1.dp, CardBorder),
                                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Código numérico a la izquierda (ancho mínimo 42dp, adaptable hasta 4 dígitos)
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = modulo.containerColor,
                                            modifier = Modifier.widthIn(min = 42.dp)
                                        ) {
                                            Text(
                                                text = item.codigo,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = modulo.primaryColor,
                                                textAlign = TextAlign.Center,
                                                maxLines = 1,
                                                softWrap = false,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                            )
                                        }

                                        // Nombre y detalle referencial en el centro
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.nombre,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (item.estadoRegistro == "*") Color(0xFF94A3B8) else Color(0xFF0F172A),
                                                textDecoration = if (item.estadoRegistro == "*") TextDecoration.LineThrough else TextDecoration.None,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (modulo == ModuloTipo.MAQUINARIAS) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                val lineaTxt = item.nomLinea ?: "Cód. ${item.codLinea}"
                                                val plantaTxt = item.nomPlanta ?: "Cód. ${item.codPlanta}"
                                                val ubicTxt = item.nomUbicacion ?: "Cód. ${item.codUbicacion}"
                                                Text(
                                                    text = "$lineaTxt · $plantaTxt · $ubicTxt",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF64748B),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        // Estado compacto a la derecha
                                        EstadoPillBadge(item.estadoRegistro)
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "Mostrando ${registrosFiltrados.size} de ${registrosAdmitidos.size} registros",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // 5. Panel Inferior de Acciones Rápidas (Propuesta 2: Zona ergonómica del pulgar)
                AnimatedVisibility(
                    visible = state.registroSeleccionado != null && !state.mostrarSheetRegistro,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    val rolActual = state.rolActual
                    if (rolActual != null) {
                        state.registroSeleccionado?.let { seleccionada ->
                            BottomActionPanel(
                                seleccionada = seleccionada,
                                modulo = modulo,
                                rol = rolActual,
                                onSalirPanel = { viewModel.deseleccionar() },
                                onModificar = { viewModel.abrirModificacion(seleccionada) },
                                onInactivar = { viewModel.solicitarInactivar(seleccionada) },
                                onReactivar = { viewModel.solicitarReactivar(seleccionada) },
                                onEliminar = { viewModel.solicitarEliminar(seleccionada) }
                            )
                        }
                    }
                }
            }
        }

        // 6. Formulario Modal BottomSheet Overlay (con soporte para teclado e inconsistencias eliminadas)
        var codLineaSel by remember { mutableStateOf("") }
        var nomLineaSel by remember { mutableStateOf("") }
        var codPlantaSel by remember { mutableStateOf("") }
        var nomPlantaSel by remember { mutableStateOf("") }
        var codUbicSel by remember { mutableStateOf("") }
        var nomUbicSel by remember { mutableStateOf("") }

        LaunchedEffect(state.mostrarSheetRegistro, state.registroSeleccionado, state.modoEdicion) {
            if (state.mostrarSheetRegistro) {
                if (state.modoEdicion && state.registroSeleccionado != null) {
                    val sel = state.registroSeleccionado!!
                    codLineaSel = sel.codLinea ?: ""
                    nomLineaSel = sel.nomLinea ?: ""
                    codPlantaSel = sel.codPlanta ?: ""
                    nomPlantaSel = sel.nomPlanta ?: ""
                    codUbicSel = sel.codUbicacion ?: ""
                    nomUbicSel = sel.nomUbicacion ?: ""
                } else {
                    codLineaSel = ""
                    nomLineaSel = ""
                    codPlantaSel = ""
                    nomPlantaSel = ""
                    codUbicSel = ""
                    nomUbicSel = ""
                }
            }
        }

        AnimatedVisibility(
            visible = state.mostrarSheetRegistro,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { viewModel.cerrarSheet() }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .imePadding()
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    color = Color.White,
                    shadowElevation = 24.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .heightIn(max = 640.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp, bottom = 4.dp)
                                .width(40.dp)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFCBD5E1))
                        )
                        MaestroFormSheetContent(
                            modulo = modulo,
                            item = state.registroSeleccionado,
                            modoEdicion = state.modoEdicion,
                            codigoSugerido = state.codigoSugerido,
                            nombreInicial = if (!state.modoEdicion) state.textoBusqueda.trim().take(60) else "",
                            error = state.mensajeError,
                            codLinea = codLineaSel,
                            nomLinea = nomLineaSel,
                            codPlanta = codPlantaSel,
                            nomPlanta = nomPlantaSel,
                            codUbicacion = codUbicSel,
                            nomUbicacion = nomUbicSel,
                            onAbrirLookup = { tipo -> lookupActivo = tipo },
                            onGuardar = { cod, nom ->
                                viewModel.guardar(cod, nom, codLineaSel, codPlantaSel, codUbicSel)
                            },
                            onSalir = { viewModel.cerrarSheet() }
                        )
                    }
                }
            }
        }

        // 6b. Modal de Consulta y Selección Referencial (Solo para Maquinarias)
        lookupActivo?.let { tipo ->
            val (tituloLookup, listaLookup, selCodigo) = when (tipo) {
                TipoReferencia.LINEA -> Triple("Seleccionar Línea de Producción", state.lineasActivas, codLineaSel)
                TipoReferencia.PLANTA -> Triple("Seleccionar Planta de Producción", state.plantasActivas, codPlantaSel)
                TipoReferencia.UBICACION -> Triple("Seleccionar Ubicación en la Línea", state.ubicacionesActivas, codUbicSel)
            }
            ReferencialLookupDialog(
                titulo = tituloLookup,
                items = listaLookup,
                codigoSeleccionado = selCodigo,
                primaryColor = modulo.primaryColor,
                containerColor = modulo.containerColor,
                onSeleccionar = { ref ->
                    when (tipo) {
                        TipoReferencia.LINEA -> {
                            codLineaSel = ref.codigo
                            nomLineaSel = ref.nombre
                        }
                        TipoReferencia.PLANTA -> {
                            codPlantaSel = ref.codigo
                            nomPlantaSel = ref.nombre
                        }
                        TipoReferencia.UBICACION -> {
                            codUbicSel = ref.codigo
                            nomUbicSel = ref.nombre
                        }
                    }
                    lookupActivo = null
                },
                onCancelar = { lookupActivo = null }
            )
        }

        // 7. Diálogo de Confirmación Modal Overlay
        ConfirmacionModalOverlay(
            dialogo = state.dialogoConfirmacion,
            primaryColor = modulo.primaryColor,
            onDescartar = { viewModel.descartarDialogo() },
            onConfirmarInactivar = { cod, mod -> viewModel.confirmarInactivar(cod, mod) },
            onConfirmarEliminar = { cod, mod -> viewModel.confirmarEliminar(cod, mod) },
            onConfirmarReactivar = { cod, mod -> viewModel.confirmarReactivar(cod, mod) },
            onConfirmarSalir = {
                viewModel.descartarDialogo()
                onSalir()
            }
        )
    }
}

private fun iconoModulo(modulo: ModuloTipo): ImageVector = when (modulo) {
    ModuloTipo.MAQUINARIAS -> Icons.Default.PrecisionManufacturing
    ModuloTipo.PLANTAS -> Icons.Default.Factory
    ModuloTipo.LINEAS -> Icons.Default.LinearScale
    ModuloTipo.UBICACIONES -> Icons.Default.Place
}

@Composable
fun MaestroFormSheetContent(
    modulo: ModuloTipo,
    item: RegistroItem?,
    modoEdicion: Boolean,
    codigoSugerido: String = "1",
    nombreInicial: String = "",
    error: String?,
    codLinea: String,
    nomLinea: String,
    codPlanta: String,
    nomPlanta: String,
    codUbicacion: String,
    nomUbicacion: String,
    onAbrirLookup: (TipoReferencia) -> Unit,
    onGuardar: (String, String) -> Unit,
    onSalir: () -> Unit
) {
    // En Adición se sugiere automáticamente un código libre, editable por el usuario
    var codigo by remember(item, modoEdicion, modulo, codigoSugerido) {
        mutableStateOf(if (modoEdicion) (item?.codigo ?: "") else codigoSugerido)
    }
    var nombre by remember(item, modoEdicion, modulo, nombreInicial) {
        mutableStateOf(if (modoEdicion) (item?.nombre ?: "") else nombreInicial)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = if (modoEdicion) "Modificar ${modulo.singular}" else "Adicionar ${modulo.singular}",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo Código (Input manual numérico sin prefijo, máx. 4 dígitos; protegido con candado en edición)
        Text(
            text = if (modoEdicion) "Código (no editable)" else "Código",
            fontSize = 13.sp,
            color = Color(0xFF475569),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = codigo,
            onValueChange = { nuevo ->
                if (!modoEdicion) {
                    // Validación estricta en tiempo real: solamente dígitos y máximo 4 caracteres
                    val soloDigitos = nuevo.filter { it.isDigit() }.take(4)
                    codigo = soloDigitos
                }
            },
            placeholder = { Text("Ej: 105", fontSize = 14.sp, color = Color(0xFF94A3B8)) },
            supportingText = {
                Text(
                    text = if (modoEdicion) "Clave primaria protegida" else "Solo números · Máximo 4 dígitos (${codigo.length}/4)",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = !modoEdicion,
            trailingIcon = {
                if (modoEdicion) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Protegido con candado",
                        tint = Color(0xFF64748B)
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = if (modoEdicion) Color(0xFFF1F5F9) else Color.White,
                disabledContainerColor = Color(0xFFF1F5F9),
                disabledTextColor = Color(0xFF334155),
                focusedBorderColor = modulo.primaryColor,
                unfocusedBorderColor = Color(0xFFCBD5E1),
                focusedPlaceholderColor = Color(0xFFCBD5E1),
                unfocusedPlaceholderColor = Color(0xFF94A3B8)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo Nombre (máx. 60 caracteres)
        Text(
            text = "Nombre / Descripción",
            fontSize = 13.sp,
            color = Color(0xFF475569),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it.take(60) },
            placeholder = { Text("Ingrese el nombre...", fontSize = 14.sp, color = Color(0xFF94A3B8)) },
            supportingText = {
                Text(
                    text = "Máximo 60 caracteres (${nombre.length}/60)",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = modulo.primaryColor,
                unfocusedBorderColor = Color(0xFFCBD5E1),
                focusedPlaceholderColor = Color(0xFFCBD5E1),
                unfocusedPlaceholderColor = Color(0xFF94A3B8)
            )
        )

        // Campos referenciales con ícono de consulta (Lupa) solo para Maestro de Maquinarias
        if (modulo == ModuloTipo.MAQUINARIAS) {
            Spacer(modifier = Modifier.height(10.dp))
            SelectorReferencialField(
                etiqueta = "Línea de Producción",
                valorMostrado = if (codLinea.isNotEmpty()) "$codLinea · $nomLinea" else "",
                placeholder = "Toque aquí para seleccionar línea",
                primaryColor = modulo.primaryColor,
                onClick = { onAbrirLookup(TipoReferencia.LINEA) }
            )

            Spacer(modifier = Modifier.height(10.dp))
            SelectorReferencialField(
                etiqueta = "Planta de Producción",
                valorMostrado = if (codPlanta.isNotEmpty()) "$codPlanta · $nomPlanta" else "",
                placeholder = "Toque aquí para seleccionar planta",
                primaryColor = modulo.primaryColor,
                onClick = { onAbrirLookup(TipoReferencia.PLANTA) }
            )

            Spacer(modifier = Modifier.height(10.dp))
            SelectorReferencialField(
                etiqueta = "Ubicación en la Línea",
                valorMostrado = if (codUbicacion.isNotEmpty()) "$codUbicacion · $nomUbicacion" else "",
                placeholder = "Toque aquí para seleccionar ubicación",
                primaryColor = modulo.primaryColor,
                onClick = { onAbrirLookup(TipoReferencia.UBICACION) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Estado de Registro (Default 'A' en Adición)
        Text("Estado de Registro", fontSize = 13.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        EstadoPillBadge(
            estado = if (modoEdicion) (item?.estadoRegistro ?: "A") else "A",
            completo = true
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFEE2E2),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFF991B1B),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = error,
                        color = Color(0xFF991B1B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onSalir,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Text("Cancelar", fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), fontSize = 15.sp)
            }
            Button(
                onClick = { onGuardar(codigo, nombre) },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = modulo.primaryColor)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SelectorReferencialField(
    etiqueta: String,
    valorMostrado: String,
    placeholder: String,
    primaryColor: Color,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(etiqueta, fontSize = 13.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            border = BorderStroke(1.dp, if (valorMostrado.isNotEmpty()) primaryColor.copy(alpha = 0.6f) else Color(0xFFCBD5E1)),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clickable { onClick() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valorMostrado.ifEmpty { placeholder },
                    fontSize = 14.sp,
                    color = if (valorMostrado.isNotEmpty()) Color(0xFF0F172A) else Color(0xFF94A3B8),
                    fontWeight = if (valorMostrado.isNotEmpty()) FontWeight.Medium else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Consultar $etiqueta",
                    tint = primaryColor
                )
            }
        }
    }
}
