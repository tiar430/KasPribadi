package com.tiar.kas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.Account
import com.tiar.kas.data.AccountGroup
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.util.formatRupiah
import com.tiar.kas.util.label
import kotlin.math.abs
import kotlinx.coroutines.launch

@Composable
fun AccountFormScreen(db: AppDatabase, editId: Long?, onClose: () -> Unit) {
    val accountDao = db.accountDao()
    val txDao = db.transactionDao()
    val scope = rememberCoroutineScope()

    var name by rememberSaveable { mutableStateOf("") }
    var group by rememberSaveable { mutableStateOf(AccountGroup.TUNAI) }
    var initialText by rememberSaveable { mutableStateOf("") }     // angka tanpa tanda
    var isDebt by rememberSaveable { mutableStateOf(false) }       // true berarti saldo awal minus
    var includeInTotal by rememberSaveable { mutableStateOf(true) }
    var sortOrder by rememberSaveable { mutableStateOf(0) }        // disimpan supaya urutan tidak berubah saat edit
    var statementText by rememberSaveable { mutableStateOf("") }
    var dueText by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var loaded by rememberSaveable { mutableStateOf(editId == null) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var txCount by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(editId) {
        if (!loaded && editId != null) {
            accountDao.getById(editId)?.let { acc ->      // let hanya berjalan kalau akun ditemukan
                name = acc.name
                group = acc.accountGroup
                initialText = abs(acc.initialBalance).toString()   // abs membuang tanda minus
                isDebt = acc.initialBalance < 0
                includeInTotal = acc.includeInTotal
                sortOrder = acc.sortOrder
                statementText = acc.statementDay?.toString() ?: ""
                dueText = acc.dueDay?.toString() ?: ""
            }
            loaded = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            if (editId == null) "Akun baru" else "Edit akun",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama akun") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        PickerField(
            label = "Grup",
            selectedText = group.label(),
            // ordinal TUNAI = 0, BANK = 1, dan seterusnya, diubah ke Long untuk PickerField
            options = AccountGroup.entries.map { it.ordinal.toLong() to it.label() },
            onSelect = { group = AccountGroup.entries[it.toInt()] }   // ambil enum dari nomor urutnya
        )

        if (group == AccountGroup.KARTU_KREDIT) {
        	Row(
        		modifier = Modifier.fillMaxWidth(),
        		horizontalArrangement = Arrangement.spacedBy(8.dp)
        	) {
        		OutlinedTextField(
        			value = statementText,
        			onValueChange = { input -> statementText = input.filter { it.isDigit() }.take(2) },
        			label = { Text("Tgl cetak") },
        			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        			singleLine = true,
        			modifier = Modifier.weight(1f)
        		)
        		OutlinedTextField(
        			value = dueText,
        			onValueChange = { input -> dueText = input.filter { it.isDigit() }.take(2) },
        			label = { Text("Tgl jatuh tempo") },
        			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        			singleLine = true,
        			modifier = Modifier.weight(1f)
        		)
        	}
        	Text(
        		"Contoh cetak 25 dan jatuh tempo 10. Tanggal 31 di bulan pendek dianggap akhir bulan",
        		style = MaterialTheme.typography.bodySmall
        	)
        }

        OutlinedTextField(
            value = initialText,
            onValueChange = { input -> initialText = input.filter { it.isDigit() }.take(13) },
            label = { Text("Saldo awal") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            supportingText = {
                val amount = initialText.toLongOrNull() ?: 0L
                Text(formatRupiah(if (isDebt) -amount else amount))   // pratinjau dengan tanda minus
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Saldo awal minus (utang)")
            Switch(checked = isDebt, onCheckedChange = { isDebt = it })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hitung di total aset")
            Switch(checked = includeInTotal, onCheckedChange = { includeInTotal = it })
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onClose) { Text("Batal") }

            Button(onClick = {
                val trimmed = name.trim()
                val amount = initialText.toLongOrNull() ?: 0L
                val isCard = group == AccountGroup.KARTU_KREDIT

                fun validDay(text: String) = text.isEmpty() || text.toIntOrNull()?.let { it in 1..31 } == true
                error = when {
                	trimmed.isEmpty() -> "Nama akun wajib diisi"
                	isCard && !validDay(statementText) -> "Tanggal cetak harus 1 sampai 31"
                	isCard && !validDay(dueText) -> "Tanggal jatuh tempo harus 1 sampai 31"
                	else -> null
                }
                
                if (error == null) {
                    scope.launch {
                        val acc = Account(
                            id = editId ?: 0,
                            name = trimmed,
                            accountGroup = group,
                            initialBalance = if (isDebt) -amount else amount,
                            includeInTotal = includeInTotal,
                            sortOrder = if (editId == null) accountDao.nextSortOrder() else sortOrder,
                            statementDay = if (isCard) statementText.toIntOrNull() else null,
                            dueDay = if (isCard) dueText.toIntOrNull() else null
                        )
                        if (editId == null) accountDao.insert(acc) else accountDao.update(acc)
                        onClose()
                    }
                }
            }) {
                Text("Simpan")
            }

            if (editId != null) {
                TextButton(onClick = {
                    scope.launch {
                        if (accountDao.count() <= 1) {
                            error = "Minimal harus ada satu akun"
                        } else {
                            txCount = txDao.countByAccount(editId)   // hitung dulu sebelum dialog tampil
                            showDeleteDialog = true
                        }
                    }
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showDeleteDialog && editId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus akun?") },
            text = {
                Text(
                    if (txCount > 0) "Akun ini punya $txCount transaksi. Semua transaksi itu ikut terhapus permanen."
                    else "Akun ini belum punya transaksi."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        accountDao.deleteById(editId)
                        onClose()
                    }
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}
