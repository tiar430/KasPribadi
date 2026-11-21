package com.tiar.kas.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT id, name, type, parentId, sortOrder FROM categories ORDER BY sortOrder, id")
    fun observeAll(): Flow<List<Category>>

    @Insert
    suspend fun insert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT COUNT(id) FROM categories")
    suspend fun count(): Int
}
