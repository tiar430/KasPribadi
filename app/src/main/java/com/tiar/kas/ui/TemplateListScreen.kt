package com.tiar.kas.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.OutlinedTextField
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
import com.tiar.kas.util.formatRupiah
import kotlinx.coroutines.launch

@Composable
fun TemplateListScreen(db: AppDatabase, onBack: () -> Unit) {
    val dao = db.templateDao()
    val scope = rememberCoroutineScope()

    val templates by remember { dao.observeAll() }.collectAsState(initial = emptyList())
    val accounts by remember { db.accountDao().observeAll() }.collectAsState(initial = emptyList())
    val categories by remember { db.categoryDao().observeAll() }.collectAsState(initial = emptyList())

    // peta id ke nama, supaya template bisa menampilkan nama akun dan kategori
    val accountNames = accounts.associate { it.id to it.name }
    val categoryNames = categories.associate { it.id to it.name }

    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var nameInput by rememberSaveable { mutableStateOf("") }

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
                Text("Template", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = onBack) { Text("Kembali") }
            }
            Text(
                "Buat template dari form transaksi lewat tombol Simpan sebagai template",
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (templates.isEmpty()) {
            item { Text("Belum ada template", modifier = Modifier.padding(vertical = 16.dp)) }
        }

        items(templates, key = { it.id }) { t ->
            // ?.let dipakai karena id bisa null, sedangkan kunci peta bertipe Long
            val toName = t.toAccountId?.let { accountNames[it] }
            val catName = t.categoryId?.let { categoryNames[it] } ?: "Tanpa kategori"
            val detail = when (t.type) {
                TransactionType.TRANSFER -> "${accountNames[t.accountId]} ke $toName"
                else -> "$catName | ${accountNames[t.accountId]}"
            }
            val amountLabel = if (t.amount > 0) formatRupiah(t.amount) else "Nominal diisi saat dipakai"

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        editingId = t.id
                        nameInput = t.name
                    }
                    .padding(vertical = 12.dp)
            ) {
                Text(t.name, style = MaterialTheme.typography.titleSmall)
                Text(detail, style = MaterialTheme.typography.bodySmall)
                Text(amountLabel, style = MaterialTheme.typography.bodySmall)
            }
            HorizontalDivider()
        }
    }

    val editId = editingId
    if (editId != null) {
        AlertDialog(
            onDismissRequest = { editingId = null },
            title = { Text("Ubah template") },
            text = {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Nama template") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val name = nameInput.trim()
                    if (name.isNotEmpty()) {
                        scope.launch {
                            dao.rename(editId, name)
                            editingId = null
                        }
                    }
                }) { Text("Simpan") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        scope.launch {
                            dao.deleteById(editId)
                            editingId = null
                        }
                    }) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
                    TextButton(onClick = { editingId = null }) { Text("Batal") }
                }
            }
        )
    }
}
