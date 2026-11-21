package com.tiar.kas.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class CategoryType { PENGELUARAN, PEMASUKAN }

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,         // menunjuk ke tabel categories sendiri
            parentColumns = ["id"],           // kolom milik induk
            childColumns = ["parentId"],      // kolom milik subkategori
            onDelete = ForeignKey.CASCADE     // induk dihapus, subkategori ikut terhapus
        )
    ],
    indices = [Index("parentId")]             // mempercepat pencarian subkategori per induk
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: CategoryType,
    val parentId: Long? = null,               // null berarti kategori utama
    val sortOrder: Int = 0
)
