package com.tiar.kas.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val localeId = Locale.forLanguageTag("id-ID")	// format Indonesia

private val rupiahFormat = NumberFormat.getCurrencyInstance(localeId).apply {
	maximumFractionDigits = 0	// tanpa ,00 di belakang
}

fun formatRupiah(amount: Long): String = rupiahFormat.format(amount)	// 100000 menjadi Rp100.000

fun formatDate(millis: Long): String = SimpleDateFormat("dd MMM yyyy HH:mm", localeId).format(Date(millis))	// 17 Nov 2026 05:30

fun formatCompact(amount: Long): String = when {	// nominal ringkas 1000000 menjadi 1jt
	amount >= 1_000_000 ->
		String.format(localeId, "%.1f", amount / 1_000_000.0)	// satu angka dibelakang koma
			.removeSuffix(",0") + "jt"	// 2,0jt menjadi 2jt
	amount >= 1_000 -> "${amount / 1_000}rb"
	else -> amount.toString()
}

fun formatPercent(value: Double): String = String.format(localeId, "%.1f%%", value)
