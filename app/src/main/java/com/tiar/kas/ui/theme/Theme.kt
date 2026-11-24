package com.tiar.kas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun KasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KasColors,
        typography = KasTypography,
        shapes = KasShapes,
        content = content
    )
}
