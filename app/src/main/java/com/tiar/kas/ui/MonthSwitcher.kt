package com.tiar.kas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tiar.kas.util.label
import java.time.YearMonth

@Composable
fun MonthSwitcher(
	month: YearMonth,
	onMonthChange: (YearMonth) -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		TextButton(onClick = { onMonthChange(month.minusMonths(1)) }) { Text("<") }
		Text(
			month.label(),
			style = MaterialTheme.typography.titleLarge,
			modifier = Modifier.clickable {onMonthChange(YearMonth.now()) }
		)
		TextButton(onClick = { onMonthChange(month.plusMonths(1)) }) { Text(">") }
	}
}
