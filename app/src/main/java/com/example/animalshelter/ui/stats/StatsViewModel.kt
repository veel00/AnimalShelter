package com.example.animalshelter.ui.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.animalshelter.data.Achievement
import com.example.animalshelter.data.AdoptedPet
import com.example.animalshelter.data.Repository
import kotlinx.coroutines.launch

class StatsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = Repository(app)

    val adopted: LiveData<List<AdoptedPet>> = repo.adoptedDao.getAll()

    private val _achievements = MutableLiveData<List<Achievement>>()
    val achievements: LiveData<List<Achievement>> = _achievements

    init {
        loadAchievements()
    }

    fun loadAchievements() = viewModelScope.launch {
        val adoptedCount = repo.adoptedDao.getCount()
        val playerState = repo.ensurePlayerState()
        val petsCount = repo.petDao.getPetCount()

        val list = listOf(
            Achievement(
                id = "first_adopt",
                emoji = "🏠",
                title = "Первый дом",
                description = "Пристрой первого питомца",
                current = adoptedCount.coerceAtMost(1),
                target = 1
            ),
            Achievement(
                id = "five_adopt",
                emoji = "💚",
                title = "Доброе сердце",
                description = "Пристрой 5 питомцев",
                current = adoptedCount.coerceAtMost(5),
                target = 5
            ),
            Achievement(
                id = "ten_adopt",
                emoji = "😇",
                title = "Ангел-хранитель",
                description = "Пристрой 10 питомцев",
                current = adoptedCount.coerceAtMost(10),
                target = 10
            ),
            Achievement(
                id = "rich",
                emoji = "💰",
                title = "Богач",
                description = "Накопи 1000 монет",
                current = playerState.coins.coerceAtMost(1000),
                target = 1000
            ),
            Achievement(
                id = "full_house",
                emoji = "🏡",
                title = "Полный дом",
                description = "10 питомцев одновременно",
                current = petsCount.coerceAtMost(10),
                target = 10
            ),
            Achievement(
                id = "collector",
                emoji = "🎯",
                title = "Коллекционер",
                description = "Пристрой 3 питомцев",
                current = adoptedCount.coerceAtMost(3),
                target = 3
            )
        )

        _achievements.postValue(list)
    }
}
