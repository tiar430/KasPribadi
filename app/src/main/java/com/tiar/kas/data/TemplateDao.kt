package com.tiar.kas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
	@Query("SELECT id, name, type, amount, accountId, toAccountId, categoryId, note, sortOrder FROM templates ORDER BY sortOrder, id")
	fun observeAll(): Flow<List<TransactionTemplate>>

	@Query("SELECT id, name, type, amount, accountId, toAccountId, categoryId, note, sortOrder FROM templates WHERE id = :id")
	suspend fun getById(id: Long): TransactionTemplate?

	@Insert
	suspend fun insert(template: TransactionTemplate): Long

	@Update
	suspend fun update(template: TransactionTemplate)

	@Query("DELETE FROM templates WHERE id = :id")
	suspend fun deleteById(id: Long)

	@Query("SELECT COALESCE(MAX(sortOrder), -1) + 1 FROM templates")
	suspend fun nextSortOrder(): Int

	@Query("UPDATE templates SET name = :name WHERE id = :id")
	suspend fun rename(id: Long, name: String)
}
