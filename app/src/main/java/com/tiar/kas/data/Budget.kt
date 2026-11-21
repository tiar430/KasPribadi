package com.tiar.kas.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
	tableName = "budgets",
	foreignKeys = [
		ForeignKey(
			entity = Category::class,
			parentColumns = ["id"],
			childColumns = ["categoryId"],
			onDelete = ForeignKey.CASCADE
		)
	]
)
data class Budget(
	@PrimaryKey
	val categoryId: Long,
	val amount: Long
)
