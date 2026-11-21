package com.tiar.kas.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "templates",
	foreignKeys = [
		ForeignKey(entity = Account::class, parentColumns = ["id"], childColumns = ["accountId"], onDelete = ForeignKey.CASCADE),
		ForeignKey(entity = Account::class, parentColumns = ["id"], childColumns = ["toAccountId"], onDelete = ForeignKey.CASCADE),
		ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["categoryId"], onDelete = ForeignKey.SET_NULL)
	],
	indices = [Index("accountId"), Index("toAccountId"), Index("categoryId")]
)
data class TransactionTemplate(
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	val name: String,
	val type: TransactionType,
	val amount: Long,
	val accountId: Long,
	val toAccountId: Long? = null,
	val categoryId: Long? = null,
	val note: String = "",
	val sortOrder: Int = 0
)
