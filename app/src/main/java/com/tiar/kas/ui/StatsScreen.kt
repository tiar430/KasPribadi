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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.data.CategoryTotal
import com.tiar.kas.data.TransactionType
import com.tiar.kas.ui.theme.Tabular
import com.tiar.kas.util.endMillis
import com.tiar.kas.util.formatPercent
import com.tiar.kas.util.formatRupiah
import com.tiar.kas.util.startMillis
import java.time.YearMonth
import kotlinx.coroutines.flow.flowOf

private val chartPalette = listOf(
    Color(0xFF0F6E56), Color(0xFF378ADD), Color(0xFFEF9F27), Color(0xFFD4537E), Color(0xFF7F77DD),
    Color(0xFF1D9E75), Color(0xFFD85A30), Color(0xFF85B7EB), Color(0xFF888780), Color(0xFF97C459)
)

private fun colorAt(index: Int): Color = chartPalette[index % chartPalette.size]

private const val NO_CATEGORY = -1L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(db: AppDatabase) {
    val dao = db.transactionDao()

    var month by rememberSaveable { mutableStateOf(YearMonth.now()) }
    var type by rememberSaveable { mutableStateOf(TransactionType.PENGELUARAN) }
    var expandedId by rememberSaveable(type) { mutableStateOf<Long?>(null) }

    val totals by remember(month, type) {
        dao.observeCategoryTotals(type.name, month.startMillis(), month.endMillis())
    }.collectAsState(initial = emptyList())

    val subs by remember(month, type, expandedId) {
        val id = expandedId
        if (id == null) flowOf(emptyList<CategoryTotal>())
        else dao.observeSubcategoryTotals(type.name, id, month.startMillis(), month.endMillis())
    }.collectAsState(initial = emptyList())

    val trendStart = month.minusMonths(5)
    val monthly by remember(month, type, expandedId) {
        dao.observeMonthlyTotals(type.name, expandedId, trendStart.startMillis(), month.endMillis())
    }.collectAsState(initial = emptyList())

    val trend = remember(monthly, month) {
        val byMonth = monthly.associate { it.ym to it.total }
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
    val typeOptions = listOf(TransactionType.PENGELUARAN to "Pengeluaran", TransactionType.PEMASUKAN to "Pemasukan")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "header") { ScreenHeader("Statistik") }

        item(key = "month") { MonthSwitcher(month = month, onMonthChange = { month = it }) }

        item(key = "type") {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                typeOptions.forEachIndexed { index, (t, text) ->
                    SegmentedButton(
                        selected = type == t,
                        onClick = { type = t },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = typeOptions.size),
                        label = { Text(text) }
                    )
                }
            }
        }

        item(key = "donut") {
            FormCard {
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
                            modifier = Modifier.size(200.dp)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                typeOptions.first { it.first == type }.second,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(formatRupiah(grandTotal), style = MaterialTheme.typography.titleLarge.merge(Tabular))
                            Text(
                                "${totals.size} kategori",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            "Belum ada data di bulan ini",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item(key = "trend") {
            FormCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tren 6 bulan", style = MaterialTheme.typography.titleSmall)
                    Text(
                        expandedName ?: "Semua kategori",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    BarChart(data = trend, selected = month, barColor = mainColor, onBarClick = { month = it })
                }
            }
        }

        if (totals.isNotEmpty()) {
            item(key = "categories") {
                FormCard {
                    totals.forEachIndexed { index, row ->
                        if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        CategoryStatRow(
                            row = row,
                            color = colorAt(index),
                            grandTotal = grandTotal,
                            isExpanded = expandedId == row.categoryId,
                            subs = if (expandedId == row.categoryId) subs else emptyList(),
                            onToggle = {
                                expandedId = if (expandedId == row.categoryId) null else row.categoryId
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryStatRow(
    row: CategoryTotal,
    color: Color,
    grandTotal: Long,
    isExpanded: Boolean,
    subs: List<CategoryTotal>,
    onToggle: () -> Unit
) {
    val canExpand = row.categoryId != NO_CATEGORY
    val fraction = if (grandTotal > 0) row.total.toFloat() / grandTotal else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = canExpand, onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                row.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(formatRupiah(row.total), style = MaterialTheme.typography.titleSmall.merge(Tabular))
            if (canExpand) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = if (isExpanded) "Tutup rincian" else "Buka rincian",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // lintasan abu abu
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                // isian berwarna selebar porsi kategori
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(color, CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                formatPercent(fraction * 100.0),
                style = MaterialTheme.typography.labelMedium.merge(Tabular),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isExpanded) {
            subs.forEach { sub ->
                val subName =
                    if (sub.categoryId == row.categoryId) "${sub.name} (tanpa subkategori)"
                    else sub.name
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(subName, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text(
                        formatPercent(sub.total * 100.0 / row.total),
                        style = MaterialTheme.typography.labelMedium.merge(Tabular),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(formatRupiah(sub.total), style = MaterialTheme.typography.bodyMedium.merge(Tabular))
                }
            }
        }
    }
}
