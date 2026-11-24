package com.tiar.kas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.TransactionRow
import com.tiar.kas.data.TransactionType
import com.tiar.kas.ui.theme.Tabular
import com.tiar.kas.util.dayLabel
import com.tiar.kas.util.formatRupiah
import com.tiar.kas.util.formatTime
import java.time.LocalDate

// judul hari tanpa garis, total di kanan
@Composable
fun DayHeader(
    date: LocalDate,
    dayRows: List<TransactionRow>,
    incomeColor: Color,
    expenseColor: Color
) {
    val income = dayRows.filter { it.type == TransactionType.PEMASUKAN }.sumOf { it.amount }
    val expense = dayRows.filter { it.type == TransactionType.PENGELUARAN }.sumOf { it.amount }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(date.dayLabel(), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (income > 0) Text("+${formatRupiah(income)}", style = MaterialTheme.typography.labelMedium.merge(Tabular), color = incomeColor)
            if (expense > 0) Text("-${formatRupiah(expense)}", style = MaterialTheme.typography.labelMedium.merge(Tabular), color = expenseColor)
        }
    }
}

// satu hari dalam satu kartu putih
@Composable
fun DayCard(
    date: LocalDate,
    dayRows: List<TransactionRow>,
    incomeColor: Color,
    expenseColor: Color,
    onRowClick: (Long) -> Unit
) {
    Column {
        DayHeader(date, dayRows, incomeColor, expenseColor)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),   // garis tepi setipis rambut
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                if (dayRows.isEmpty()) {
                    Text(
                        "Tidak ada transaksi di tanggal ini",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                dayRows.forEachIndexed { index, r ->
                    // garis mulai setelah ikon, bukan dari tepi kartu
                    if (index > 0) HorizontalDivider(modifier = Modifier.padding(start = 68.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    TransactionItem(r, incomeColor, expenseColor, onClick = { onRowClick(r.id) })
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    r: TransactionRow,
    incomeColor: Color,
    expenseColor: Color,
    onClick: () -> Unit
) {
    val visual = categoryVisual(r.categoryName, r.type)
    val title = if (r.type == TransactionType.TRANSFER) "Transfer" else r.categoryName ?: "Tanpa kategori"

    // akun, jam, dan catatan digabung dalam satu baris kecil
    val detail = buildList {
        add(if (r.type == TransactionType.TRANSFER) "${r.accountName} ke ${r.toAccountName}" else r.accountName)
        add(formatTime(r.date))
        if (r.note.isNotBlank()) add(r.note)
    }.joinToString("  ·  ")

    val sign = when (r.type) {
        TransactionType.PEMASUKAN -> "+"
        TransactionType.PENGELUARAN -> "-"
        TransactionType.TRANSFER -> ""
    }
    val amountColor = when (r.type) {
        TransactionType.PEMASUKAN -> incomeColor
        TransactionType.PENGELUARAN -> expenseColor
        TransactionType.TRANSFER -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            Text(title, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis     // teks terlalu panjang diakhiri titik tiga
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text("$sign${formatRupiah(r.amount)}", style = MaterialTheme.typography.titleSmall.merge(Tabular), color = amountColor)
            if (r.photoPath != null) {
                Icon(
                    Icons.Outlined.PhotoCamera,
                    contentDescription = "Ada foto struk",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
