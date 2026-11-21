package com.tiar.kas.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionType { PEMASUKAN, PENGELUARAN, TRANSFER }

@Entity(
	tableName = "transactions",
	foreignKeys = [
		// akun asal dihapus, transaksinya ikut terhapus
		ForeignKey(entity = Account::class, parentColumns = ["id"], childColumns = ["accountId"], onDelete = ForeignKey.CASCADE),
		// akun tujuan transfer dihapus, transaksinya ikut terhapus
		ForeignKey(entity = Account::class, parentColumns = ["id"], childColumns = ["toAccountId"], onDelete = ForeignKey.CASCADE),
		// kategori dihapus, transaksi tetap ada dengan kategori kosong
		ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["categoryId"], onDelete = ForeignKey.SET_NULL)
	],
	indices = [Index("accountId"), Index("toAccountId"), Index("categoryId"), Index("date")]
)
data class MoneyTransaction(
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	val date: Long,		// waktu dalam milidetik sejak 1 Januari 1970
	val type: TransactionType,
	val amount: Long,	// selalu positif, dalam rupiah
	val accountId: Long,	// akun sumber uang
	val toAccountId: Long? = null,	//hanya terisi untuk TRANSFER
	val categoryId: Long? = null,	// kosong untuk TRANSFER
	val note: String = ""
)
