package com.tiar.kas.data

import androidx.room.withTransaction

// Pair berisi nama induk dan daftar nama subkategori
private val defaultExpense = listOf(
    "Makan & Minum" to listOf("Sarapan", "Makan Siang", "Makan Malam", "Kopi & Jajan"),
    "Transportasi" to listOf("Bensin", "Parkir", "Ojek Online", "Servis Kendaraan"),
    "Belanja" to listOf("Kebutuhan Rumah", "Pakaian", "Elektronik"),
    "Tagihan" to listOf("Listrik", "Air", "Internet", "Pulsa & Data"),
    "Kesehatan" to listOf("Obat", "Dokter"),
    "Hiburan" to listOf("Langganan", "Liburan"),
    "Lainnya" to emptyList()
)

private val defaultIncome = listOf(
    "Gaji" to emptyList(),
    "Bonus" to emptyList(),
    "Usaha Sampingan" to emptyList(),
    "Lainnya" to emptyList<String>()
)

suspend fun seedIfEmpty(db: AppDatabase) {
    // semua insert di dalam blok ini berhasil bersama atau batal bersama
    db.withTransaction {
        val accountDao = db.accountDao()
        if (accountDao.count() == 0) {
            accountDao.insert(Account(name = "Dompet", accountGroup = AccountGroup.TUNAI))
            accountDao.insert(Account(name = "Rekening Bank", accountGroup = AccountGroup.BANK))
        }

        val categoryDao = db.categoryDao()
        if (categoryDao.count() == 0) {
            insertTree(categoryDao, defaultExpense, CategoryType.PENGELUARAN)
            insertTree(categoryDao, defaultIncome, CategoryType.PEMASUKAN)
        }
    }
}

private suspend fun insertTree(
    dao: CategoryDao,
    tree: List<Pair<String, List<String>>>,
    type: CategoryType
) {
    tree.forEachIndexed { i, (parentName, children) ->      // i adalah nomor urut induk
        val parentId = dao.insert(Category(name = parentName, type = type, sortOrder = i))
        children.forEachIndexed { j, childName ->
            // id induk yang baru dibuat dipakai sebagai parentId
            dao.insert(Category(name = childName, type = type, parentId = parentId, sortOrder = j))
        }
    }
}
