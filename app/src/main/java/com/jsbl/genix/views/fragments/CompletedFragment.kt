package com.jsbl.genix.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jsbl.genix.R
import com.jsbl.genix.databinding.FragmentAvailableBinding
import com.jsbl.genix.databinding.FragmentCompletedBinding
import com.jsbl.genix.databinding.FragmentStartedBinding
import com.jsbl.genix.model.GetAllGamesResponseItem
import com.jsbl.genix.model.games.FaltuInterface
import com.jsbl.genix.model.games.GameListActiveItem
import com.jsbl.genix.model.games.GameListAvailableItem
import com.jsbl.genix.model.games.GameListInActiveItem
import com.jsbl.genix.model.redeem.RedeemCartListItem
import com.jsbl.genix.model.registration.LoginMdl
import com.jsbl.genix.utils.APP_TAG
import com.jsbl.genix.utils.RequestHandler
import com.jsbl.genix.utils.logD
import com.jsbl.genix.viewModel.GamificationViewModel
import com.jsbl.genix.views.adapters.AvailableAdapter
import com.jsbl.genix.views.adapters.GridAvailableAdapter
import java.util.*
import kotlin.math.log

class CompletedFragment : BaseFragment<GamificationViewModel, FragmentCompletedBinding>(
        GamificationViewModel::class.java
) {

    private lateinit var completedGamesList: ArrayList<GameListInActiveItem>

    var status = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.onClickListener = this
        binding.listItemLabel.isSelected = true

        arguments?.let {

            completedGamesList = it.getParcelableArrayList<GameListInActiveItem>("gameListInActiveItem")!!
            logD("**completedTrip","completed trips:   ${completedGamesList.size}")
            updatelist()
        }
    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.list_item_label -> {
                binding.listItemLabel.isSelected = true
                binding.gridItemLabel.isSelected = false
                    listAdapter(completedGamesList)
            }
            R.id.grid_item_label -> {
                binding.gridItemLabel.isSelected = true
                binding.listItemLabel.isSelected = false
                    gridAdapter(completedGamesList)
            }
        }
    }

    fun updatelist() {
            if (binding.listItemLabel.isSelected) {
                // list adapter
                listAdapter(completedGamesList)
            } else {
                // grid adapter
                gridAdapter(completedGamesList)
            }

            if (completedGamesList.size > 0) {
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
        return R.layout.fragment_completed// bahi ye b krna hota hai to do kia hua tha
    }

}