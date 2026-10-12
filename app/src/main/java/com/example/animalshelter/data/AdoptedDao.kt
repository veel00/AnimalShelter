package com.example.animalshelter.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AdoptedDao {

    @Query("SELECT * FROM adopted_pets ORDER BY id DESC")
    fun getAll(): LiveData<List<AdoptedPet>>

    @Insert
    suspend fun insert(pet: AdoptedPet)

    @Query("SELECT COUNT(*) FROM adopted_pets")
    suspend fun getCount(): Int
}