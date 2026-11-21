package com.tiar.kas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.data.Budget
import com.tiar.kas.data.CategoryType
import com.tiar.kas.data.TransactionType
import com.tiar.kas.util.endMillis
import com.tiar.kas.util.formatPercent
import com.tiar.kas.util.formatRupiah
import com.tiar.kas.util.startMillis
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.launch

private val warningColor = Color(0xFFF9A825)     // kuning tua, terbaca di latar putih

@Composable
fun BudgetScreen(db: AppDatabase, onBack: () -> Unit) {
    val budgetDao = db.budgetDao()
    val scope = rememberCoroutineScope()

    var month by rememberSaveable { mutableStateOf(YearMonth.now()) }

    val categories by remember { db.categoryDao().observeAll() }.collectAsState(initial = emptyList())
    val budgets by remember { budgetDao.observeAll() }.collectAsState(initial = emptyList())
    val spentTotals by remember(month) {
        db.transactionDao().observeCategoryTotals(
            TransactionType.PENGELUARAN.name, month.startMillis(), month.endMillis()
        )
    }.collectAsState(initial = emptyList())

    val mainExpense = categories.filter { it.type == CategoryType.PENGELUARAN && it.parentId == null }
    val budgetMap = budgets.associate { it.categoryId to it.amount }
    val spentMap = spentTotals.associate { it.categoryId to it.total }   // peta id kategori ke total terpakai

    // ringkasan hanya menghitung kategori yang punya budget
    val totalBudget = budgets.sumOf { it.amount }
    val totalSpentBudgeted = budgets.sumOf { spentMap[it.categoryId] ?: 0L }
    val totalRemaining = totalBudget - totalSpentBudgeted

    // sisa hari termasuk hari ini, hanya bermakna untuk bulan berjalan
    val isCurrentMonth = month == YearMonth.now()
    val daysLeft = if (isCurrentMonth) month.lengthOfMonth() - LocalDate.now().dayOfMonth + 1 else 0

    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var inputText by rememberSaveable { mutableStateOf("") }

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
                Text("Budget bulanan", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = onBack) { Text("Kembali") }
            }
        }

        item {
            MonthSwitcher(month = month, onMonthChange = { month = it })
        }

        // ringkasan semua budget
        if (totalBudget > 0) {
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text("Total budget", style = MaterialTheme.typography.titleMedium)
                    BudgetProgress(spent = totalSpentBudgeted, budget = totalBudget)
                    if (isCurrentMonth && totalRemaining > 0 && daysLeft > 0) {
                        Text(
                            "Bisa dipakai sekitar ${formatRupiah(totalRemaining / daysLeft)} per hari sampai akhir bulan",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                HorizontalDivider(thickness = 2.dp)
            }
        } else {
            item {
                Text(
                    "Belum ada budget. Ketuk kategori untuk mengatur",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                HorizontalDivider()
            }
        }

        items(mainExpense, key = { it.id }) { cat ->
            val budget = budgetMap[cat.id]
            val spent = spentMap[cat.id] ?: 0L

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        editingId = cat.id
                        inputText = budget?.toString() ?: ""
                    }
                    .padding(vertical = 12.dp)
            ) {
                Text(cat.name, style = MaterialTheme.typography.titleSmall)
                if (budget != null) {
                    BudgetProgress(spent = spent, budget = budget)
                } else {
                    // kategori tanpa budget tetap menampilkan pengeluarannya
                    Text(
                        "Belum diatur, terpakai ${formatRupiah(spent)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            HorizontalDivider()
        }
    }

    val editId = editingId
    if (editId != null) {
        val catName = mainExpense.firstOrNull { it.id == editId }?.name.orEmpty()

        AlertDialog(
            onDismissRequest = { editingId = null },
            title = { Text("Budget $catName") },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { input -> inputText = input.filter { it.isDigit() }.take(13) },
                        label = { Text("Nominal per bulan") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        supportingText = { inputText.toLongOrNull()?.let { Text(formatRupiah(it)) } }
                    )
                    Text(
                        "Kosongkan lalu simpan untuk menghapus budget",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val amount = inputText.toLongOrNull() ?: 0L
                    scope.launch {
                        if (amount > 0) {
                            budgetDao.upsert(Budget(categoryId = editId, amount = amount))
                        } else {
                            budgetDao.deleteByCategory(editId)
                        }
                        editingId = null
                    }
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { editingId = null }) { Text("Batal") }
            }
        )
    }
}

// bar progres beserta teks terpakai, persen, dan sisa atau kelebihan
@Composable
private fun BudgetProgress(spent: Long, budget: Long) {
    val ratio = spent.toFloat() / budget                  // bisa lebih dari 1 kalau melewati budget
    val barColor = when {
        ratio > 1f -> MaterialTheme.colorScheme.error      // lewat budget
        ratio >= 0.8f -> warningColor                      // hampir habis
        else -> MaterialTheme.colorScheme.primary          // aman
    }
    val remaining = budget - spent

    Spacer(modifier = Modifier.height(6.dp))
    LinearProgressIndicator(
        progress = { ratio.coerceIn(0f, 1f) },             // bar dibatasi 0 sampai penuh
        color = barColor,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "${formatRupiah(spent)} dari ${formatRupiah(budget)}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            formatPercent(ratio * 100.0),
            style = MaterialTheme.typography.bodySmall,
            color = barColor
        )
    }
    Text(
        if (remaining >= 0) "Sisa ${formatRupiah(remaining)}"
        else "Lewat ${formatRupiah(-remaining)}",          // tanda minus dibalik supaya tampil positif
        style = MaterialTheme.typography.bodySmall,
        color = if (remaining >= 0) Color.Unspecified else MaterialTheme.colorScheme.error
    )
}
