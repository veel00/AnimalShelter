package com.example.animalshelter.ui.stats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.animalshelter.data.Achievement
import com.example.animalshelter.databinding.ItemAchievementBinding

class AchievementAdapter : ListAdapter<Achievement, AchievementAdapter.VH>(DIFF) {

    inner class VH(val b: ItemAchievementBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        val b = holder.b

        b.tvEmoji.text = if (item.unlocked) item.emoji else "🔒"
        b.tvTitle.text = item.title
        b.tvDescription.text = item.description
        b.tvProgress.text = item.progressText

        // Цвета в зависимости от статуса
        if (item.unlocked) {
            b.tvProgress.setTextColor(0xFF43A047.toInt())
            b.tvProgress.setBackgroundColor(0xFFE8F5E9.toInt())
            b.tvTitle.alpha = 1f
            b.tvDescription.alpha = 1f
            b.root.setCardBackgroundColor(0xFFFFFFFF.toInt())
        } else {
            b.tvProgress.setTextColor(0xFF9E9E9E.toInt())
            b.tvProgress.setBackgroundColor(0xFFF5F5F5.toInt())
            b.tvTitle.alpha = 0.6f
            b.tvDescription.alpha = 0.6f
            b.root.setCardBackgroundColor(0xFFF5F5F5.toInt())
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Achievement>() {
            override fun areItemsTheSame(o: Achievement, n: Achievement) = o.id == n.id
            override fun areContentsTheSame(o: Achievement, n: Achievement) = o == n
        }
    }
}