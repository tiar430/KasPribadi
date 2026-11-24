package com.tiar.kas.ui

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.data.CategoryTotal
import com.tiar.kas.data.TransactionType
import com.tiar.kas.util.endMillis
import com.tiar.kas.util.formatPercent
import com.tiar.kas.util.formatRupiah
import com.tiar.kas.util.startMillis
import java.time.YearMonth
import kotlinx.coroutines.flow.flowOf

private val chartPalette = listOf(
    Color(0xFFE57373), Color(0xFF64B5F6), Color(0xFF81C784), Color(0xFFFFB74D), Color(0xFFBA68C8),
    Color(0xFF4DB6AC), Color(0xFFF06292), Color(0xFFA1887F), Color(0xFF90A4AE), Color(0xFFDCE775)
)

private fun colorAt(index: Int): Color = chartPalette[index % chartPalette.size]

// id -1 dipakai query untuk transaksi yang kategorinya sudah dihapus
private const val NO_CATEGORY = -1L

@Composable
fun StatsScreen(db: AppDatabase, onBack: () -> Unit) {
    val dao = db.transactionDao()

    var month by rememberSaveable { mutableStateOf(YearMonth.now()) }
    var type by rememberSaveable { mutableStateOf(TransactionType.PENGELUARAN) }

    // kunci type membuat rincian tertutup saat berpindah Pengeluaran dan Pemasukan
    var expandedId by rememberSaveable(type) { mutableStateOf<Long?>(null) }

    val totals by remember(month, type) {
        dao.observeCategoryTotals(type.name, month.startMillis(), month.endMillis())
    }.collectAsState(initial = emptyList())

    val subs by remember(month, type, expandedId) {
        val id = expandedId
        if (id == null) flowOf(emptyList<CategoryTotal>())     // tidak ada yang dibuka, tidak perlu query
        else dao.observeSubcategoryTotals(type.name, id, month.startMillis(), month.endMillis())
    }.collectAsState(initial = emptyList())

    // tren dari 5 bulan sebelumnya sampai bulan yang dilihat
    val trendStart = month.minusMonths(5)
    val monthly by remember(month, type, expandedId) {
        dao.observeMonthlyTotals(type.name, expandedId, trendStart.startMillis(), month.endMillis())
    }.collectAsState(initial = emptyList())

    // ubah hasil query menjadi 6 bulan lengkap, bulan tanpa data bernilai 0
    val trend = remember(monthly, month) {
        val byMonth = monthly.associate { it.ym to it.total }   // peta "2026-11" ke total
        (0..5).map { i ->
            val ym = trendStart.plusMonths(i.toLong())
            ym to (byMonth[ym.toString()] ?: 0L)
        }
    }

    val grandTotal = totals.sumOf { it.total }
    val expandedName = totals.firstOrNull { it.categoryId == expandedId }?.name
    val mainColor =
        if (type == TransactionType.PENGELUARAN) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.primary

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
        	ScreenHeader("Statistik")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Statistik", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = onBack) { Text("Kembali") }
            }
        }

        item {
            MonthSwitcher(month = month, onMonthChange = { month = it })
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = type == TransactionType.PENGELUARAN,
                    onClick = { type = TransactionType.PENGELUARAN },
                    label = { Text("Pengeluaran") }
                )
                FilterChip(
                    selected = type == TransactionType.PEMASUKAN,
                    onClick = { type = TransactionType.PEMASUKAN },
                    label = { Text("Pemasukan") }
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                if (grandTotal > 0) {
                    DonutChart(
                        values = totals.map { it.total },
                        colors = totals.indices.map { colorAt(it) },
                        modifier = Modifier.size(220.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total", style = MaterialTheme.typography.bodySmall)
                        Text(formatRupiah(grandTotal), style = MaterialTheme.typography.titleMedium)
                    }
                } else {
                    Text("Belum ada data di bulan ini")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Tren 6 bulan, ${expandedName ?: "semua kategori"}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            BarChart(
                data = trend,
                selected = month,
                barColor = mainColor,
                onBarClick = { month = it }           // ketuk batang untuk pindah bulan
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        itemsIndexed(totals, key = { _, row -> row.categoryId }) { index, row ->
            val percent = row.total * 100.0 / grandTotal
            val canExpand = row.categoryId != NO_CATEGORY
            val isExpanded = expandedId == row.categoryId

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        // enabled false membuat baris Tanpa kategori tidak bisa diketuk
                        .clickable(enabled = canExpand) {
                            expandedId = if (isExpanded) null else row.categoryId   // ketuk lagi untuk menutup
                        }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(colorAt(index), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(row.name, modifier = Modifier.weight(1f))
                    Text(formatPercent(percent), style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(formatRupiah(row.total), style = MaterialTheme.typography.titleSmall)
                    if (canExpand) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isExpanded) "▲" else "▼", style = MaterialTheme.typography.bodySmall)
                    }
                }

                if (isExpanded) {
                    subs.forEach { sub ->
                        // transaksi yang memakai kategori induk langsung, bukan subkategorinya
                        val subName =
                            if (sub.categoryId == row.categoryId) "${sub.name} (tanpa subkategori)"
                            else sub.name

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, top = 4.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(subName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            // persen terhadap total induknya, bukan total bulan
                            Text(formatPercent(sub.total * 100.0 / row.total), style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(formatRupiah(sub.total), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}
