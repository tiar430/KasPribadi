package com.tiar.kas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringDao {
	@Query("SELECT id, type, amount, accountId, toAccountId, categoryId, note, frequency, startDate, runCount, active FROM recurring_rules ORDER by id")
	fun observeAll(): Flow<List<RecurringRule>>

	@Query("SELECT id, type, amount, accountId, toAccountId, categoryId, note, frequency, startDate, runCount, active FROM recurring_rules WHERE active = 1")
	suspend fun getActive(): List<RecurringRule>

	@Insert
	suspend fun insert(rule: RecurringRule): Long

	@Query("UPDATE recurring_rules SET runCount = :runCount WHERE id = :id")
	suspend fun updateRunCount(id: Long, runCount: Int)

	@Query("UPDATE recurring_rules SET active = :active WHERE id = :id")
	suspend fun setActive(id: Long, active: Boolean)

	@Query("UPDATE recurring_rules SET active = 1, runCount = :runCount WHERE id = :id")
	suspend fun resume(id: Long, runCount: Int)

	@Query("DELETE FROM recurring_rules WHERE id = :id")
	suspend fun deleteById(id: Long)
}
