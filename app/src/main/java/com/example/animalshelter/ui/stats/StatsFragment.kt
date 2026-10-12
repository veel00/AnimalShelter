package com.example.animalshelter.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animalshelter.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {

    private var _b: FragmentStatsBinding? = null
    private val b get() = _b!!

    private val vm: StatsViewModel by viewModels()
    private lateinit var adoptedAdapter: AdoptedAdapter
    private lateinit var achievementAdapter: AchievementAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _b = FragmentStatsBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //адаптер достижений
        achievementAdapter = AchievementAdapter()
        b.rvAchievements.layoutManager = LinearLayoutManager(requireContext())
        b.rvAchievements.adapter = achievementAdapter

        //адаптер пристроенных
        adoptedAdapter = AdoptedAdapter()
        b.rvAdopted.layoutManager = LinearLayoutManager(requireContext())
        b.rvAdopted.adapter = adoptedAdapter

        //наблюдаем за пристроенными
        vm.adopted.observe(viewLifecycleOwner) { list ->
            adoptedAdapter.submitList(list)
            b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE

            val total = list.size
            val earned = list.sumOf { it.adoptionFee }
            b.tvTotalAdopted.text = total.toString()
            b.tvEarnedCoins.text = earned.toString()
        }

        //наблюдаем за достижениями
        vm.achievements.observe(viewLifecycleOwner) { list ->
            achievementAdapter.submitList(list)
        }
    }

    override fun onResume() {
        super.onResume()
        //обновляем при возврате на экран
        vm.loadAchievements()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}