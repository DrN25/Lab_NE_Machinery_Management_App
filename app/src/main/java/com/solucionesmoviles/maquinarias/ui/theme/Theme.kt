package com.solucionesmoviles.maquinarias.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = IndustrialBlue,
    onPrimary = Color.White,
    primaryContainer = IndustrialContainer,
    onPrimaryContainer = IndustrialBlue,
    secondary = IndustrialBlueLight,
    background = SurfaceBg,
    surface = Color.White
)

@Composable
fun MaestroMaquinariasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
