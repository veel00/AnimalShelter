package com.example.animalshelter.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PetDao {

    @Query("SELECT * FROM pets ORDER BY id DESC")
    fun getAllPets(): LiveData<List<Pet>>

    @Query("SELECT * FROM pets WHERE id = :id LIMIT 1")
    suspend fun getPetById(id: Int): Pet?

    @Query("SELECT * FROM pets")
    suspend fun getAllPetsOnce(): List<Pet>

    @Query("SELECT COUNT(*) FROM pets")
    suspend fun getPetCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: Pet): Long

    @Update
    suspend fun updatePet(pet: Pet)

    @Delete
    suspend fun deletePet(pet: Pet)

    @Query("DELETE FROM pets")
    suspend fun deleteAll()
}