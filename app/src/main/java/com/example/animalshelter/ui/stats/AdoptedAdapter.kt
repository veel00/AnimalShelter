package com.example.animalshelter.ui.stats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.animalshelter.data.AdoptedPet
import com.example.animalshelter.databinding.ItemAdoptedBinding

class AdoptedAdapter : ListAdapter<AdoptedPet, AdoptedAdapter.VH>(DIFF) {

    inner class VH(val b: ItemAdoptedBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemAdoptedBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        val b = holder.b

        b.tvEmoji.text = item.emoji
        b.tvName.text = item.name
        b.tvFamily.text = "👨‍👩‍👧 ${item.familyName}"
        b.tvDate.text = "📅 ${item.date}"
        b.tvFee.text = "+${item.adoptionFee}💰"
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<AdoptedPet>() {
            override fun areItemsTheSame(o: AdoptedPet, n: AdoptedPet) = o.id == n.id
            override fun areContentsTheSame(o: AdoptedPet, n: AdoptedPet) = o == n
        }
    }
}