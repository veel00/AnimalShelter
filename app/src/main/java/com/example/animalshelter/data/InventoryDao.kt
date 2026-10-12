package com.example.animalshelter.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface InventoryDao {

    @Query("SELECT * FROM inventory")
    fun getAll(): LiveData<List<InventoryItem>>

    @Query("SELECT * FROM inventory")
    suspend fun getAllOnce(): List<InventoryItem>

    @Query("SELECT quantity FROM inventory WHERE itemName = :name LIMIT 1")
    suspend fun getQuantity(name: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: InventoryItem)

    @Transaction
    suspend fun add(name: String, amount: Int) {
        val current = getQuantity(name) ?: 0
        upsert(InventoryItem(name, current + amount))
    }

    @Query("UPDATE inventory SET quantity = quantity - 1 WHERE itemName = :name AND quantity > 0")
    suspend fun consumeOne(name: String): Int

    @Query("DELETE FROM inventory")
    suspend fun clear()
}