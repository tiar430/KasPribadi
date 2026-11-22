package com.tiar.kas.util

import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

data class CardCycle(
	val lastStatement: LocalDate,
	val nextStatement: LocalDate,
	val dueDate: LocalDate,
	val closedStart: Long,
	val closedEnd: Long,
	val currentStart: Long,
	val currentEnd: Long
)

// tanggal tertentu di bulan itu, 31 di bulan pendek menjadi akhir bulan
private fun YearMonth.dayOrLast(day: Int): LocalDate = atDay(minOf(day, lengthOfMonth()))

// jam 00.00 hari berikutnya, dipakai sebagai batas akhir ekslusif
private fun LocalDate.afterDayMillis(): Long = plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun cardCycle(statementDay: Int, dueDay: Int, today: LocalDate = LocalDate.now()): CardCycle {
	val thisMonth = YearMonth.from(today)
	val candidate = thisMonth.dayOrLast(statementDay)
	val last = if (today.isAfter(candidate)) candidate else thisMonth.minusMonths(1).dayOrLast(statementDay)
	val lastMonth = YearMonth.from(last)
	val previous = lastMonth.minusMonths(1).dayOrLast(statementDay)
	val next = lastMonth.plusMonths(1).dayOrLast(statementDay)

	val due =
		if (dueDay > statementDay) lastMonth.dayOrLast(dueDay)
		else lastMonth.plusMonths(1).dayOrLast(dueDay)

	return CardCycle(
		lastStatement = last,
		nextStatement = next,
		dueDate = due,
		closedStart = previous.afterDayMillis(),
		closedEnd = last.afterDayMillis(),
		currentStart = last.afterDayMillis(),
		currentEnd = next.afterDayMillis()
	)
}
