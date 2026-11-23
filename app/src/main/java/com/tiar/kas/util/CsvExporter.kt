package com.tiar.kas.util

import android.content.Context
import android.net.Uri
import com.tiar.kas.data.ExportRow
import com.tiar.kas.data.TransactionType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CsvExporter {
	// format tanggal yang dikenali excel di semua pengaturan region
	private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

	private val header = listOf(
		"Tanggal", "Jenis", "Kategori", "Subkategori", "Akun",
		"Akun Tujuan", "Nominal", "Nominal Bertanda", "Catatan"
	)

	suspend fun export(context: Context, rows: List<ExportRow>, target: Uri, separator: Char) = withContext(Dispatchers.IO) {
		val output = context.contentResolver.openOutputStream(target)
			?: error("Lokasi ekspor tidak bisa ditulis")

		output.bufferedWriter(Charsets.UTF_8).use { writer ->
			writer.write("\uFEFF")
			writer.write(line(header, separator))

			rows.forEach { r ->
				val signed = when (r.type) {
					TransactionType.PEMASUKAN -> r.amount.toString()
					TransactionType.PENGELUARAN -> (-r.amount).toString()
					TransactionType.TRANSFER -> ""
				}
				val fields = listOf(
					Instant.ofEpochMilli(r.date).atZone(ZoneId.systemDefault()).format(dateFormat),
					r.type.name.lowercase().replaceFirstChar { it.uppercase() },
					r.categoryName.orEmpty(),
					r.subcategoryName.orEmpty(),
					r.accountName,
					r.toAccountName.orEmpty(),
					r.amount.toString(),
					signed,
					r.note
				)
				writer.write(line(fields, separator))
			}
		}
	}

	// gabungkan kolom menjadi satu baris, diakhiri CRLF seperti standar CSV
	private fun line(fields: List<String>, separator: Char): String =
		fields.joinToString(separator.toString()) { escape(it, separator) } + "\r\n"

	private fun escape(value: String, separator: Char): String =
		if (value.contains(separator) || value.contains('"') || value.contains('\n') || value.contains('\r')) {
			"\"" + value.replace("\"", "\"\"") + "\""
		} else {
			value
		}
}
