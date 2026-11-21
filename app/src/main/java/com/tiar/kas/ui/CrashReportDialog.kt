package com.tiar.kas.ui

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun CrashReportDialog() {
	val context = LocalContext.current
	val clipboard = LocalClipboardManager.current
	val file = remember { File(context.filesDir, "last_crash.txt") }

	// isi laporan kalau file ada, null kalau tidak ada crash sebelumnya
	var report by remember { mutableStateOf(if (file.exists()) file.readText() else null) }

	report?.let { text ->
		AlertDialog(
			onDismissRequest = {},		// tidak bisa ditutup dengan ketuk diluar
			title = { Text("Aplikasi sempat crash") },
			text = {
				SelectionContainer {	// teks bisa diblok dan dicopy manual
					Text(
						text,
						style = MaterialTheme.typography.bodySmall,
						modifier = Modifier
							.heightIn(max = 400.dp)		// batasi tinggi supaya tombol tetap terlihat
							.verticalScroll(rememberScrollState())
					)
				}
			},
			confirmButton = {
				TextButton(onClick = {
					clipboard.setText(AnnotatedString(text))	// salin seluruh laporan ke Clipboard
					file.delete()
					report = null
				}) { Text("Salin dan tutup") }
			},
			dismissButton = {
				TextButton(onClick = {
					file.delete()
					report = null
				}) { Text("Tutup") }
			}
		)
	}
}
