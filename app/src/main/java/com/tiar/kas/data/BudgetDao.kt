package com.tiar.kas.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
	@Query("SELECT categoryId, amount FROM budgets")
	fun observeAll(): Flow<List<Budget>>

	// categoryId belum ada di tabel, barus baru dibuat
	// categoryId sudah ada, nominalnya ditimpa
	@Upsert
	suspend fun upsert(budget: Budget)

	@Query("DELETE FROM budgets WHERE categoryId = :categoryId")
	suspend fun deleteByCategory(categoryId: Long)
}
