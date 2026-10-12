package com.example.animalshelter.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.animalshelter.data.Repository
import com.example.animalshelter.util.NotificationHelper

class PetHungerWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val repo = Repository(applicationContext)
            val pets = repo.petDao.getAllPetsOnce()

            var hungryCount = 0

            pets.forEach { pet ->
                //уменьшаем голод на 10
                pet.hunger = (pet.hunger - 10).coerceAtLeast(0)

                //если голод <20 и настроение <20 два дня — болезнь
                if (pet.hunger < 20 || pet.mood < 20) {
                    pet.daysInShelter++
                    if (pet.daysInShelter >= 2) {
                        pet.isSick = true
                    }
                } else {
                    pet.daysInShelter = 0
                }

                //если голод <30 — считаем голодным
                if (pet.hunger < 30) hungryCount++

                repo.petDao.updatePet(pet)
            }

            //показываем уведомление, если есть голодные
            if (hungryCount > 0) {
                NotificationHelper.showHungryNotification(applicationContext, hungryCount)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "pet_hunger_worker"
        const val INTERVAL_HOURS = 3L
    }
}
