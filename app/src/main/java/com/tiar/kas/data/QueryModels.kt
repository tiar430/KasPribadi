package com.tiar.kas.data

data class AccountBalance(
    val id: Long,
    val name: String,
    val accountGroup: AccountGroup,
    val includeInTotal: Boolean,       // baru, untuk menyaring total aset
    val statementDay: Int?,
    val dueDay: Int?,
    val balance: Long
)

data class TransactionRow(
    val id: Long,
    val date: Long,
    val type: TransactionType,
    val amount: Long,
    val note: String,
    val accountName: String,
    val toAccountName: String?,
    val categoryName: String?,
    val photoPath: String?
)

// ringkasa satu rentang waktu
data class MonthSummary(
	val income: Long,
	val expense: Long
)

// total satu kategori induk dalam rentang waktu
data class CategoryTotal(
	val categoryId: Long,
	val name: String,
	val total: Long
)

// total satu bulan, ym berformat 2026-11
data class MonthTotal(
	val ym: String,
	val total: Long
)

// aktivitas satu kartu dalam satu periode
data class CardActivity(
	val charges: Long,		// belanja dikurangi refund
	val payments: Long		// transfer masuk ke kartu
)

// satu baris ekspor, kategori induk dan subkategori dipisah
data class ExportRow(
	val date: Long,
	val type: TransactionType,
	val amount: Long,
	val note: String,
	val accountName: String,
	val toAccountName: String?,
	val categoryName: String?,
	val subcategoryName: String?
)
