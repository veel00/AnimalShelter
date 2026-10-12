package com.example.animalshelter.ui.shop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.animalshelter.data.InventoryItem
import com.example.animalshelter.data.Repository
import kotlinx.coroutines.launch

class ShopViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = Repository(app)

    private val _coins = MutableLiveData<Int>()
    val coins: LiveData<Int> = _coins

    private val _qtyBasic = MutableLiveData<Int>()
    val qtyBasic: LiveData<Int> = _qtyBasic

    private val _qtyPremium = MutableLiveData<Int>()
    val qtyPremium: LiveData<Int> = _qtyPremium

    private val _qtyMed = MutableLiveData<Int>()
    val qtyMed: LiveData<Int> = _qtyMed

    private val _qtyToy = MutableLiveData<Int>()
    val qtyToy: LiveData<Int> = _qtyToy

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        _coins.value = repo.ensurePlayerState().coins
        _qtyBasic.value = repo.inventoryDao.getQuantity(InventoryItem.FOOD_BASIC) ?: 0
        _qtyPremium.value = repo.inventoryDao.getQuantity(InventoryItem.FOOD_PREMIUM) ?: 0
        _qtyMed.value = repo.inventoryDao.getQuantity(InventoryItem.MEDICINE) ?: 0
        _qtyToy.value = repo.inventoryDao.getQuantity(InventoryItem.TOY) ?: 0
    }

    fun buyBasic() = buy(InventoryItem.FOOD_BASIC, 10, "Обычный корм")
    fun buyPremium() = buy(InventoryItem.FOOD_PREMIUM, 30, "Премиум корм")
    fun buyMed() = buy(InventoryItem.MEDICINE, 25, "Таблетка")
    fun buyToy() = buy(InventoryItem.TOY, 15, "Игрушка")

    private fun buy(item: String, price: Int, label: String) = viewModelScope.launch {
        val state = repo.ensurePlayerState()
        if (state.coins < price) {
            _message.postValue("Не хватает монет! 💰")
            return@launch
        }

        state.coins -= price
        repo.savePlayerState(state)
        repo.inventoryDao.add(item, 1)

        _message.postValue("$label куплен! ✅")
        refresh()
    }

    fun clearMessage() { _message.value = null }
}