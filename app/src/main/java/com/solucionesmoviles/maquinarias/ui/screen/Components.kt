package com.solucionesmoviles.maquinarias.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solucionesmoviles.maquinarias.ui.theme.*
import com.solucionesmoviles.maquinarias.ui.viewmodel.CatalogoReferencia
import com.solucionesmoviles.maquinarias.ui.viewmodel.DialogoConfirmacion
import com.solucionesmoviles.maquinarias.ui.viewmodel.ModuloTipo
import com.solucionesmoviles.maquinarias.ui.viewmodel.RegistroItem
import com.solucionesmoviles.maquinarias.ui.viewmodel.RolUsuario

@Composable
fun SalirButton(
    primaryColor: Color,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.2.dp, primaryColor.copy(alpha = 0.45f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color(0xFFF8FAFC),
            contentColor = primaryColor
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
        modifier = Modifier.height(38.dp)
    ) {
        Text(
            text = "Salir",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun FiltroChipItem(
    texto: String,
    seleccionado: Boolean,
    primaryColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (seleccionado) primaryColor else Color.White,
        border = if (seleccionado) null else BorderStroke(1.dp, CardBorder),
        shadowElevation = if (seleccionado) 1.dp else 0.dp,
        modifier = Modifier
            .heightIn(min = 36.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Text(
                text = texto,
                color = if (seleccionado) Color.White else Color(0xFF475569),
                fontSize = 13.sp,
                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
fun EstadoPillBadge(
    estado: String,
    completo: Boolean = false
) {
    val (fondo, texto, etiqueta) = when (estado) {
        "A" -> Triple(StatusActiveBg, StatusActiveText, if (completo) "A · Activo" else "A")
        "I" -> Triple(StatusInactiveBg, StatusInactiveText, if (completo) "I · Inactivo" else "I")
        else -> Triple(StatusDeletedBg, StatusDeletedText, if (completo) "* · Eliminado" else "*")
    }
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = fondo,
        modifier = Modifier.widthIn(min = 34.dp)
    ) {
        Text(
            text = etiqueta,
            color = texto,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun BottomActionPanel(
    seleccionada: RegistroItem,
    modulo: ModuloTipo,
    rol: RolUsuario,
    onSalirPanel: () -> Unit,
    onModificar: () -> Unit,
    onInactivar: () -> Unit,
    onReactivar: () -> Unit,
    onEliminar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = Color.White,
        shadowElevation = 16.dp,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFCBD5E1))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = modulo.containerColor,
                        modifier = Modifier.widthIn(min = 42.dp)
                    ) {
                        Text(
                            text = seleccionada.codigo,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = modulo.primaryColor,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    EstadoPillBadge(seleccionada.estadoRegistro, completo = true)
                }
                // Consistente con el formulario y barra superior: botón real "Salir"
                SalirButton(
                    primaryColor = modulo.primaryColor,
                    onClick = onSalirPanel
                )
            }

            Text(
                text = seleccionada.nombre,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (seleccionada.estadoRegistro) {
                    "A" -> {
                        if (rol.puedeModificar) {
                            Button(
                                onClick = onModificar,
                                modifier = Modifier.weight(1f).height(48.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = modulo.containerColor,
                                    contentColor = modulo.primaryColor
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Modificar", fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                            }
                        }

                        if (rol.puedeInactivar) {
                            OutlinedButton(
                                onClick = onInactivar,
                                modifier = Modifier.weight(1f).height(48.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = StatusInactiveText
                                ),
                                border = BorderStroke(1.2.dp, Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Inactivar", fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                            }
                        }

                        if (rol.puedeEliminar) {
                            OutlinedButton(
                                onClick = onEliminar,
                                modifier = Modifier.weight(1f).height(48.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = StatusDeletedText
                                ),
                                border = BorderStroke(1.2.dp, Color(0xFFEF4444)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Eliminar", fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                            }
                        }

                        if (!rol.puedeModificar && !rol.puedeInactivar && !rol.puedeEliminar) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Solo lectura: el perfil ${rol.nombreMostrar} solo puede consultar y crear nuevos registros.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF475569),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    // Regla estricta: Inactivo ('I') o Eliminado ('*') NO se puede modificar ni eliminar/inactivar, SOLO Reactivar
                    "I", "*" -> {
                        if (rol.puedeReactivar) {
                            Button(
                                onClick = onReactivar,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StatusActiveBg,
                                    contentColor = StatusActiveText
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Reactivar", fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Solo lectura: el perfil ${rol.nombreMostrar} no puede reactivar registros.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF475569),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmacionModalOverlay(
    dialogo: DialogoConfirmacion?,
    primaryColor: Color,
    onDescartar: () -> Unit,
    onConfirmarInactivar: (String, ModuloTipo) -> Unit,
    onConfirmarEliminar: (String, ModuloTipo) -> Unit,
    onConfirmarReactivar: (String, ModuloTipo) -> Unit,
    onConfirmarSalir: () -> Unit
) {
    AnimatedVisibility(
        visible = dialogo != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        dialogo?.let { dlg ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable { onDescartar() },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .widthIn(max = 440.dp)
                        .padding(16.dp)
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 24.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        when (dlg) {
                            is DialogoConfirmacion.Inactivar -> {
                                Text(
                                    text = "¿Inactivar ${dlg.modulo.singular}?",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "¿Está seguro de pasar a estado Inactivo (I) ${dlg.modulo.articuloSingular} Cód. ${dlg.item.codigo} \"${dlg.item.nombre}\"?\n\nNo aparecerá disponible para nuevas asignaciones.",
                                    fontSize = 15.sp,
                                    color = Color(0xFF475569),
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(22.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = onDescartar,
                                        modifier = Modifier.height(46.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                                    ) {
                                        Text("Cancelar", color = Color(0xFF475569), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Button(
                                        onClick = { onConfirmarInactivar(dlg.item.codigo, dlg.modulo) },
                                        modifier = Modifier.height(46.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Confirmar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    }
                                }
                            }

                            is DialogoConfirmacion.Eliminar -> {
                                Text(
                                    text = "¿Eliminar ${dlg.modulo.singular}?",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "¿Está seguro de marcar como eliminada (*) ${dlg.modulo.articuloSingular} Cód. ${dlg.item.codigo} \"${dlg.item.nombre}\"?\n\nSe aplicará un borrado lógico para conservar el historial del sistema.",
                                    fontSize = 15.sp,
                                    color = Color(0xFF475569),
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(22.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = onDescartar,
                                        modifier = Modifier.height(46.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                                    ) {
                                        Text("Cancelar", color = Color(0xFF475569), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Button(
                                        onClick = { onConfirmarEliminar(dlg.item.codigo, dlg.modulo) },
                                        modifier = Modifier.height(46.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Confirmar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    }
                                }
                            }

                            is DialogoConfirmacion.Reactivar -> {
                                Text(
                                    text = "¿Reactivar ${dlg.modulo.singular}?",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "¿Desea reactivar ${dlg.modulo.articuloSingular} Cód. ${dlg.item.codigo} \"${dlg.item.nombre}\" y restaurar su estado a Activo (A)?",
                                    fontSize = 15.sp,
                                    color = Color(0xFF475569),
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(22.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = onDescartar,
                                        modifier = Modifier.height(46.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                                    ) {
                                        Text("Cancelar", color = Color(0xFF475569), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Button(
                                        onClick = { onConfirmarReactivar(dlg.item.codigo, dlg.modulo) },
                                        modifier = Modifier.height(46.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Confirmar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    }
                                }
                            }

                            is DialogoConfirmacion.Salir -> {
                                Text(
                                    text = "¿Cerrar sesión actual?",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Regresará a la pantalla de selección de rol para ingresar con otro perfil.",
                                    fontSize = 15.sp,
                                    color = Color(0xFF475569),
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(22.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = onDescartar,
                                        modifier = Modifier.height(46.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                                    ) {
                                        Text("Cancelar", color = Color(0xFF475569), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Button(
                                        onClick = onConfirmarSalir,
                                        modifier = Modifier.height(46.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Confirmar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Pantalla modal de consulta y selección de registros referenciales activos ('A'),
 * ordenados alfabéticamente por nombre/descripción según requerimiento de la guía.
 */
@Composable
fun ReferencialLookupDialog(
    titulo: String,
    items: List<CatalogoReferencia>,
    codigoSeleccionado: String,
    primaryColor: Color,
    containerColor: Color,
    onSeleccionar: (CatalogoReferencia) -> Unit,
    onCancelar: () -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    val filtrados = remember(items, busqueda) {
        if (busqueda.isBlank()) items
        else items.filter {
            it.nombre.contains(busqueda, ignoreCase = true) ||
                it.codigo.contains(busqueda, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onCancelar() },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 460.dp)
                .heightIn(min = 420.dp, max = 700.dp)
                .padding(vertical = 16.dp)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 24.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = titulo,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Solo registros activos (A), ordenados por código",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    placeholder = { Text("Filtrar por nombre o código...", fontSize = 14.sp, color = Color(0xFF94A3B8)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                    trailingIcon = {
                        if (busqueda.isNotEmpty()) {
                            IconButton(onClick = { busqueda = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedPlaceholderColor = Color(0xFFCBD5E1),
                        unfocusedPlaceholderColor = Color(0xFF94A3B8)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (filtrados.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay registros activos disponibles",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filtrados, key = { it.codigo }) { ref ->
                            val selected = ref.codigo == codigoSeleccionado
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) containerColor else Color(0xFFF8FAFC),
                                border = BorderStroke(
                                    width = if (selected) 1.5.dp else 1.dp,
                                    color = if (selected) primaryColor else CardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSeleccionar(ref) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (selected) primaryColor else Color(0xFFE2E8F0),
                                            modifier = Modifier.widthIn(min = 42.dp)
                                        ) {
                                            Text(
                                                text = ref.codigo,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (selected) Color.White else Color(0xFF334155),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                maxLines = 1,
                                                softWrap = false,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Text(
                                            text = ref.nombre,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Seleccionado",
                                            tint = primaryColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Text("Cancelar", fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), fontSize = 15.sp)
                }
            }
        }
    }
}
