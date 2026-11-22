package com.tiar.kas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.data.TransactionType
import com.tiar.kas.data.nextRun
import com.tiar.kas.data.skipMissed
import com.tiar.kas.util.formatDateOnly
import com.tiar.kas.util.formatRupiah
import com.tiar.kas.util.label
import kotlinx.coroutines.launch

@Composable
fun RecurringListScreen(db: AppDatabase, onBack: () -> Unit) {
	val dao = db.recurringDao()
	val scope = rememberCoroutineScope()

	val rules by remember { dao.observeAll() }.collectAsState(initial = emptyList())
	val accounts by remember { db.accountDao().observeAll() }.collectAsState(initial = emptyList())
	val categories by remember { db.categoryDao().observeAll() }.collectAsState(initial = emptyList())
	val accountNames = accounts.associate { it.id to it.name }
	val categoryNames = categories.associate { it.id to it.name }

	var deletingId by rememberSaveable { mutableStateOf<Long?>(null) }

	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		contentPadding = PaddingValues(16.dp)
	) {
		item {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text("Transaksi berulang", style = MaterialTheme.typography.titleLarge)
				TextButton(onClick = onBack) { Text("Kembali") }
			}
			Text(
				"Buat aturan baru dari form transaksi lewat pilihan Ulangi",
				style = MaterialTheme.typography.bodySmall
			)
		}

		if (rules.isEmpty()) {
			item { Text("Belum ada aturan berulang", modifier = Modifier.padding(vertical = 16.dp)) }
		}

		items(rules, key = { it.id }) { rule ->
			val toName = rule.toAccountId?.let { accountNames[it] }
			val catName = rule.categoryId?.let { categoryNames[it] } ?: "Tanpa kategori"
			val detail = when (rule.type) {
				TransactionType.TRANSFER -> "${accountNames[rule.accountId]} ke $toName"
				else -> "$catName | ${accountNames[rule.accountId]}"
			}
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 12.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Column(modifier = Modifier.weight(1f)) {
					Text("${rule.frequency.label()} ${formatRupiah(rule.amount)}", style = MaterialTheme.typography.titleSmall)
					Text(detail, style = MaterialTheme.typography.bodySmall)
					if (rule.note.isNotBlank()) Text(rule.note, style = MaterialTheme.typography.bodySmall)
					Text(
						if (rule.active) "Berikutnya ${formatDateOnly(rule.nextRun())}" else "Dihentikan",
						style = MaterialTheme.typography.bodySmall
					)
					Text("${rule.runCount} transaksi sudah dicatat", style = MaterialTheme.typography.bodySmall)
					TextButton(onClick = { deletingId = rule.id }) {
						Text("Hapus aturan", color = MaterialTheme.colorScheme.error)
					}
				}

				Switch(
					checked = rule.active,
					onCheckedChange = { checked ->
						scope.launch {
							if (checked) dao.resume(rule.id, rule.skipMissed())
							else dao.setActive(rule.id, false)
						}
					}
				)
			}
			HorizontalDivider()
		}
	}

	val deleteId = deletingId
	if (deleteId != null) {
		AlertDialog(
			onDismissRequest = { deletingId = null },
			title = { Text("Hapus aturan berulang?") },
			text = { Text("Transaksi yang sudah dicatat tetap ada. Aturan ini tidak akan membuat transaksi baru lagi.") },
			confirmButton = {
				TextButton(onClick = {
					scope.launch {
						dao.deleteById(deleteId)
						deletingId = null
					}
				}) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
			},
			dismissButton = {
				TextButton(onClick = { deletingId = null }) { Text("Batal") }
			}
		)
	}
}
