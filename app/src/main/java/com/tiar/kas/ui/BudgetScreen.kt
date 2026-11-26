package com.tiar.kas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.data.Budget
import com.tiar.kas.data.CategoryType
import com.tiar.kas.data.TransactionType
import com.tiar.kas.ui.theme.Tabular
import com.tiar.kas.util.endMillis
import com.tiar.kas.util.formatPercent
import com.tiar.kas.util.formatRupiah
import com.tiar.kas.util.startMillis
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs
import kotlinx.coroutines.launch

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
    val spentMap = spentTotals.associate { it.categoryId to it.total }

    val totalBudget = budgets.sumOf { it.amount }
    val totalSpent = budgets.sumOf { spentMap[it.categoryId] ?: 0L }
    val totalRemaining = totalBudget - totalSpent

    val isCurrentMonth = month == YearMonth.now()
    val daysLeft = if (isCurrentMonth) month.lengthOfMonth() - LocalDate.now().dayOfMonth + 1 else 0

    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var inputText by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "header") { ScreenHeader("Budget bulanan", onBack = onBack) }

        item(key = "month") { MonthSwitcher(month = month, onMonthChange = { month = it }) }

        item(key = "summary") {
            if (totalBudget > 0) {
                BudgetSummaryCard(
                    remaining = totalRemaining,
                    budget = totalBudget,
                    spent = totalSpent,
                    perDay = if (isCurrentMonth && totalRemaining > 0 && daysLeft > 0) totalRemaining / daysLeft else null
                )
            } else {
                FormCard {
                    Text(
                        "Belum ada budget. Ketuk kategori di bawah untuk mengatur",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        item(key = "categories") {
            FormCard {
                mainExpense.forEachIndexed { index, cat ->
                    if (index > 0) {
                        HorizontalDivider(modifier = Modifier.padding(start = 68.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    }
                    BudgetRow(
                        name = cat.name,
                        budget = budgetMap[cat.id],
                        spent = spentMap[cat.id] ?: 0L,
                        onClick = {
                            editingId = cat.id
                            inputText = budgetMap[cat.id]?.toString() ?: ""
                        }
                    )
                }
            }
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
                        visualTransformation = ThousandsTransformation(prefix = "Rp "),   // titik ribuan otomatis
                        shape = MaterialTheme.shapes.small
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Kosongkan lalu simpan untuk menghapus budget",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val amount = inputText.toLongOrNull() ?: 0L
                    scope.launch {
                        if (amount > 0) budgetDao.upsert(Budget(categoryId = editId, amount = amount))
                        else budgetDao.deleteByCategory(editId)
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

// warna bar berdasarkan persentase terpakai
@Composable
private fun ratioColor(ratio: Float): Color = when {
    ratio > 1f -> MaterialTheme.colorScheme.error
    ratio >= 0.8f -> MaterialTheme.colorScheme.tertiary
    else -> MaterialTheme.colorScheme.primary
}

@Composable
private fun BudgetSummaryCard(remaining: Long, budget: Long, spent: Long, perDay: Long?) {
    val ratio = spent.toFloat() / budget
    val onCard = MaterialTheme.colorScheme.onPrimaryContainer

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = onCard,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(if (remaining >= 0) "Sisa budget" else "Lewat budget", style = MaterialTheme.typography.labelLarge)
            Text(
                formatRupiah(abs(remaining)),
                style = MaterialTheme.typography.headlineMedium.merge(Tabular),
                color = if (remaining >= 0) onCard else MaterialTheme.colorScheme.error
            )
            Text(
                "Terpakai ${formatRupiah(spent)} dari ${formatRupiah(budget)}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            BudgetBar(ratio = ratio, color = ratioColor(ratio), track = onCard.copy(alpha = 0.12f))
            if (perDay != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Sekitar ${formatRupiah(perDay)} per hari sampai akhir bulan",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun BudgetRow(name: String, budget: Long?, spent: Long, onClick: () -> Unit) {
    val visual = categoryVisual(name, TransactionType.PENGELUARAN)
    val ratio = if (budget != null && budget > 0) spent.toFloat() / budget else 0f
    val color = ratioColor(ratio)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(visual.container, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(visual.icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = visual.content)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    if (budget == null) "Belum diatur, terpakai ${formatRupiah(spent)}"
                    else "${formatRupiah(spent)} dari ${formatRupiah(budget)}",
                    style = MaterialTheme.typography.bodySmall.merge(Tabular),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (budget == null) {
                Text("Atur", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            } else {
                Text(formatPercent(ratio * 100.0), style = MaterialTheme.typography.labelLarge.merge(Tabular), color = color)
            }
        }

        if (budget != null) {
            Spacer(modifier = Modifier.height(10.dp))
            // bar menjorok sejajar nama kategori
            Box(modifier = Modifier.padding(start = 52.dp)) {
                BudgetBar(ratio = ratio, color = color)
            }
            if (spent > budget) {
                Text(
                    "Lewat ${formatRupiah(spent - budget)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 52.dp, top = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun BudgetBar(
    ratio: Float,
    color: Color,
    track: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(CircleShape)
            .background(track)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(ratio.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(color, CircleShape)
        )
    }
}
