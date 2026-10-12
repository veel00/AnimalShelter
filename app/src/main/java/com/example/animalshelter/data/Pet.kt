package com.example.animalshelter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    val type: String,
    var hunger: Int = 50,
    var health: Int = 100,
    var mood: Int = 50,
    var isSick: Boolean = false,
    var daysInShelter: Int = 0,
    val photoResId: Int = 0,
    val dailyCost: Int
) {
    val isHungry: Boolean get() = hunger < 30
    val isSad: Boolean get() = mood < 20

    val emoji: String
        get() = when (type) {
            "dog" -> "🐶"
            "rabbit" -> "🐰"
            "goat" -> "🐐"
            else -> "🐾"
        }

    val typeLabel: String
        get() = when (type) {
            "dog" -> "Собака"
            "rabbit" -> "Кролик"
            "goat" -> "Козочка"
            else -> "Питомец"
        }
}