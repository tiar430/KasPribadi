package com.tiar.kas.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tiar.kas.R

// satu file font variabel dipakai untuk empat ketebalan
@OptIn(ExperimentalTextApi::class)
private fun jakarta(weight: Int) = Font(
    R.font.plus_jakarta_sans,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight))   // atur sumbu ketebalan font
)

val Jakarta = FontFamily(jakarta(400), jakarta(500), jakarta(600), jakarta(700))

// salin semua gaya bawaan Material, lalu ganti hurufnya
private val base = Typography()

val KasTypography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Jakarta, letterSpacing = (-0.5).sp),
    displayMedium = base.displayMedium.copy(fontFamily = Jakarta, letterSpacing = (-0.5).sp),
    displaySmall = base.displaySmall.copy(fontFamily = Jakarta, letterSpacing = (-0.25).sp),
    headlineLarge = base.headlineLarge.copy(fontFamily = Jakarta, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.25).sp),
    headlineMedium = base.headlineMedium.copy(fontFamily = Jakarta, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.25).sp),
    headlineSmall = base.headlineSmall.copy(fontFamily = Jakarta, fontWeight = FontWeight.SemiBold),
    titleLarge = base.titleLarge.copy(fontFamily = Jakarta, fontWeight = FontWeight.SemiBold),
    titleMedium = base.titleMedium.copy(fontFamily = Jakarta, fontWeight = FontWeight.SemiBold),
    titleSmall = base.titleSmall.copy(fontFamily = Jakarta, fontWeight = FontWeight.Medium),
    bodyLarge = base.bodyLarge.copy(fontFamily = Jakarta),
    bodyMedium = base.bodyMedium.copy(fontFamily = Jakarta),
    bodySmall = base.bodySmall.copy(fontFamily = Jakarta),
    labelLarge = base.labelLarge.copy(fontFamily = Jakarta, fontWeight = FontWeight.SemiBold),
    labelMedium = base.labelMedium.copy(fontFamily = Jakarta, fontWeight = FontWeight.Medium),
    labelSmall = base.labelSmall.copy(fontFamily = Jakarta, fontWeight = FontWeight.Medium)
)

// angka tabular, setiap digit sama lebar supaya nominal rata di daftar
val Tabular = TextStyle(fontFeatureSettings = "tnum")

val KasShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),     // chip kecil
    small = RoundedCornerShape(12.dp),         // tombol dan kolom isian
    medium = RoundedCornerShape(16.dp),        // kartu
    large = RoundedCornerShape(24.dp),         // kartu besar dan lembar bawah
    extraLarge = RoundedCornerShape(28.dp)     // dialog
)
