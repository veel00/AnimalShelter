package com.example.animalshelter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_state")
data class PlayerState(
    @PrimaryKey val id: Int = 1,
    var coins: Int = 500,
    var lastLoginTime: Long = System.currentTimeMillis(),
    var totalAdopted: Int = 0,
    var isFirstLaunch: Boolean = true,
    var lastChargeTime: Long = 0L
)