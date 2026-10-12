package com.example.animalshelter.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(context: Context) {

    private val db = AppDatabase.get(context)

    val petDao = db.petDao()
    val inventoryDao = db.inventoryDao()
    val adoptedDao = db.adoptedDao()
    val playerDao = db.playerDao()

    suspend fun ensurePlayerState(): PlayerState = withContext(Dispatchers.IO) {
        playerDao.getState() ?: PlayerState().also { playerDao.save(it) }
    }

    suspend fun savePlayerState(state: PlayerState) = withContext(Dispatchers.IO) {
        playerDao.save(state)
    }

    suspend fun initializeIfNeeded() = withContext(Dispatchers.IO) {
        val state = ensurePlayerState()
        if (!state.isFirstLaunch) return@withContext

        // Стартовый инвентарь
        inventoryDao.upsert(InventoryItem(InventoryItem.FOOD_BASIC, 5))
        inventoryDao.upsert(InventoryItem(InventoryItem.MEDICINE, 1))
        inventoryDao.upsert(InventoryItem(InventoryItem.TOY, 1))

        // Стартовый питомец — Муся
        petDao.insertPet(
            Pet(
                name = "Муся",
                type = "goat",
                hunger = 50,
                health = 100,
                mood = 60,
                dailyCost = 10
            )
        )

        // Снимаем флаг первого запуска
        state.isFirstLaunch = false
        playerDao.save(state)
    }
}