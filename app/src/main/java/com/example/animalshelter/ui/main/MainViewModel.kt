package com.example.animalshelter.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.animalshelter.data.InventoryItem
import com.example.animalshelter.data.Pet
import com.example.animalshelter.data.Repository
import com.example.animalshelter.util.DailyTasksManager
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = Repository(app)

    val pets: LiveData<List<Pet>> = repo.petDao.getAllPets()

    private val _coins = MutableLiveData<Int>()
    val coins: LiveData<Int> = _coins

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _dailyTasks = MutableLiveData<List<DailyTasksManager.DailyTask>>()
    val dailyTasks: LiveData<List<DailyTasksManager.DailyTask>> = _dailyTasks

    private val _taskProgress = MutableLiveData<List<Int>>()
    val taskProgress: LiveData<List<Int>> = _taskProgress

    init {
        viewModelScope.launch {
            repo.initializeIfNeeded()
            checkDailyBonus()
            checkSponsors()
            checkRandomEvent()
            chargeDailyCost()
            refreshCoins()
            refreshDailyTasks()
        }
    }

    fun refreshCoins() = viewModelScope.launch {
        val state = repo.ensurePlayerState()
        _coins.postValue(state.coins)
    }

    // ─────────────────────────────────────────────
    //  ЕЖЕДНЕВНЫЕ ЗАДАНИЯ
    // ─────────────────────────────────────────────

    fun refreshDailyTasks() = viewModelScope.launch {
        val context = getApplication<Application>()
        val tasks = DailyTasksManager.getCurrentTasks(context)

        // Проверяем выполненные — выдаём награды
        tasks.forEach { task ->
            val done = DailyTasksManager.tryReward(context, task.id)
            if (done) {
                val state = repo.ensurePlayerState()
                state.coins += task.reward
                repo.savePlayerState(state)
                _message.postValue("✅ ${task.title}: +${task.reward} 💰")
            }
        }

        _dailyTasks.postValue(tasks)
        _taskProgress.postValue(DailyTasksManager.getProgress(context))
        refreshCoins()
    }

    // ─────────────────────────────────────────────
    //  ЕЖЕДНЕВНЫЙ БОНУС + СПОНСОРЫ
    // ─────────────────────────────────────────────

    private suspend fun checkDailyBonus() {
        val state = repo.ensurePlayerState()
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L

        if (now - state.lastLoginTime >= dayMs) {
            state.coins += 50
            state.lastLoginTime = now
            repo.savePlayerState(state)
            _message.postValue("🎁 Ежедневный бонус: +50 монет!")
        }
    }

    private suspend fun checkSponsors() {
        if ((1..100).random() <= 30) {
            val state = repo.ensurePlayerState()
            state.coins += 100
            repo.savePlayerState(state)
            _message.postValue("💚 Фонд «Добрые руки» перевёл 100 монет!")
        }
    }

    // ─────────────────────────────────────────────
    //  СПИСАНИЕ dailyCost
    // ─────────────────────────────────────────────

    private suspend fun chargeDailyCost() {
        val state = repo.ensurePlayerState()
        val pets = repo.petDao.getAllPetsOnce()
        if (pets.isEmpty()) return

        val totalCost = pets.sumOf { it.dailyCost }
        val dayMs = 24 * 60 * 60 * 1000L

        if (System.currentTimeMillis() - state.lastChargeTime < dayMs) return

        if (state.coins >= totalCost) {
            state.coins -= totalCost
            _message.postValue("Содержание питомцев: -$totalCost 💰")
        } else {
            pets.forEach { pet ->
                pet.hunger = (pet.hunger - 20).coerceAtLeast(0)
                repo.petDao.updatePet(pet)
            }
            _message.postValue("Не хватает монет! Питомцы голодают 😢")
        }

        state.lastChargeTime = System.currentTimeMillis()
        repo.savePlayerState(state)
    }

    // ─────────────────────────────────────────────
    //  СЛУЧАЙНЫЕ СОБЫТИЯ
    // ─────────────────────────────────────────────

    private suspend fun checkRandomEvent() {
        val pets = repo.petDao.getAllPetsOnce()
        if (pets.isEmpty()) return
        if ((1..100).random() > 20) return

        val events = listOf(
            RandomEvent("🐾 Питомец нашёл монету на прогулке!", coins = 20),
            RandomEvent("🎁 Друг приюта подарил корм!", food = 1),
            RandomEvent("📰 В газете написали о приюте — +50 монет!", coins = 50),
            RandomEvent("🌟 Волонтёр помог с уборкой — +15 монет!", coins = 15),
            RandomEvent("😴 Кто-то приболел... -10 здоровья", health = -10)
        )

        val event = events.random()

        if (event.coins > 0) {
            val state = repo.ensurePlayerState()
            state.coins += event.coins
            repo.savePlayerState(state)
        }
        if (event.food > 0) {
            repeat(event.food) {
                repo.inventoryDao.add(InventoryItem.FOOD_BASIC, 1)
            }
        }
        if (event.health < 0) {
            val randomPet = pets.random()
            randomPet.health = (randomPet.health + event.health).coerceAtLeast(0)
            if (randomPet.health < 30) randomPet.isSick = true
            repo.petDao.updatePet(randomPet)
        }

        _message.postValue(event.text)
    }

    // ─────────────────────────────────────────────
    //  ПОДБОР ПИТОМЦА
    // ─────────────────────────────────────────────

    fun pickUpRandomPet() = viewModelScope.launch {
        val count = repo.petDao.getPetCount()
        if (count >= MAX_PETS) {
            _message.postValue("Приют переполнен (макс. $MAX_PETS). Пристрой кого-нибудь!")
            return@launch
        }

        val types = listOf("dog", "rabbit", "goat")
        val type = types.random()

        val names = mapOf(
            "dog"    to listOf("Бобик", "Рекс", "Лайма", "Джек", "Белка", "Тузик"),
            "rabbit" to listOf("Пуша", "Снежок", "Клёпа", "Барсик", "Степашка"),
            "goat"   to listOf("Муся", "Зорька", "Белла", "Мила", "Ночка")
        )

        val existingNames = repo.petDao.getAllPetsOnce().map { it.name }.toSet()
        val availableNames = names[type]!!.filter { it !in existingNames }

        val name = if (availableNames.isNotEmpty()) {
            availableNames.random()
        } else {
            "${names[type]!!.random()} ${existingNames.size + 1}"
        }

        val cost = when (type) {
            "dog"    -> 15
            "rabbit" -> 7
            "goat"   -> 10
            else     -> 8
        }

        val pet = Pet(
            name = name,
            type = type,
            hunger = (30..70).random(),
            health = (60..100).random(),
            mood = (40..80).random(),
            dailyCost = cost
        )
        repo.petDao.insertPet(pet)
        _message.postValue("🎉 Ты подобрал(а) $name!")
    }

    fun clearMessage() { _message.value = null }

    // ─────────────────────────────────────────────
    //  ВСПОМОГАТЕЛЬНОЕ
    // ─────────────────────────────────────────────

    private data class RandomEvent(
        val text: String,
        val coins: Int = 0,
        val food: Int = 0,
        val health: Int = 0
    )

    companion object {
        const val MAX_PETS = 10
    }
}
