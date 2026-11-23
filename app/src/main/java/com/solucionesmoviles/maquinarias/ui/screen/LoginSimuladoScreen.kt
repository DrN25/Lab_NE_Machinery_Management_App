package com.solucionesmoviles.maquinarias.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solucionesmoviles.maquinarias.ui.theme.CardBorder
import com.solucionesmoviles.maquinarias.ui.theme.IndustrialBlue
import com.solucionesmoviles.maquinarias.ui.theme.SurfaceBg
import com.solucionesmoviles.maquinarias.ui.viewmodel.RolUsuario

/**
 * Pantalla de Login Simulado (Selector de Rol con Permisos RBAC).
 * Diseñada para adultos mayores: tarjetas grandes, alto contraste y descripción clara de permisos.
 */
@Composable
fun LoginSimuladoScreen(
    onSeleccionarRol: (RolUsuario) -> Unit,
    onCerrarApp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = IndustrialBlue,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.PrecisionManufacturing,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Soluciones Móviles S.A.C.",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Módulo 12 · Control de Maquinarias Textiles",
            fontSize = 14.sp,
            color = Color(0xFF475569),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Seleccione su perfil para ingresar:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        RolUsuario.entries.forEach { rol ->
            RolCard(
                rol = rol,
                icono = iconoPorRol(rol),
                onClick = { onSeleccionarRol(rol) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.weight(1f, fill = true))
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onCerrarApp,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                tint = Color(0xFF475569),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Cerrar aplicación",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569)
            )
        }
    }
}

@Composable
private fun RolCard(
    rol: RolUsuario,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, rol.badgeColor.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = rol.badgeColor.copy(alpha = 0.12f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icono,
                            contentDescription = null,
                            tint = rol.badgeColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rol.nombreMostrar,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = rol.descripcionPermisos,
                        fontSize = 13.sp,
                        color = Color(0xFF475569),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PermisoTag("Crear / Editar", permitido = true)
                    PermisoTag("Inactivar", permitido = rol.puedeCambiarEstado)
                    PermisoTag("Eliminar", permitido = rol.puedeEliminar)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = rol.badgeColor
                ) {
                    Text(
                        text = "Entrar",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PermisoTag(texto: String, permitido: Boolean) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (permitido) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
    ) {
        Text(
            text = if (permitido) "✓ $texto" else "✕ $texto",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (permitido) Color(0xFF166534) else Color(0xFF94A3B8),
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

private fun iconoPorRol(rol: RolUsuario): ImageVector = when (rol) {
    RolUsuario.ADMIN -> Icons.Default.AdminPanelSettings
    RolUsuario.GERENTE -> Icons.Default.ManageAccounts
    RolUsuario.EMPLEADO -> Icons.Default.Badge
}
