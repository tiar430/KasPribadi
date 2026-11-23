package com.tiar.kas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.data.MonthSummary
import com.tiar.kas.data.TransactionRow
import com.tiar.kas.data.TransactionType
import com.tiar.kas.util.BackupManager
import com.tiar.kas.util.dayLabel
import com.tiar.kas.util.endMillis
import com.tiar.kas.util.formatRupiah
import com.tiar.kas.util.formatTime
import com.tiar.kas.util.label
import com.tiar.kas.util.startMillis
import com.tiar.kas.util.toLocalDate
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

private enum class HomeView { HARIAN, KALENDER }

@Composable
fun HomeScreen(
    db: AppDatabase,
    onAddClick: () -> Unit,
    onRowClick: (Long) -> Unit,
    onManageAccounts: () -> Unit,
    onSearch: () -> Unit,
    onStats: () -> Unit,
    onBudget: () -> Unit,
    onTemplates: () -> Unit,
    onRecurring: () -> Unit,
    onSecurity: () -> Unit,
    onBackup: () -> Unit
) {
    val txDao = db.transactionDao()
    var month by rememberSaveable { mutableStateOf(YearMonth.now()) }
    var view by rememberSaveable { mutableStateOf(HomeView.HARIAN) }
    var selectedDay by rememberSaveable { mutableStateOf<LocalDate?>(LocalDate.now()) }

    // ganti bulan sekaligus menghapus pilihan tanggal, karena tanggal lama bukan milik bulan baru
    fun changeMonth(newMonth: YearMonth) {
        month = newMonth
        selectedDay = if (newMonth == YearMonth.now()) LocalDate.now() else null
    }

    val balances by remember { db.accountDao().observeBalances() }.collectAsState(initial = emptyList())
    val rows by remember(month) {
        txDao.observeRowsBetween(month.startMillis(), month.endMillis())
    }.collectAsState(initial = emptyList())
    val summary by remember(month) {
        txDao.observeSummaryBetween(month.startMillis(), month.endMillis())
    }.collectAsState(initial = MonthSummary(income = 0, expense = 0))

    val grouped = remember(rows) { rows.groupBy { it.date.toLocalDate() } }

    val incomeColor = MaterialTheme.colorScheme.primary
    val expenseColor = MaterialTheme.colorScheme.error

    val context = LocalContext.current
    var reminderHidden by rememberSaveable { mutableStateOf(false) }
    val lastBackup = BackupManager.lastBackup(context)
    val reminderDays = BackupManager.reminderDays(context)
    val daysSince = if (lastBackup > 0) ChronoUnit.DAYS.between(lastBackup.toLocalDate(), LocalDate.now()) else -1L
    val showReminder = !reminderHidden && reminderDays > 0 && (lastBackup == 0L || daysSince >= reminderDays)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp)
        ) {

        	if (showReminder) {
        		item {
        			Surface(
        				color = MaterialTheme.colorScheme.tertiaryContainer,
        				shape = RoundedCornerShape(12.dp),
        				modifier = Modifier
        					.fillMaxWidth()
        					.padding(bottom = 8.dp)
        			) {
        				Column(modifier = Modifier.padding(12.dp)) {
        					Text(
        						if (lastBackup == 0L) "Belum pernah backup"
        						else "Backup terakhir $daysSince hari lalu", style = MaterialTheme.typography.titleSmall
        					)
        					Row {
        						TextButton(onClick = onBackup) { Text("Backup sekarang") }
        						TextButton(onClick = { reminderHidden = true }) { Text("Nanti") }
        					}
        				}
        			}
        		}
        	}
        
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { changeMonth(month.minusMonths(1)) }) { Text("<") }
                    Text(
                        month.label(),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.clickable { changeMonth(YearMonth.now()) }
                    )
                    TextButton(onClick = { changeMonth(month.plusMonths(1)) }) { Text(">") }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryItem("Pemasukan", summary.income, incomeColor)
                    SummaryItem("Pengeluaran", summary.expense, expenseColor)
                    SummaryItem(
                        "Selisih",
                        summary.income - summary.expense,
                        if (summary.income >= summary.expense) incomeColor else expenseColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Total aset  ${formatRupiah(balances.filter { it.includeInTotal }.sumOf { it.balance })}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = onManageAccounts) { Text("Kelola") }
                }
            }

            // pilihan mode tampilan
            item {
                Row(
                	modifier = Modifier
                		.fillMaxWidth()
                		.horizontalScroll(rememberScrollState()),
                	horizontalArrangement = Arrangement.spacedBy(8.dp),
                	verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = view == HomeView.HARIAN,
                        onClick = { view = HomeView.HARIAN },
                        label = { Text("Harian") },
                    )
                    FilterChip(
                        selected = view == HomeView.KALENDER,
                        onClick = { view = HomeView.KALENDER },
                        label = { Text("Kalender") },
                    )
                    TextButton(onClick = onStats) { Text("Statistik") }
                    TextButton(onClick = onBudget) { Text("Budget") }
                    TextButton(onClick = onTemplates) { Text("Template") }
                    TextButton(onClick = onRecurring) { Text("Berulang") }
                    TextButton(onClick = onSearch) { Text("Cari") }
                    TextButton(onClick = onSecurity) { Text("Keamanan") }
                    TextButton(onClick = onBackup) { Text("Backup") }
                }
            }

            when (view) {
                HomeView.HARIAN -> {
                    if (rows.isEmpty()) {
                        item {
                            Text("Belum ada transaksi di bulan ini", modifier = Modifier.padding(vertical = 16.dp))
                        }
                    }
                    grouped.forEach { (date, dayRows) ->
                        item(key = "day-$date") {
                            DayHeader(date, dayRows, incomeColor, expenseColor)
                        }
                        items(dayRows, key = { it.id }) { r ->
                            TransactionItem(r, incomeColor, expenseColor, onClick = { onRowClick(r.id) })
                        }
                    }
                }

                HomeView.KALENDER -> {
                    item(key = "calendar") {
                        CalendarGrid(
                            month = month,
                            grouped = grouped,
                            selectedDay = selectedDay,
                            incomeColor = incomeColor,
                            expenseColor = expenseColor,
                            onDayClick = { date -> selectedDay = date }
                        )
                    }

                    // salin ke val supaya Kotlin yakin nilainya tidak null di dalam blok else
                    val day = selectedDay
                    if (day == null) {
                        item {
                            Text("Ketuk tanggal untuk melihat transaksinya", modifier = Modifier.padding(vertical = 16.dp))
                        }
                    } else {
                        val dayRows = grouped[day].orEmpty()
                        item(key = "selected-$day") {
                            DayHeader(day, dayRows, incomeColor, expenseColor)
                        }
                        if (dayRows.isEmpty()) {
                            item {
                                Text("Tidak ada transaksi di tanggal ini", modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                        items(dayRows, key = { it.id }) { r ->
                            TransactionItem(r, incomeColor, expenseColor, onClick = { onRowClick(r.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, amount: Long, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Text(formatRupiah(amount), style = MaterialTheme.typography.titleSmall, color = color)
    }
}
