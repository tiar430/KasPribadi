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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import com.tiar.kas.util.formatDateOnly
import com.tiar.kas.util.formatTime
import java.time.LocalDate
import kotlinx.coroutines.launch

@Composable
fun BackupScreen(db: AppDatabase, onBack: () -> Unit) {
	val context = LocalContext.current
	val scope = rememberCoroutineScope()
	var lastBackup by remember { mutableLongStateOf(BackupManager.lastBackup(context)) }
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
			Text("Bakcup", style = MaterialTheme.typography.titleLarge)
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
