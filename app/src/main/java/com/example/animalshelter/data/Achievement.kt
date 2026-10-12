package com.example.animalshelter.data

data class Achievement(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val current: Int,
    val target: Int
) {
    val unlocked: Boolean get() = current >= target
    val progressText: String get() = if (unlocked) "✅ Получено" else "$current/$target"
}