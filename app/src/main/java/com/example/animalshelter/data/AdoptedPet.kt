package com.example.animalshelter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adopted_pets")
data class AdoptedPet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: String,
    val photoResId: Int = 0,
    val familyName: String,
    val familyPhone: String,
    val notes: String = "",
    val adoptionFee: Int = 200,
    val wasSick: Boolean = false,
    val emoji: String = "🐾"
)