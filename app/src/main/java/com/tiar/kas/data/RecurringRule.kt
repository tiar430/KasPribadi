package com.tiar.kas.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class Frequency { HARIAN, MINGGUAN, BULANAN, TAHUNAN }

@Entity(
	tableName = "recurring_rules",
	foreignKeys = [
		ForeignKey(entity = Account::class, parentColumns = ["id"], childColumns = ["accountId"], onDelete = ForeignKey.CASCADE),
		ForeignKey(entity = Account::class, parentColumns = ["id"], childColumns = ["toAccountId"], onDelete = ForeignKey.CASCADE),
		ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["categoryId"], onDelete = ForeignKey.SET_NULL)
	],
	indices = [Index("accountId"), Index("toAccountId"), Index("categoryId")]
)
data class RecurringRule(
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	val type: TransactionType,
	val amount: Long,
	val accountId: Long,
	val toAccountId: Long? = null,
	val categoryId: Long? = null,
	val note: String = "",
	val frequency: Frequency,
	val startDate: Long,
	val runCount: Int = 0,
	val active: Boolean = true
)
