package com.tiar.kas.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

	@Insert
	suspend fun insert(tx: MoneyTransaction): Long

	@Update
	suspend fun update(tx: MoneyTransaction)

	@Delete
	suspend fun delete(tx: MoneyTransaction)

	@Query("DELETE FROM transactions")
	suspend fun deleteAll()

	// :id diisi otomatis oleh Room dari parameter id
	@Query("DELETE FROM transactions WHERE id = :id")
	suspend fun deleteById(id: Long)

	// jumlah transaksi yang melibatkan akun ini, sebagai asal atau tujuan transfer
    @Query("SELECT COUNT(id) FROM transactions WHERE accountId = :id OR toAccountId = :id")
    suspend fun countByAccount(id: Long): Int

	// tanda ? karena id yang dicari bisa saja sudah tidak ada
	@Query("SELECT id, date, type, amount, accountId, toAccountId, categoryId, note FROM transactions WHERE id = :id")
	suspend fun getById(id: Long): MoneyTransaction?

	@Query("""
		SELECT t.id, t.date, t.type, t.amount, t.note,
			a.name AS accountName,
			ta.name AS toAccountName,
			c.name AS categoryName
		FROM transactions t
		JOIN accounts a			ON a.id = t.accountId
		LEFT JOIN accounts ta	ON ta.id = t.toAccountId
		LEFT JOIN categories c	ON c.id = t.categoryId
		ORDER BY t.date DESC, t.id DESC
	""")
	fun observeRows(): Flow<List<TransactionRow>>

	@Query("""
		SELECT t.id, t.date, t.type, t.amount, t.note,
			a.name AS accountName,
			ta.name AS toAccountName,
			c.name AS categoryName
		FROM transactions t
		JOIN accounts a			ON a.id = t.accountId
		LEFT JOIN accounts ta	ON ta.id = t.toAccountId
		LEFT JOIN categories c	ON c.id = t.categoryId
		WHERE t.date >= :start AND t.date < :end
		ORDER BY t.date DESC, t.id DESC
	""")
	fun observeRowsBetween(start: Long, end: Long): Flow<List<TransactionRow>>

	// CASE memilih nominal sesuai jenis, transfer menghasilkan 0 di kedua kolom
	@Query("""
		SELECT COALESCE(SUM(CASE WHEN type = 'PEMASUKAN' THEN amount ELSE 0 END), 0) AS income,
			COALESCE(SUM(CASE WHEN type = 'PENGELUARAN' THEN amount ELSE 0 END), 0) AS expense
		FROM transactions
		WHERE date >= :start AND date < :end
	""")
	fun observeSummaryBetween(start: Long, end: Long): Flow<MonthSummary>

	@Query("""
		SELECT t.id, t.date, t.type, t.amount, t.note,
			a.name AS accountName,
			ta.name AS toAccountName,
			c.name AS categoryName
		FROM transactions t
		JOIN accounts a ON a.id = t.accountId
		LEFT JOIN accounts ta ON ta.id = t.toAccountId
		LEFT JOIN categories c ON c.id = t.categoryId
		LEFT JOIN categories pc ON pc.id = c.parentId
		WHERE (:type IS NULL OR t.type = :type)
		AND (:accountId IS NULL OR t.accountId = :accountId OR t.toAccountId = :accountId)
		AND (:categoryId IS NULL OR t.categoryId = :categoryId OR c.parentId = :categoryId)
		AND (:query = ''
		  	OR t.note LIKE '%' || :query || '%'
		  	OR c.name LIKE '%' || :query || '%'
		  	OR pc.name LIKE '%' || :query || '%'
		  	OR a.name LIKE '%' || :query || '%'
		  	OR CAST(t.amount AS TEXT) = :query)
		ORDER BY t.date DESC, t.id DESC
		LIMIT 500
	""")
	fun observeSearch(
		query: String,
		type: String?,
		accountId: Long?,
		categoryId: Long?
	): Flow<List<TransactionRow>>

	@Query("""
		SELECT COALESCE(p.id, c.id, -1) AS categoryId,
			COALESCE(p.name, c.name, 'Tanpa kategori') AS name,
			SUM(t.amount) AS total
		FROM transactions t
		LEFT JOIN categories c ON c.id = t.categoryId
		LEFT JOIN categories p ON p.id = c.parentId
		WHERE t.type = :type
			AND t.date >= :start AND t.date < :end
		GROUP BY COALESCE(p.id, c.id, -1)
		ORDER BY total DESC
	""")
	fun observeCategoryTotals(type: String, start: Long, end: Long): Flow<List<CategoryTotal>>

	// rincian satu kateogri induk, termasuk transaksi yang langsung memakai induknya
	@Query("""
		SELECT c.id AS categoryId, c.name AS name, SUM(t.amount) AS total
		FROM transactions t
		JOIN categories c ON c.id = t.categoryId
		WHERE t.type = :type
			AND t.date >= :start
			AND t.date < :end
			AND (c.id = :parentId OR c.parentId = :parentId)
		GROUP BY c.id
		ORDER BY total DESC
	""")
	fun observeSubcategoryTotals(type: String, parentId: Long, start: Long, end: Long): Flow<List<CategoryTotal>>

	// total perbulan, categoryId null berarti semua kategori
	@Query("""
		SELECT strftime('%Y-%m', t.date / 1000, 'unixepoch', 'localtime') AS ym, SUM(t.amount) AS total
		FROM transactions t
		LEFT JOIN categories c ON c.id = t.categoryId
		WHERE t.type = :type
			AND t.date >= :start
			AND t.date < :end
			AND (:categoryId IS NULL OR c.id = :categoryId OR c.parentId = :categoryId)
		GROUP BY ym
		ORDER BY ym
	""")
	fun observeMonthlyTotals(type: String, categoryId: Long?, start: Long, end: Long): Flow<List<MonthTotal>>
}
