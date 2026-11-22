package com.tiar.kas.data

import androidx.room.withTransaction
import java.time.Instant
import java.time.ZoneId

private const val MAX_CATCH_UP = 400

fun RecurringRule.occurrenceAt(n: Int): Long {
	val start = Instant.ofEpochMilli(startDate).atZone(ZoneId.systemDefault())
	val step = n.toLong()
	val next = when (frequency) {
		Frequency.HARIAN -> start.plusDays(step)
		Frequency.MINGGUAN -> start.plusWeeks(step)
		Frequency.BULANAN -> start.plusMonths(step)
		Frequency.TAHUNAN -> start.plusYears(step)
	}
	return next.toInstant().toEpochMilli()
}

fun RecurringRule.nextRun(): Long = occurrenceAt(runCount)
fun RecurringRule.skipMissed(now: Long = System.currentTimeMillis()): Int {
	var n = runCount
	while (occurrenceAt(n) <= now && n - runCount < 100_000) n++
	return n
}
suspend fun processRecurring(db: AppDatabase, now: Long = System.currentTimeMillis()): Int {
	var created = 0
	db.withTransaction {
		val recurringDao = db.recurringDao()
		val txDao = db.transactionDao()

		recurringDao.getActive().forEach { rule ->
			var n = rule.runCount
			var guard = 0
			while (rule.occurrenceAt(n) <= now && guard < MAX_CATCH_UP) {
				txDao.insert(
					MoneyTransaction(
						date = rule.occurrenceAt(n),
						type = rule.type,
						amount = rule.amount,
						accountId = rule.accountId,
						toAccountId = rule.toAccountId,
						categoryId = rule.categoryId,
						note = rule.note
					)
				)
				n++
				guard++
				created++
			}
			if (n != rule.runCount) recurringDao.updateRunCount(rule.id, n)
		}
	}
	return created
}
