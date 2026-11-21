package com.tiar.kas.util

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// MMMM berarti nama bulan lengkap, yyyy berarti tahun 4 digit
private val localeId = Locale.forLanguageTag("id-ED")
private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", localeId)
private val dayFormatter = DateTimeFormatter.ofPattern("EEEE, d MMM", localeId)	// contoh Kamis, 19 Nov
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", localeId)		// contoh 08:30
private val shortMonthFormatter = DateTimeFormatter.ofPattern("MMM", localeId)	// contoh Nov
private val fullDateFormatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy", localeId)	// contoh Sab, 21 Nov 2026

// tanggal 1 jam 00.00 di zona waktu HP, diubah ke milidetik seperti kolom date
fun YearMonth.startMillis(): Long =
	atDay(1)									// tanggal 1 bulan saat ini
		.atStartOfDay(ZoneId.systemDefault())	// jam 00.00 zona waktu HP, contoh WITA
		.toInstant()
		.toEpochMilli()

// awal bulan berikutnya, dipakai sebagai akhir eksklusif
fun YearMonth.endMillis(): Long = plusMonths(1).startMillis()

fun YearMonth.label(): String = format(monthFormatter)	// contoh November 2026

fun YearMonth.shortLabel(): String = format(shortMonthFormatter)

// milidetik menjadi tanggal saja di zona waktu HO, jam dibuang
fun Long.toLocalDate(): LocalDate =
	Instant.ofEpochMilli(this)
		.atZone(ZoneId.systemDefault())
		.toLocalDate()

fun LocalDate.dayLabel(): String = format(dayFormatter)

fun formatTime(millis: Long): String =
	Instant.ofEpochMilli(millis)
		.atZone(ZoneId.systemDefault())
		.format(timeFormatter)

fun formatDateOnly(millis: Long): String = millis.toLocalDate().format(fullDateFormatter)
