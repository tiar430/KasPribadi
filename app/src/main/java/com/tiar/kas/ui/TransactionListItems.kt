package com.tiar.kas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.TransactionRow
import com.tiar.kas.data.TransactionType
import com.tiar.kas.util.dayLabel
import com.tiar.kas.util.formatRupiah
import com.tiar.kas.util.formatTime
import java.time.LocalDate

@Composable
fun DayHeader(
    date: LocalDate,
    dayRows: List<TransactionRow>,
    incomeColor: Color,
    expenseColor: Color
) {
    val income = dayRows.filter { it.type == TransactionType.PEMASUKAN }.sumOf { it.amount }
    val expense = dayRows.filter { it.type == TransactionType.PENGELUARAN }.sumOf { it.amount }

    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(date.dayLabel(), style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (income > 0) Text(formatRupiah(income), color = incomeColor, style = MaterialTheme.typography.bodySmall)
                if (expense > 0) Text(formatRupiah(expense), color = expenseColor, style = MaterialTheme.typography.bodySmall)
            }
        }
        HorizontalDivider(thickness = 2.dp)
    }
}

@Composable
fun TransactionItem(
    r: TransactionRow,
    incomeColor: Color,
    expenseColor: Color,
    onClick: () -> Unit
) {
    val detail = when (r.type) {
        TransactionType.TRANSFER -> "${r.accountName} ke ${r.toAccountName}"
        else -> "${r.categoryName ?: "Tanpa kategori"} | ${r.accountName}"
    }
    val sign = when (r.type) {
        TransactionType.PEMASUKAN -> "+"
        TransactionType.PENGELUARAN -> "-"
        TransactionType.TRANSFER -> ""
    }
    val amountColor = when (r.type) {
        TransactionType.PEMASUKAN -> incomeColor
        TransactionType.PENGELUARAN -> expenseColor
        TransactionType.TRANSFER -> Color.Unspecified
    }
    val subline = if (r.note.isBlank()) formatTime(r.date) else "${formatTime(r.date)}  ${r.note}"

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(detail)
                Text(subline, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                "$sign${formatRupiah(r.amount)}",
                style = MaterialTheme.typography.titleSmall,
                color = amountColor
            )
        }
        HorizontalDivider()
    }
}
