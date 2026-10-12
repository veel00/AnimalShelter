package com.example.animalshelter.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.animalshelter.databinding.FragmentShopBinding
import com.example.animalshelter.util.DailyTasksManager

class ShopFragment : Fragment() {

    private var _b: FragmentShopBinding? = null
    private val b get() = _b!!

    private val vm: ShopViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _b = FragmentShopBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.coins.observe(viewLifecycleOwner) { b.tvCoins.text = "💰 $it" }
        vm.qtyBasic.observe(viewLifecycleOwner) { b.tvQtyBasic.text = "В наличии: $it" }
        vm.qtyPremium.observe(viewLifecycleOwner) { b.tvQtyPremium.text = "В наличии: $it" }
        vm.qtyMed.observe(viewLifecycleOwner) { b.tvQtyMed.text = "В наличии: $it" }
        vm.qtyToy.observe(viewLifecycleOwner) { b.tvQtyToy.text = "В наличии: $it" }

        vm.message.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                vm.clearMessage()
            }
        }

        b.btnBuyBasic.setOnClickListener {
            animateButton(it)
            vm.buyBasic()
            DailyTasksManager.incrementProgress(requireContext(), "buy_1")
        }
        b.btnBuyPremium.setOnClickListener {
            animateButton(it)
            vm.buyPremium()
            DailyTasksManager.incrementProgress(requireContext(), "buy_1")
        }
        b.btnBuyMed.setOnClickListener {
            animateButton(it)
            vm.buyMed()
            DailyTasksManager.incrementProgress(requireContext(), "buy_1")
        }
        b.btnBuyToy.setOnClickListener {
            animateButton(it)
            vm.buyToy()
            DailyTasksManager.incrementProgress(requireContext(), "buy_1")
        }
    }

    private fun animateButton(view: View) {
        view.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}