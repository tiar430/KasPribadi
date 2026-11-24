package com.tiar.kas.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.FlightTakeoff
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.TwoWheeler
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.tiar.kas.data.TransactionType

// ikon beserta warna latar lingkaran dan warna ikonnya
data class CategoryVisual(
	val icon: ImageVector,
	val container: Color,
	val content: Color
)

// pasangan warna latar muda dan warna ikon tua dari keluarga warna yang sama
private val Food = Color(0xFFFAEEDA) to Color(0xFF854F0B)
private val Transport = Color(0xFFE6F1FB) to Color(0xFF185FA5)
private val Bills = Color(0xFFEEEDFE) to Color(0xFF534AB7)
private val Health = Color(0xFFFCEBEB) to Color(0xFFA32D2D)
private val Shopping = Color(0xFFFAECE7) to Color(0xFF993C1D)
private val Fun = Color(0xFFFBEAF0) to Color(0xFF993556)
private val Income = Color(0xFFE1F5EE) to Color(0xFF0F6E56)
private val Neutral = Color(0xFFF1EFE8) to Color(0xFF5F5E5A)

private fun visual(icon: ImageVector, colors: Pair<Color, Color>) = CategoryVisual(icon, colors.first, colors.second)

fun categoryVisual(name: String?, type: TransactionType): CategoryVisual {
	if (type == TransactionType.TRANSFER) return visual(Icons.Outlined.SwapHoriz, Neutral)
	val n = name?.lowercase().orEmpty()
	return when {
		"kopi" in n || "jajan" in n -> visual(Icons.Outlined.LocalCafe, Food)
		"makan" in n || "sarapan" in n -> visual(Icons.Outlined.Restaurant, Food)
		"bensin" in n -> visual(Icons.Outlined.LocalGasStation, Transport)
		"parkir" in n -> visual(Icons.Outlined.LocalParking, Transport)
		"servis" in n -> visual(Icons.Outlined.Build, Transport)
		"ojek" in n || "transport" in n -> visual(Icons.Outlined.TwoWheeler, Transport)
		"listrik" in n -> visual(Icons.Outlined.Bolt, Bills)
		n == "air" -> visual(Icons.Outlined.WaterDrop, Bills)
		"obat" in n || "dokter" in n || "kesehatan" in n -> visual(Icons.Outlined.MedicalServices, Health)
		"internet" in n || "pulsa" in n -> visual(Icons.Outlined.Wifi, Bills)
		"tagihan" in n -> visual(Icons.Outlined.Receipt, Bills)
		"pakaian" in n -> visual(Icons.Outlined.Checkroom, Shopping)
		"elektronik" in n -> visual(Icons.Outlined.Devices, Shopping)
		"rumah" in n -> visual(Icons.Outlined.Home, Shopping)
		"belanja" in n -> visual(Icons.Outlined.ShoppingBag, Shopping)
		"langganan" in n -> visual(Icons.Outlined.Subscriptions, Fun)
		"liburan" in n -> visual(Icons.Outlined.FlightTakeoff, Fun)
		"hiburan" in n -> visual(Icons.Outlined.Movie, Fun)
		"gaji" in n -> visual(Icons.Outlined.Work, Income)
		"bonus" in n -> visual(Icons.Outlined.Redeem, Income)
		"usaha" in n -> visual(Icons.Outlined.Storefront, Income)
		type ==TransactionType.PEMASUKAN -> visual(Icons.Outlined.Payments, Income)
		else -> visual(Icons.Outlined.Category, Neutral)
	}
}
