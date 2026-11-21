package com.tiar.kas.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT id, name, accountGroup, initialBalance, includeInTotal, sortOrder FROM accounts ORDER BY sortOrder, id")
    fun observeAll(): Flow<List<Account>>

    @Query("""
        SELECT a.id, a.name, a.accountGroup, a.includeInTotal,
               a.initialBalance
               + COALESCE((SELECT SUM(CASE WHEN t.type = 'PEMASUKAN' THEN t.amount ELSE -t.amount END)
                           FROM transactions t
                           WHERE t.accountId = a.id), 0)
               + COALESCE((SELECT SUM(t.amount)
                           FROM transactions t
                           WHERE t.type = 'TRANSFER' AND t.toAccountId = a.id), 0)
               AS balance
        FROM accounts a
        ORDER BY a.sortOrder, a.id
    """)
    fun observeBalances(): Flow<List<AccountBalance>>

    // baru, mengambil satu akun untuk form edit
    @Query("SELECT id, name, accountGroup, initialBalance, includeInTotal, sortOrder FROM accounts WHERE id = :id")
    suspend fun getById(id: Long): Account?

    // baru, nomor urut untuk akun baru supaya tampil paling bawah
    // MAX kosong saat belum ada akun, COALESCE mengubahnya jadi -1 sehingga hasilnya 0
    @Query("SELECT COALESCE(MAX(sortOrder), -1) + 1 FROM accounts")
    suspend fun nextSortOrder(): Int

    @Insert
    suspend fun insert(account: Account): Long

    @Update
    suspend fun update(account: Account)

    @Delete
    suspend fun delete(account: Account)

    // baru, transaksi milik akun ini ikut terhapus lewat CASCADE
    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(id) FROM accounts")
    suspend fun count(): Int
}
