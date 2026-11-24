package com.tiar.kas.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.util.BackupManager
import com.tiar.kas.util.CsvExporter
import com.tiar.kas.util.endMillis
import com.tiar.kas.util.formatDateOnly
import com.tiar.kas.util.formatTime
import com.tiar.kas.util.startMillis
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.launch

@Composable
fun BackupScreen(db: AppDatabase, onBack: () -> Unit) {
	val context = LocalContext.current
	val scope = rememberCoroutineScope()
	var lastBackup by remember { mutableLongStateOf(BackupManager.lastBackup(context)) }
	var reminderDays by remember { mutableStateOf(BackupManager.reminderDays(context)) }
	var busy by remember { mutableStateOf(false) }		// cegah tombol diketuk dua kali saat proses berjalan

	// pemilih lokasi simpan, uri null kalau batal
	val createBackup = rememberLauncherForActivityResult(
		ActivityResultContracts.CreateDocument("application/zip")
	) { uri ->
		if (uri != null) {
			busy = true
			scope.launch {
				try {
					val photoCount = BackupManager.backup(context, db, uri)
					lastBackup = BackupManager.lastBackup(context)
					Toast.makeText(context, "Backup tersimpan dengan $photoCount foto", Toast.LENGTH_SHORT).show()
				} catch (e: Exception) {
					// pesan error asli ikut ditampilkan agar mudah dilacak
					Toast.makeText(context, "Backup gagal: ${e.message}", Toast.LENGTH_LONG).show()
				} finally {
					busy = false
				}
			}
		}
	}

	var pendingRestore by rememberSaveable { mutableStateOf<Uri?>(null) }
	val openBackup = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
		if (uri != null) pendingRestore = uri
	}
	var exportMonth by rememberSaveable { mutableStateOf(YearMonth.now()) }
	var allTime by rememberSaveable { mutableStateOf(false) }
	var separator by rememberSaveable { mutableStateOf(";") }

	val exportCsv = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
		if (uri != null) {
			busy = true
			scope.launch {
				try {
					val (start, end) =
						if (allTime) 0L to Long.MAX_VALUE
						else exportMonth.startMillis() to exportMonth.endMillis()
					val rows = db.transactionDao().getExportRows(start, end)
					CsvExporter.export(context, rows, uri, separator.first())
					Toast.makeText(context, "${rows.size} transaksi diekspor", Toast.LENGTH_SHORT).show()
				} catch (e: Exception) {
					Toast.makeText(context, "Ekspor gagal: ${e.message}", Toast.LENGTH_LONG).show()
				} finally {
					busy = false
				}
			}
		}
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			ScreenHeader("Backup dan ekspor", onBack = onBack)
			TextButton(onClick = onBack) { Text("Kembali") }
		}

		Text(
			if (lastBackup > 0) "Backup terakhir ${formatDateOnly(lastBackup)} ${formatTime(lastBackup)}"
			else "Belum pernah backup",
			style = MaterialTheme.typography.bodyMedium
		)
		Text(
			"File ZIP berisi database dan semua foto struk, tanpa enkripsi. Simpan di tempat pribadi",
			style = MaterialTheme.typography.bodySmall
		)
		Text("Ingatkan backup", style = MaterialTheme.typography.titleSmall)
		Row(
			modifier = Modifier.horizontalScroll(rememberScrollState()),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			listOf(0 to "Mati", 3 to "3 hari", 7 to "7 hari", 14 to "14 hari", 30 to "30 hari").forEach { (days, text) ->
				FilterChip(
					selected = reminderDays == days,
					onClick = {
						reminderDays = days
						BackupManager.setReminderDays(context, days)
					},
					label = { Text(text) }
				)
			}
		}
		
		Button(
			onClick = { createBackup.launch("KasPribadi-${LocalDate.now()}.zip") },
			enabled = !busy
		) {
			Text(if (busy) "Menyimpan..." else "Buat backup")
		}
		OutlinedButton(
			onClick = { openBackup.launch(arrayOf("application/zip", "application/octet-stream")) },
			enabled = !busy
		) {
			Text("Pulihkan backup")
		}

		HorizontalDivider()
		Text("Ekspor CSV", style = MaterialTheme.typography.titleMedium)
		Text(
			"Pilih titik koma kalau Excel di laptopmu memakai format Indonesia",
			style = MaterialTheme.typography.bodySmall
		)

		Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
			FilterChip(selected = !allTime, onClick = { allTime = false }, label = { Text("Per bulan") })
			FilterChip(selected = allTime, onClick = { allTime = true }, label = { Text("Semua waktu") })
		}
		if (!allTime) {
			MonthSwitcher(month = exportMonth, onMonthChange = { exportMonth = it })
		}

		Row(
			modifier = Modifier.horizontalScroll(rememberScrollState()),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			FilterChip(selected = separator == ";", onClick = { separator = ";" }, label = { Text("Titik koma ;") })
			FilterChip(selected = separator == ",", onClick = { separator = "," }, label = { Text("Koma ,") })
		}

		Button(
			onClick = {
				val fileName =
					if (allTime) "KasPribadi-semua-${LocalDate.now()}.csv"
					else "KasPribadi-$exportMonth.csv"
				exportCsv.launch(fileName)
			},
			enabled = !busy
		) {
			Text("Ekspor CSV")
		}
	}

	val restoreUri = pendingRestore
	if (restoreUri != null) {
		AlertDialog(
			onDismissRequest = { pendingRestore = null },
			title = { Text("Pulihkan backup?") },
			text = { Text("Semua data sekarang diganti dengan isi backup. Data yang belum di-backup akan hilang. Aplikasi akan terbuka ulang.") },
			confirmButton = {
				TextButton(onClick = {
					pendingRestore = null
					busy = true
					scope.launch {
						try {
							BackupManager.restore(context, restoreUri)
							BackupManager.restartApp(context)
						} catch (e: Exception) {
							busy = false
							Toast.makeText(context, "Pemulihan gagal: ${e.message}", Toast.LENGTH_LONG).show()
						}
					}
				}) { Text("Pulihkan", color = MaterialTheme.colorScheme.error) }
			},
			dismissButton = {
				TextButton(onClick = { pendingRestore = null }) { Text("Batal") }
			}
		)
	}
}
