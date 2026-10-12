package com.example.animalshelter.util

import android.content.Context
import android.content.SharedPreferences

object DailyTasksManager {

    private const val PREFS_NAME = "daily_tasks"
    private const val KEY_DATE = "task_date"
    private const val KEY_TASKS = "task_ids"
    private const val KEY_PROGRESS = "task_progress"
    private const val KEY_REWARDED = "task_rewarded"

    private const val DAY_MS = 24 * 60 * 60 * 1000L

    val ALL_TASKS = listOf(
        DailyTask("feed_3", "🍖 Покорми 3 питомцев", 3, 50),
        DailyTask("play_2", "🎾 Поиграй 2 раза", 2, 50),
        DailyTask("adopt_1", "🏠 Пристрой 1 питомца", 1, 100),
        DailyTask("buy_1", "🛒 Купи что-то в магазине", 1, 30),
        DailyTask("heal_1", "💊 Вылечи питомца", 1, 40)
    )

    data class DailyTask(
        val id: String,
        val title: String,
        val target: Int,
        val reward: Int
    )

    fun getCurrentTasks(context: Context): List<DailyTask> {
        val prefs = getPrefs(context)
        val savedDate = prefs.getLong(KEY_DATE, 0L)
        val now = System.currentTimeMillis()

        if (now - savedDate >= DAY_MS) {
            val newTasks = ALL_TASKS.shuffled().take(3)
            prefs.edit()
                .putLong(KEY_DATE, now)
                .putString(KEY_TASKS, newTasks.joinToString(",") { it.id })
                .putString(KEY_PROGRESS, newTasks.joinToString(",") { "0" })
                .putString(KEY_REWARDED, newTasks.joinToString(",") { "0" })
                .apply()
            return newTasks
        }

        val ids = prefs.getString(KEY_TASKS, "")?.split(",") ?: return emptyList()
        return ids.mapNotNull { id -> ALL_TASKS.find { it.id == id } }
    }

    fun getProgress(context: Context): List<Int> {
        val prefs = getPrefs(context)
        return prefs.getString(KEY_PROGRESS, "")?.split(",")
            ?.mapNotNull { it.toIntOrNull() } ?: emptyList()
    }

    private fun getRewarded(context: Context): List<Int> {
        val prefs = getPrefs(context)
        return prefs.getString(KEY_REWARDED, "")?.split(",")
            ?.mapNotNull { it.toIntOrNull() } ?: emptyList()
    }

    fun incrementProgress(context: Context, taskId: String): Boolean {
        val tasks = getCurrentTasks(context)
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index == -1) return false

        val prefs = getPrefs(context)
        val progress = getProgress(context).toMutableList()
        val rewarded = getRewarded(context).toMutableList()

        while (progress.size < tasks.size) progress.add(0)
        while (rewarded.size < tasks.size) rewarded.add(0)

        if (rewarded[index] == 1) return false

        progress[index] += 1

        val task = tasks[index]
        val justCompleted = progress[index] >= task.target && rewarded[index] == 0

        if (justCompleted) {
            rewarded[index] = 1
        }

        prefs.edit()
            .putString(KEY_PROGRESS, progress.joinToString(","))
            .putString(KEY_REWARDED, rewarded.joinToString(","))
            .apply()

        return justCompleted
    }

    /**
     * Проверяет, выдана ли награда. Если нет — помечает как выданную и возвращает true.
     */
    fun tryReward(context: Context, taskId: String): Boolean {
        val tasks = getCurrentTasks(context)
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index == -1) return false

        val prefs = getPrefs(context)
        val progress = getProgress(context).toMutableList()
        val rewarded = getRewarded(context).toMutableList()

        while (progress.size < tasks.size) progress.add(0)
        while (rewarded.size < tasks.size) rewarded.add(0)

        val task = tasks[index]
        if (progress[index] >= task.target && rewarded[index] == 0) {
            rewarded[index] = 1
            prefs.edit()
                .putString(KEY_REWARDED, rewarded.joinToString(","))
                .apply()
            return true
        }
        return false
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}