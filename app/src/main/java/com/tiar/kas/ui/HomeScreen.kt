package com.tiar.kas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.data.MonthSummary
import com.tiar.kas.ui.theme.Tabular
import com.tiar.kas.util.BackupManager
import com.tiar.kas.util.endMillis
import com.tiar.kas.util.formatRupiah
import com.tiar.kas.util.label
import com.tiar.kas.util.startMillis
import com.tiar.kas.util.toLocalDate
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import kotlin.math.abs

private enum class HomeView { HARIAN, KALENDER }

@Composable
fun HomeScreen(
    db: AppDatabase,
    onAddClick: () -> Unit,
    onRowClick: (Long) -> Unit,
    onManageAccounts: () -> Unit,
    onSearch: () -> Unit,
    onBackup: () -> Unit
) {
    val txDao = db.transactionDao()
    var month by rememberSaveable { mutableStateOf(YearMonth.now()) }
    var view by rememberSaveable { mutableStateOf(HomeView.HARIAN) }
    var selectedDay by rememberSaveable { mutableStateOf<LocalDate?>(LocalDate.now()) }

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
    val totalAssets = balances.filter { it.includeInTotal }.sumOf { it.balance }

    val incomeColor = MaterialTheme.colorScheme.primary
    val expenseColor = MaterialTheme.colorScheme.error

    val context = LocalContext.current
    var reminderHidden by rememberSaveable { mutableStateOf(false) }
    val lastBackup = BackupManager.lastBackup(context)
    val reminderDays = BackupManager.reminderDays(context)
    val daysSince = if (lastBackup > 0) ChronoUnit.DAYS.between(lastBackup.toLocalDate(), LocalDate.now()) else -1L
    val showReminder = !reminderHidden && reminderDays > 0 && (lastBackup == 0L || daysSince >= reminderDays)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Tambah transaksi")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)       // jarak sama antar blok
        ) {
            item(key = "header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kas Pribadi", style = MaterialTheme.typography.headlineSmall)
                    IconButton(onClick = onSearch) {
                        Icon(Icons.Outlined.Search, contentDescription = "Cari transaksi")
                    }
                }
            }

            if (showReminder) {
                item(key = "reminder") {
                    BackupReminder(
                        text = if (lastBackup == 0L) "Belum pernah backup" else "Backup terakhir $daysSince hari lalu",
                        onBackup = onBackup,
                        onLater = { reminderHidden = true }
                    )
                }
            }

            item(key = "balance") {
                BalanceCard(total = totalAssets, summary = summary, month = month, onManageAccounts = onManageAccounts)
            }

            item(key = "month") {
                MonthSwitcher(month = month, onMonthChange = { changeMonth(it) })
            }

            item(key = "modes") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = view == HomeView.HARIAN, onClick = { view = HomeView.HARIAN }, label = { Text("Harian") })
                    FilterChip(selected = view == HomeView.KALENDER, onClick = { view = HomeView.KALENDER }, label = { Text("Kalender") })
                }
            }

            when (view) {
                HomeView.HARIAN -> {
                    if (rows.isEmpty()) {
                        item(key = "empty") { EmptyMonth() }
                    }
                    grouped.forEach { (date, dayRows) ->
                        item(key = "day-$date") {
                            DayCard(date, dayRows, incomeColor, expenseColor, onRowClick)
                        }
                    }
                }

                HomeView.KALENDER -> {
                    item(key = "calendar") {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            shape = MaterialTheme.shapes.medium,
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.padding(8.dp)) {
                                CalendarGrid(
                                    month = month,
                                    grouped = grouped,
                                    selectedDay = selectedDay,
                                    incomeColor = incomeColor,
                                    expenseColor = expenseColor,
                                    onDayClick = { date -> selectedDay = date }
                                )
                            }
                        }
                    }
                    val day = selectedDay
                    if (day == null) {
                        item(key = "hint") {
                            Text(
                                "Ketuk tanggal untuk melihat transaksinya",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        item(key = "selected-$day") {
                            DayCard(day, grouped[day].orEmpty(), incomeColor, expenseColor, onRowClick)
                        }
                    }
                }
            }
        }
    }
}

// kartu hijau utama, satu satunya elemen berwarna kuat di layar
@Composable
private fun BalanceCard(total: Long, summary: MonthSummary, month: YearMonth, onManageAccounts: () -> Unit) {
    val onCard = MaterialTheme.colorScheme.onPrimary
    val soft = onCard.copy(alpha = 0.72f)                  // putih transparan untuk teks pendukung
    val net = summary.income - summary.expense

    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = onCard,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total aset", style = MaterialTheme.typography.labelLarge, color = soft)
                Text(
                    "Kelola",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable(onClick = onManageAccounts)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Text(formatRupiah(total), style = MaterialTheme.typography.headlineMedium.merge(Tabular))

            Spacer(modifier = Modifier.height(16.dp))
            Text(month.label(), style = MaterialTheme.typography.labelMedium, color = soft)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FlowStat(Icons.Outlined.ArrowDownward, "Pemasukan", summary.income, Modifier.weight(1f))
                FlowStat(Icons.Outlined.ArrowUpward, "Pengeluaran", summary.expense, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Selisih ${if (net >= 0) "+" else "-"}${formatRupiah(abs(net))}",
                style = MaterialTheme.typography.labelMedium.merge(Tabular),
                color = soft
            )
        }
    }
}

// kotak kecil transparan di dalam kartu hijau
@Composable
private fun FlowStat(icon: ImageVector, label: String, amount: Long, modifier: Modifier) {
    val onCard = MaterialTheme.colorScheme.onPrimary
    Column(
        modifier = modifier
            .background(onCard.copy(alpha = 0.12f), MaterialTheme.shapes.medium)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(onCard.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = onCard)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = onCard.copy(alpha = 0.72f))
        }
        Spacer(modifier = Modifier.height(6.dp))
        // nominal di baris sendiri supaya muat di layar luar Fold 5
        Text(formatRupiah(amount), style = MaterialTheme.typography.titleSmall.merge(Tabular), maxLines = 1)
    }
}

@Composable
private fun BackupReminder(text: String, onBackup: () -> Unit, onLater: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Backup, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            TextButton(onClick = onLater) { Text("Nanti") }
            TextButton(onClick = onBackup) { Text("Backup") }
        }
    }
}

// tampilan bulan kosong yang mengajak mencatat, bukan sekadar teks
@Composable
private fun EmptyMonth() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.EditNote, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("Belum ada transaksi bulan ini", style = MaterialTheme.typography.titleSmall)
        Text(
            "Ketuk tombol tambah untuk mencatat",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
