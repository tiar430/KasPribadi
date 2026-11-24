package com.tiar.kas.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Forest		= Color(0xFF0F6E56)
val ForestDeep	= Color(0xFF04342C)
val Mint		= Color(0xFFE1F5EE)
val Paper		= Color(0xFFFAF9F5)
val Ink			= Color(0xFF2C2C2A)
val InkSoft		= Color(0xFF5F5E5A)
val Line		= Color(0xFFE3E1D9)
val Amber		= Color(0xFF854F0B)
val AmberSoft	= Color(0xFFFAEEDA)
val Rose		= Color(0xFFA32D2D)
val RoseSoft	= Color(0xFFFCEBEB)

val KasColors = lightColorScheme(
	primary = Forest,
	onPrimary = Color.White,
	primaryContainer = Mint,
	onPrimaryContainer = ForestDeep,
	secondary = Color(0xFF444441),
	onSecondary = Color.White,
	secondaryContainer = Color(0xFFF1EFE8),
	onSecondaryContainer = Ink,
	tertiary = Amber,
	onTertiary = Color.White,
	tertiaryContainer = AmberSoft,
	onTertiaryContainer = Color(0xFF412402),
	error = Rose,
	onError = Color.White,
	errorContainer = RoseSoft,
	onErrorContainer = Color(0xFF501313),
	background = Paper,
	onBackground = Ink,
	surface = Paper,
	onSurface = Ink,
	surfaceVariant = Color(0xFFF1EFE8),
	onSurfaceVariant = InkSoft,
	outline = Color(0xFFB4B2A9),
	outlineVariant = Line,
	surfaceContainerLowest = Color.White,
	surfaceContainerLow = Color(0xFFF6F5F0),
	surfaceContainer = Color(0xFFF1EFE8),
	surfaceContainerHigh = Color(0xFFECEAE3),
	surfaceContainerHighest = Color(0xFFE6E4DC)
)
