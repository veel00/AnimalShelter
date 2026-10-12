package com.example.animalshelter.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animalshelter.databinding.FragmentMainBinding
import com.example.animalshelter.util.SoundHelper

class MainFragment : Fragment() {

    private var _b: FragmentMainBinding? = null
    private val b get() = _b!!

    private val vm: MainViewModel by viewModels()
    private lateinit var adapter: PetAdapter
    private lateinit var taskAdapter: DailyTaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _b = FragmentMainBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ─── Список питомцев ───
        adapter = PetAdapter { pet ->
            SoundHelper.playPetSound(requireContext(), pet.type)
            val action = MainFragmentDirections.actionMainToDetail(pet.id)
            findNavController().navigate(action)
        }
        b.rvPets.layoutManager = LinearLayoutManager(requireContext())
        b.rvPets.adapter = adapter

        // ─── Ежедневные задания ───
        taskAdapter = DailyTaskAdapter()
        b.rvDailyTasks.layoutManager = LinearLayoutManager(requireContext())
        b.rvDailyTasks.adapter = taskAdapter

        vm.dailyTasks.observe(viewLifecycleOwner) { tasks ->
            val progress = vm.taskProgress.value ?: emptyList()
            taskAdapter.submit(tasks, progress)
        }
        vm.taskProgress.observe(viewLifecycleOwner) { progress ->
            val tasks = vm.dailyTasks.value ?: emptyList()
            taskAdapter.submit(tasks, progress)
        }

        // ─── Питомцы из БД ───
        vm.pets.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            b.tvPetCount.text = "${list.size}/${MainViewModel.MAX_PETS}"
        }

        // ─── Монеты ───
        vm.coins.observe(viewLifecycleOwner) { coins ->
            b.tvCoins.text = "💰 $coins"
        }

        // ─── Сообщения ───
        vm.message.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                vm.clearMessage()
            }
        }

        // ─── Кнопки ───
        b.fabAdd.setOnClickListener { vm.pickUpRandomPet() }

        b.btnShop.setOnClickListener {
            val action = MainFragmentDirections.actionMainToShop()
            findNavController().navigate(action)
        }
        b.btnStats.setOnClickListener {
            val action = MainFragmentDirections.actionMainToStats()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        vm.refreshCoins()
        vm.refreshDailyTasks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}