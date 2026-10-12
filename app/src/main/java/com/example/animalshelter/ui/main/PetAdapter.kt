package com.example.animalshelter.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.animalshelter.data.Pet
import com.example.animalshelter.databinding.ItemPetBinding

class PetAdapter(
    private val onClick: (Pet) -> Unit
) : ListAdapter<Pet, PetAdapter.VH>(DIFF) {

    inner class VH(val b: ItemPetBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemPetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val pet = getItem(position)
        val b = holder.b

        b.tvEmoji.text = pet.emoji
        b.tvName.text = pet.name
        b.tvStats.text = "🍖 ${pet.hunger}   ❤️ ${pet.health}   😊 ${pet.mood}"
        b.tvCost.text = "${pet.dailyCost}💰/день"

        //статус
        when {
            pet.isSick -> {
                b.tvStatus.text = "🤒 Болен"
                b.tvStatus.visibility = View.VISIBLE
            }
            pet.isHungry -> {
                b.tvStatus.text = "🍽 Голоден"
                b.tvStatus.visibility = View.VISIBLE
            }
            pet.isSad -> {
                b.tvStatus.text = "😢 Грустит"
                b.tvStatus.visibility = View.VISIBLE
            }
            else -> b.tvStatus.visibility = View.GONE
        }

        //цвет карточки
        b.root.setCardBackgroundColor(
            when {
                pet.isHungry -> 0xFFFFCDD2.toInt()   // красноватый
                pet.isSad    -> 0xFFFFF9C4.toInt()   // желтоватый
                else         -> 0xFFFFFFFF.toInt()   // белый
            }
        )

        b.root.setOnClickListener { onClick(pet) }
        b.root.alpha = 0f
        b.root.animate()
            .alpha(1f)
            .setDuration(300)
            .setStartDelay((position * 40).toLong())
            .start()
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Pet>() {
            override fun areItemsTheSame(o: Pet, n: Pet) = o.id == n.id
            override fun areContentsTheSame(o: Pet, n: Pet) = o == n
        }
    }
}