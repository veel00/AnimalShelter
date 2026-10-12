package com.example.animalshelter.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.animalshelter.data.AdoptedPet
import com.example.animalshelter.data.InventoryItem
import com.example.animalshelter.data.Pet
import com.example.animalshelter.data.Repository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PetDetailViewModel(app: Application, private val petId: Int) : AndroidViewModel(app) {

    private val repo = Repository(app)

    private val _pet = MutableLiveData<Pet?>()
    val pet: LiveData<Pet?> = _pet

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _finished = MutableLiveData<Boolean>()
    val finished: LiveData<Boolean> = _finished

    private val _coins = MutableLiveData<Int>()
    val coins: LiveData<Int> = _coins

    init {
        refresh()
        loadCoins()
    }

    fun refresh() = viewModelScope.launch {
        _pet.value = repo.petDao.getPetById(petId)
    }

    private fun loadCoins() = viewModelScope.launch {
        val state = repo.ensurePlayerState()
        _coins.value = state.coins
    }

    fun feed() = viewModelScope.launch {
        val p = _pet.value ?: return@launch
        if (p.hunger >= 100) {
            _message.postValue("Питомец не голоден 😊")
            return@launch
        }

        val premium = repo.inventoryDao.getQuantity(InventoryItem.FOOD_PREMIUM) ?: 0
        val basic = repo.inventoryDao.getQuantity(InventoryItem.FOOD_BASIC) ?: 0

        val msg = when {
            premium > 0 -> {
                repo.inventoryDao.consumeOne(InventoryItem.FOOD_PREMIUM)
                p.hunger = (p.hunger + 50).coerceAtMost(100)
                "Премиум корм! +50 голода"
            }
            basic > 0 -> {
                repo.inventoryDao.consumeOne(InventoryItem.FOOD_BASIC)
                p.hunger = (p.hunger + 20).coerceAtMost(100)
                "Обычный корм. +20 голода"
            }
            else -> {
                _message.postValue("Нет еды! Купи в магазине 🛒")
                return@launch
            }
        }

        repo.petDao.updatePet(p)
        _pet.postValue(p)
        _message.postValue(msg)
    }

    fun heal() = viewModelScope.launch {
        val p = _pet.value ?: return@launch
        val meds = repo.inventoryDao.getQuantity(InventoryItem.MEDICINE) ?: 0

        if (meds <= 0) {
            _message.postValue("Нет лекарств! Купи в магазине 💊")
            return@launch
        }
        if (!p.isSick && p.health >= 100) {
            _message.postValue("Питомец здоров ❤️")
            return@launch
        }

        repo.inventoryDao.consumeOne(InventoryItem.MEDICINE)
        p.isSick = false
        p.health = (p.health + 40).coerceAtMost(100)
        repo.petDao.updatePet(p)
        _pet.postValue(p)
        _message.postValue("Питомец вылечен! ❤️")
    }

    fun play() = viewModelScope.launch {
        val p = _pet.value ?: return@launch
        if (p.mood >= 100) {
            _message.postValue("Питомец и так счастлив 😊")
            return@launch
        }

        val toys = repo.inventoryDao.getQuantity(InventoryItem.TOY) ?: 0
        val msg = if (toys > 0) {
            repo.inventoryDao.consumeOne(InventoryItem.TOY)
            p.mood = (p.mood + 30).coerceAtMost(100)
            "+30 настроения 🎾"
        } else {
            p.mood = (p.mood + 10).coerceAtMost(100)
            "Погладил(а)! +10 настроения 🖐"
        }

        repo.petDao.updatePet(p)
        _pet.postValue(p)
        _message.postValue(msg)
    }

    fun adopt(familyName: String, phone: String) = viewModelScope.launch {
        val p = _pet.value ?: return@launch

        val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
        repo.adoptedDao.insert(
            AdoptedPet(
                name = p.name,
                date = date,
                familyName = familyName,
                familyPhone = phone,
                wasSick = p.isSick,
                adoptionFee = 200,
                emoji = p.emoji
            )
        )
        repo.petDao.deletePet(p)

        val state = repo.ensurePlayerState()
        state.coins += 200
        state.totalAdopted += 1
        repo.savePlayerState(state)

        _message.postValue("${p.name} нашёл новый дом! +200 💰")
        _finished.postValue(true)
    }

    fun rename(newName: String) = viewModelScope.launch {
        val p = _pet.value ?: return@launch
        val trimmed = newName.trim()
        if (trimmed.isEmpty()) {
            _message.postValue("Имя не может быть пустым")
            return@launch
        }
        if (trimmed.length > 20) {
            _message.postValue("Имя слишком длинное (макс. 20)")
            return@launch
        }

        p.name = trimmed
        repo.petDao.updatePet(p)
        _pet.postValue(p)
        _message.postValue("Имя изменено на «$trimmed» ✏️")
    }

    fun clearMessage() { _message.value = null }
}