package com.tiar.kas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tiar.kas.util.formatCompact
import com.tiar.kas.util.shortLabel
import java.time.YearMonth

@Composable
fun BarChart(
    data: List<Pair<YearMonth, Long>>,     // pasangan bulan dan totalnya, urut dari terlama
    selected: YearMonth,                   // bulan yang sedang dilihat, batangnya diberi warna penuh
    barColor: Color,
    onBarClick: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    // nilai terbesar menjadi batang setinggi penuh, minimal 1 supaya tidak membagi dengan 0
    val max = data.maxOfOrNull { it.second }?.takeIf { it > 0 } ?: 1L

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { (ym, value) ->
                Column(
                    modifier = Modifier
                        .weight(1f)                        // enam kolom dengan lebar sama
                        .fillMaxHeight()
                        .clickable { onBarClick(ym) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom   // batang tumbuh dari bawah
                ) {
                    if (value > 0) {
                        Text(formatCompact(value), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)                           // batang 60 persen lebar kolom
                            .fillMaxHeight(value.toFloat() / max)         // tinggi sebanding dengan nilai
                            .background(
                                if (ym == selected) barColor else barColor.copy(alpha = 0.35f),
                                RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                }
            }
        }

        // baris nama bulan di bawah batang
        Row(modifier = Modifier.fillMaxWidth()) {
            data.forEach { (ym, _) ->
                Text(
                    ym.shortLabel(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (ym == selected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
