package com.tiar.kas.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// daftar tabel dan versi skema, versi naik setiap struktur tabel berubah
@Database(
    entities = [
    	Account::class,
    	Category::class,
    	MoneyTransaction::class,
    	Budget::class,
    	TransactionTemplate::class,
    	RecurringRule::class
    ],
    version = 8,
    exportSchema = true,                    // 1. ubah dari false ke true
    autoMigrations = [
    	AutoMigration(from = 3, to = 4),
    	AutoMigration(from = 4, to = 5),
    	AutoMigration(from = 5, to = 6),
    	AutoMigration(from = 6, to = 7),
    	AutoMigration(from = 7, to = 8)
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun templateDao(): TemplateDao
    abstract fun recurringDao(): RecurringDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kas.db"
                )
                    // 2. baris fallbackToDestructiveMigration() dihapus
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
