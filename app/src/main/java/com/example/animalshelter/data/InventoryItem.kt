package com.example.animalshelter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory")
data class InventoryItem(
    @PrimaryKey val itemName: String,
    var quantity: Int = 0
) {
    companion object {
        const val FOOD_BASIC = "food_basic"
        const val FOOD_PREMIUM = "food_premium"
        const val MEDICINE = "medicine"
        const val TOY = "toy"

        //val ALL = listOf(FOOD_BASIC, FOOD_PREMIUM, MEDICINE, TOY)
    }
}