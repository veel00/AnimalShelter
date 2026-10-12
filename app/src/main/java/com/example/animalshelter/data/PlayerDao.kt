package com.example.animalshelter.data

import androidx.room.*

@Dao
interface PlayerDao {

    @Query("SELECT * FROM player_state WHERE id = 1 LIMIT 1")
    suspend fun getState(): PlayerState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(state: PlayerState)

    @Query("UPDATE player_state SET coins = coins + :amount WHERE id = 1")
    suspend fun addCoins(amount: Int)

    @Query("UPDATE player_state SET coins = coins - :amount WHERE id = 1 AND coins >= :amount")
    suspend fun spendCoins(amount: Int): Int
}