package com.tiar.kas.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.data.CategoryType
import com.tiar.kas.data.TransactionType
import com.tiar.kas.util.formatRupiah
import com.tiar.kas.util.toLocalDate

// id palsu untuk pilihan "Semua" di PickerField, diubah menjadi null sebelum masuk query
private const val ALL = -1L

@Composable
fun SearchScreen(
    db: AppDatabase,
    onBack: () -> Unit,
    onRowClick: (Long) -> Unit
) {
    val accounts by remember { db.accountDao().observeAll() }.collectAsState(initial = emptyList())
    val categories by remember { db.categoryDao().observeAll() }.collectAsState(initial = emptyList())

    var query by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf<TransactionType?>(null) }   // null berarti semua jenis
    var accountId by rememberSaveable { mutableStateOf<Long?>(null) }
    var categoryId by rememberSaveable { mutableStateOf<Long?>(null) }

    val q = query.trim()

    // query dibuat ulang setiap salah satu dari empat kunci ini berubah
    val results by remember(q, type, accountId, categoryId) {
        db.transactionDao().observeSearch(q, type?.name, accountId, categoryId)
    }.collectAsState(initial = emptyList())

    val grouped = remember(results) { results.groupBy { it.date.toLocalDate() } }
    val totalIncome = results.filter { it.type == TransactionType.PEMASUKAN }.sumOf { it.amount }
    val totalExpense = results.filter { it.type == TransactionType.PENGELUARAN }.sumOf { it.amount }

    // daftar kategori mengikuti jenis yang dipilih
    val categoryOptions = when (type) {
        TransactionType.PEMASUKAN -> buildCategoryOptions(categories, CategoryType.PEMASUKAN)
        TransactionType.PENGELUARAN -> buildCategoryOptions(categories, CategoryType.PENGELUARAN)
        TransactionType.TRANSFER -> emptyList()
        null -> buildCategoryOptions(categories, CategoryType.PENGELUARAN) +
                buildCategoryOptions(categories, CategoryType.PEMASUKAN)
    }

    val incomeColor = MaterialTheme.colorScheme.primary
    val expenseColor = MaterialTheme.colorScheme.error

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ScreenHeader("Cari transaksi", onBack = onBack)
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Catatan, kategori, akun, atau nominal") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            // Row bisa digeser ke samping kalau chip lebih lebar dari layar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = type == null,
                    onClick = {
                        type = null
                        categoryId = null
                    },
                    label = { Text("Semua") }
                )
                TransactionType.entries.forEach { t ->
                    FilterChip(
                        selected = type == t,
                        onClick = {
                            if (type != t) categoryId = null
                            type = t
                        },
                        label = { Text(t.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PickerField(
                    label = "Akun",
                    selectedText = accounts.firstOrNull { it.id == accountId }?.name ?: "Semua akun",
                    options = listOf(ALL to "Semua akun") + accounts.map { it.id to it.name },
                    onSelect = { id -> accountId = id.takeIf { it != ALL } }   // ALL menjadi null
                )

                if (type != TransactionType.TRANSFER) {       // transfer tidak punya kategori
                    PickerField(
                        label = "Kategori",
                        selectedText = categoryOptions.firstOrNull { it.first == categoryId }?.second ?: "Semua kategori",
                        options = listOf(ALL to "Semua kategori") + categoryOptions,
                        onSelect = { id -> categoryId = id.takeIf { it != ALL } }
                    )
                }

                TextButton(onClick = {
                    query = ""
                    type = null
                    accountId = null
                    categoryId = null
                }) { Text("Reset filter") }
            }
        }

        // ringkasan hasil
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    if (results.size >= 500) "Menampilkan 500 transaksi terbaru, persempit filter"
                    else "${results.size} transaksi ditemukan",
                    style = MaterialTheme.typography.bodySmall
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Masuk ${formatRupiah(totalIncome)}", color = incomeColor)
                    Text("Keluar ${formatRupiah(totalExpense)}", color = expenseColor)
                }
            }
        }

        grouped.forEach { (date, dayRows) ->
            item(key = "day-$date") {
            	DayCard(date, dayRows, incomeColor, expenseColor, onRowClick)
            }
        }
    }
}
