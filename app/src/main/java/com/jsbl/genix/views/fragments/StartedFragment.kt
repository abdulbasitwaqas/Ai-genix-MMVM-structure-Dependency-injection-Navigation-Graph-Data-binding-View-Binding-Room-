package com.jsbl.genix.views.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jsbl.genix.R
import com.jsbl.genix.databinding.FragmentStartedBinding
import com.jsbl.genix.model.games.FaltuInterface
import com.jsbl.genix.model.games.GameListActiveItem
import com.jsbl.genix.model.games.GameListAvailableItem
import com.jsbl.genix.utils.RequestHandler
import com.jsbl.genix.viewModel.GamificationViewModel
import com.jsbl.genix.views.adapters.AvailableAdapter
import com.jsbl.genix.views.adapters.GridAvailableAdapter
import java.util.*

class StartedFragment : BaseFragment<GamificationViewModel, FragmentStartedBinding>(
        GamificationViewModel::class.java
) {

    private lateinit var startedGamesList: ArrayList<GameListActiveItem>

    var status = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.onClickListener = this
        binding.listItemLabel.isSelected = true

        arguments?.let {

            startedGamesList = it.getParcelableArrayList<GameListActiveItem>("gameListActiveItem")!!
            updatelist()
        }
    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.list_item_label -> {
                binding.listItemLabel.isSelected = true
                binding.gridItemLabel.isSelected = false
                    listAdapter(startedGamesList)
            }
            R.id.grid_item_label -> {
                binding.gridItemLabel.isSelected = true
                binding.listItemLabel.isSelected = false
                    gridAdapter(startedGamesList)
            }
        }
    }

    fun updatelist() {
            if (binding.listItemLabel.isSelected) {
                // list adapter
                listAdapter(startedGamesList)
            } else {
                // grid adapter
                gridAdapter(startedGamesList)
            }

            if (startedGamesList.size > 0) {
                binding.gamingRV.visibility = View.VISIBLE
                binding.noGamesTV.visibility = View.GONE
            } else {
                binding.noGamesTV.visibility = View.VISIBLE
                binding.gamingRV.visibility = View.GONE
            }
    }

    override fun onLoading(obj: RequestHandler) {

    }

    override fun onSuccess(obj: RequestHandler) {
    }

    private fun listAdapter(getAllGamesItemsList: List<FaltuInterface>) {
        var availableAdapter = AvailableAdapter(getAllGamesItemsList, requireContext())
        binding.gamingRV.layoutManager = LinearLayoutManager(context)
        binding.gamingRV.adapter = availableAdapter
    }

    private fun gridAdapter(getAllGamesItemsList: List<FaltuInterface>) {
        var gridAvailableAdapter = GridAvailableAdapter(getAllGamesItemsList, requireContext())
        binding.gamingRV.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.gamingRV.adapter = gridAvailableAdapter
    }

    override fun onError(obj: RequestHandler) {
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_started// bahi ye b krna hota hai to do kia hua tha
    }

}