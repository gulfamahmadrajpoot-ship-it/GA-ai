package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Studio Colors
val GaViolet = Color(0xFF9D4EDD)
val GaVioletDark = Color(0xFF7B2CBF)
val GaGold = Color(0xFFFFB703)
val GaGoldDark = Color(0xFFFB8500)
val GaCyan = Color(0xFF00F5D4)
val GaNeonPink = Color(0xFFFF007F)

// Dark Theme Colors
val DarkBackground = Color(0xFF0B0E14)
val DarkSurface = Color(0xFF141926)
val DarkSurfaceVariant = Color(0xFF1F2638)
val DarkCardBorder = Color(0xFF2C364F)
val DarkTextPrimary = Color(0xFFF1F5F9)
val DarkTextSecondary = Color(0xFF94A3B8)

// Light Theme Colors
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightCardBorder = Color(0xFFE2E8F0)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF64748B)

// Gradients
val GaStudioGradient = Brush.horizontalGradient(
    listOf(GaViolet, GaGold)
)

val GaNeonGradient = Brush.linearGradient(
    listOf(Color(0xFF7928CA), Color(0xFFFF0080), Color(0xFFFFB703))
)

val GaDarkSurfaceGradient = Brush.verticalGradient(
    listOf(Color(0xFF171E2F), Color(0xFF0E131E))
)
