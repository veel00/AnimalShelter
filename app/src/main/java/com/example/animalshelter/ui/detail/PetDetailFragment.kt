package com.example.animalshelter.ui.detail

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.animalshelter.databinding.FragmentPetDetailBinding
import kotlin.random.Random
import com.example.animalshelter.util.SoundHelper

class PetDetailFragment : Fragment() {

    private var _b: FragmentPetDetailBinding? = null
    private val b get() = _b!!

    private val args: PetDetailFragmentArgs by navArgs()

    private val vm: PetDetailViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PetDetailViewModel(requireActivity().application, args.petId) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _b = FragmentPetDetailBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.pet.observe(viewLifecycleOwner) { pet ->
            pet ?: return@observe
            b.tvEmoji.text = pet.emoji
            b.tvName.text = pet.name
            b.tvType.text = "${pet.emoji} ${pet.typeLabel}"
            b.tvHunger.text = "${pet.hunger}/100"
            b.tvHealth.text = "${pet.health}/100"
            b.tvMood.text = "${pet.mood}/100"
            b.pbHunger.progress = pet.hunger
            b.pbHealth.progress = pet.health
            b.pbMood.progress = pet.mood
        }

        vm.coins.observe(viewLifecycleOwner) { coins ->
            b.tvCoins.text = "💰 $coins"
        }

        vm.message.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                vm.clearMessage()
            }
        }

        vm.finished.observe(viewLifecycleOwner) { done ->
            if (done) findNavController().popBackStack()
        }

        b.btnFeed.setOnClickListener {
            vm.feed()
            spawnHearts(2)
            com.example.animalshelter.util.DailyTasksManager.incrementProgress(
                requireContext(), "feed_3"
            )
        }
        b.btnHeal.setOnClickListener {
            vm.heal()
            com.example.animalshelter.util.DailyTasksManager.incrementProgress(
                requireContext(), "heal_1"
            )
        }
        b.btnPlay.setOnClickListener {
            vm.play()
            spawnHearts(3)
            com.example.animalshelter.util.DailyTasksManager.incrementProgress(
                requireContext(), "play_2"
            )
        }
        b.tvEmoji.setOnClickListener {
            spawnHearts(1)
            vm.pet.value?.let { pet ->
                SoundHelper.playPetSound(requireContext(), pet.type)
            }
        }
        b.btnAdopt.setOnClickListener { showAdoptDialog() }

        b.tvName.setOnClickListener { showRenameDialog() }
    }

    private fun showAdoptDialog() {
        val nameInput = EditText(requireContext()).apply { hint = "Имя семьи" }
        val phoneInput = EditText(requireContext()).apply { hint = "Телефон" }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 30, 50, 30)
            addView(nameInput)
            addView(phoneInput)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Пристроить в семью")
            .setView(container)
            .setPositiveButton("Пристроить") { _, _ ->
                val name = nameInput.text.toString().ifBlank { "Семья" }
                val phone = phoneInput.text.toString().ifBlank { "+7 ..." }
                vm.adopt(name, phone)
                com.example.animalshelter.util.DailyTasksManager.incrementProgress(
                    requireContext(), "adopt_1"
                )
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showRenameDialog() {
        val currentName = vm.pet.value?.name ?: "Питомец"

        val input = EditText(requireContext()).apply {
            setText(currentName)
            hint = "Новое имя"
            setSelection(currentName.length)
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 30, 50, 10)
            addView(input)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("✏️ Переименовать питомца")
            .setView(container)
            .setPositiveButton("Сохранить") { _, _ ->
                vm.rename(input.text.toString())
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun spawnHearts(count: Int) {
        val container = b.heartsContainer
        repeat(count) {
            val tv = TextView(requireContext()).apply {
                text = "❤️"
                textSize = 24f + Random.nextInt(20)
            }
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            params.leftMargin = Random.nextInt((container.width - 200).coerceAtLeast(100)) + 100
            params.topMargin = container.height - 200
            tv.layoutParams = params
            container.addView(tv)

            ObjectAnimator.ofFloat(tv, "translationY", 0f, -500f).apply {
                duration = 1500
                start()
            }
            ObjectAnimator.ofFloat(tv, "alpha", 1f, 0f).apply {
                duration = 1500
                start()
            }
            container.postDelayed({ container.removeView(tv) }, 1600)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}