package com.tiar.kas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.TransactionRow
import com.tiar.kas.data.TransactionType
import com.tiar.kas.util.formatCompact
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

private val weekdayLabels = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

@Composable
fun CalendarGrid(
    month: YearMonth,
    grouped: Map<LocalDate, List<TransactionRow>>,   // hasil groupBy dari HomeScreen
    selectedDay: LocalDate?,
    incomeColor: Color,
    expenseColor: Color,
    onDayClick: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val daysInMonth = month.lengthOfMonth()            // 28 sampai 31
    // dayOfWeek.value Senin = 1 sampai Minggu = 7, dikurangi 1 menjadi jumlah kotak kosong
    val offset = month.atDay(1).dayOfWeek.value - 1
    val weeks = (offset + daysInMonth + 6) / 7         // pembulatan ke atas tanpa pecahan

    Column(modifier = Modifier.fillMaxWidth()) {
        // baris nama hari
        Row(modifier = Modifier.fillMaxWidth()) {
            weekdayLabels.forEach { name ->
                Text(
                    name,
                    modifier = Modifier.weight(1f),    // tujuh kolom dengan lebar sama
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        for (week in 0 until weeks) {                  // 0 until 5 artinya 0, 1, 2, 3, 4
            Row(modifier = Modifier.fillMaxWidth()) {
                for (column in 0 until 7) {
                    // nomor tanggal untuk kotak ini, bisa di luar rentang untuk kotak kosong
                    val dayNumber = week * 7 + column - offset + 1

                    if (dayNumber in 1..daysInMonth) {
                        val date = month.atDay(dayNumber)
                        DayCell(
                            date = date,
                            rows = grouped[date].orEmpty(),   // orEmpty mengganti null dengan list kosong
                            isSelected = date == selectedDay,
                            isToday = date == today,
                            incomeColor = incomeColor,
                            expenseColor = expenseColor,
                            modifier = Modifier.weight(1f),
                            onClick = { onDayClick(date) }
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))   // kotak kosong tetap mengambil satu kolom
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    rows: List<TransactionRow>,
    isSelected: Boolean,
    isToday: Boolean,
    incomeColor: Color,
    expenseColor: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val income = rows.filter { it.type == TransactionType.PEMASUKAN }.sumOf { it.amount }
    val expense = rows.filter { it.type == TransactionType.PENGELUARAN }.sumOf { it.amount }

    val borderColor =
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outlineVariant
    val dayColor =
        if (date.dayOfWeek == DayOfWeek.SUNDAY) expenseColor   // tanggal Minggu berwarna merah
        else Color.Unspecified

    Column(
        modifier = modifier
            .height(64.dp)
            .padding(1.dp)                                     // celah antar kotak
            .border(if (isSelected) 2.dp else 0.5.dp, borderColor)
            .background(
                if (isToday) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(2.dp)                                     // jarak isi dari garis kotak
    ) {
        Text(date.dayOfMonth.toString(), style = MaterialTheme.typography.labelSmall, color = dayColor)
        if (income > 0) {
            Text(formatCompact(income), color = incomeColor, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        }
        if (expense > 0) {
            Text(formatCompact(expense), color = expenseColor, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        }
    }
}
