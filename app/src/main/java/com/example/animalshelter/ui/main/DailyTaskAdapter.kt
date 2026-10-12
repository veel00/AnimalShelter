package com.example.animalshelter.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.animalshelter.databinding.ItemDailyTaskBinding
import com.example.animalshelter.util.DailyTasksManager

class DailyTaskAdapter : RecyclerView.Adapter<DailyTaskAdapter.VH>() {

    private var tasks: List<DailyTasksManager.DailyTask> = emptyList()
    private var progress: List<Int> = emptyList()

    inner class VH(val b: ItemDailyTaskBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemDailyTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val task = tasks[position]
        val cur = progress.getOrElse(position) { 0 }
        val done = cur >= task.target

        val b = holder.b
        b.tvTitle.text = task.title
        b.tvProgress.text = if (done) "✅" else "$cur/${task.target}"
        b.pbTask.max = task.target
        b.pbTask.progress = cur.coerceAtMost(task.target)
        b.pbTask.progressTintList = android.content.res.ColorStateList.valueOf(
            if (done) 0xFF43A047.toInt() else 0xFFFF9800.toInt()
        )
        b.tvProgress.setTextColor(
            if (done) 0xFF43A047.toInt() else 0xFFFF9800.toInt()
        )
    }

    override fun getItemCount() = tasks.size

    fun submit(newTasks: List<DailyTasksManager.DailyTask>, newProgress: List<Int>) {
        tasks = newTasks
        progress = newProgress
        notifyDataSetChanged()
    }
}