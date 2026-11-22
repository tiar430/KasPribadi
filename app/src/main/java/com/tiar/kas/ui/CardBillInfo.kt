package com.tiar.kas.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.data.CardActivity
import com.tiar.kas.util.cardCycle
import com.tiar.kas.util.dayLabel
import com.tiar.kas.util.formatRupiah
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun CardBillInfo(db: AppDatabase, cardId: Long, statementDay: Int, dueDay: Int, onPay: (Long) -> Unit) {
	val dao = db.transactionDao()
	val cycle = remember(statementDay, dueDay) {
		cardCycle(statementDay, dueDay)
	}
	val closed by remember(cardId, cycle) {
		dao.observeCardActivity(cardId, cycle.closedStart, cycle.closedEnd)
	}.collectAsState(initial = CardActivity(charges = 0, payments = 0))
	val current by remember(cardId, cycle) {
		dao.observeCardActivity(cardId, cycle.currentStart, cycle.currentEnd)
	}.collectAsState(initial = CardActivity(charges = 0, payments = 0))

	val bill = closed.charges.coerceAtLeast(0)
	val remaining = (bill - current.payments).coerceAtLeast(0)
	val daysToDue = ChronoUnit.DAYS.between(LocalDate.now(), cycle.dueDate)

	val statusText = when {
		bill == 0L -> "Tidak ada tagihan periode ini"
		remaining == 0L -> "Lunas"
		daysToDue < 0 -> "Lewat jatuh tempo ${-daysToDue} hari, sisa ${formatRupiah(remaining)}"
		else -> "Sisa ${formatRupiah(remaining)}, jatuh tempo ${cycle.dueDate.dayLabel()}, $daysToDue hari lagi"
	}
	val statusColor =
		if (remaining > 0 && daysToDue <= 3) MaterialTheme.colorScheme.error
		else Color.Unspecified

	Column(modifier = Modifier.padding(top = 4.dp)) {
		Text(
			"Tagihan cetak ${cycle.lastStatement.dayLabel()} ${formatRupiah(bill)}",
			style = MaterialTheme.typography.bodySmall
		)
		Text(statusText, style = MaterialTheme.typography.bodySmall, color = statusColor)
		Text(
			"Belum ditagih ${formatRupiah(current.charges.coerceAtLeast(0))}, cetak berikutnya ${cycle.nextStatement.dayLabel()}",
			style = MaterialTheme.typography.bodySmall
		)
		if (remaining > 0) {
			TextButton(onClick = { onPay(remaining) }) { Text("Bayar tagihan") }
		}
	}
}
