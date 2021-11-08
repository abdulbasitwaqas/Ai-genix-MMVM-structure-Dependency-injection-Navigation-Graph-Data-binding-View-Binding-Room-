package com.jsbl.genix.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.jsbl.genix.R
import com.jsbl.genix.databinding.FragmentGamificationBinding
import com.jsbl.genix.model.games.GameListActiveItem
import com.jsbl.genix.model.games.GameListInActiveItem
import com.jsbl.genix.model.games.GamesResponseModel
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.APP_TAG
import com.jsbl.genix.utils.RequestHandler
import com.jsbl.genix.utils.callBacks.OnViewClickListener
import com.jsbl.genix.viewModel.GamificationViewModel
import java.util.ArrayList
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.jsbl.genix.BuildConfig
import com.jsbl.genix.model.games.GameListAvailableItem
import com.jsbl.genix.url.APIsURL
import com.jsbl.genix.utils.logD

class GamificationFragment : OnViewClickListener, BaseFragment<GamificationViewModel, FragmentGamificationBinding>(
        GamificationViewModel::class.java
) {
    private lateinit var collectionAdapter: GamificationCollectionAdapter
    private lateinit var allGamesResponseItem: GamesResponseModel
    private lateinit var availableGamesResponseItem: ArrayList<GameListAvailableItem?>
    private lateinit var gameListInActiveItem: ArrayList<GameListInActiveItem?>
    private lateinit var gameListActiveItem: ArrayList<GameListActiveItem?>

    private lateinit var availableFragment: AvailableFragment
    private lateinit var startedFragment: StartedFragment
    private lateinit var completedFragment: CompletedFragment


    override fun onClick(view: View, obj: Any) {
        when (view.id) {

            R.id.drawerImage -> {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onStart() {
        super.onStart()

    }

    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner,
                Observer<CustomerX> { it ->
                    it?.let {
                        showPDialog()
                        viewModel.getUserGames(it.iD.toString())

                    }
                })
    }

    override fun onLoading(obj: RequestHandler) {
        Log.d(APP_TAG, "onLoading: ")
    }

    override fun onSuccess(obj: RequestHandler) {
        Log.d(APP_TAG, "onSuccess: ")
                    dismissDialog()
        if (obj.any is GamesResponseModel) {
            allGamesResponseItem = obj.any as GamesResponseModel
            binding.tvHeading.text = allGamesResponseItem.standup?.category
            binding.tvPoints.text = allGamesResponseItem.standup?.winningPoint?.toString()
            Glide.with(requireActivity())
                    .load(BuildConfig.GAMES_ICONS + allGamesResponseItem.standup?.filePath)
                    .placeholder(R.drawable.ic_platinum)
                    .into(binding.ivTrophy)

            availableFragment = AvailableFragment()

            startedFragment = StartedFragment()

            completedFragment = CompletedFragment()
//
            val arrayList = ArrayList<Fragment>()
            arrayList.add(availableFragment)
            arrayList.add(startedFragment)
            arrayList.add(completedFragment)



            gameListInActiveItem = allGamesResponseItem.gameListInActive!!
            val bundle3 = Bundle()
            bundle3.putParcelableArrayList("gameListInActiveItem", gameListInActiveItem)
            completedFragment.arguments = bundle3

            availableGamesResponseItem = allGamesResponseItem.gameListAvailable!!
            val bundle1 = Bundle()
            bundle1.putParcelableArrayList("availableGamesResponseItem", availableGamesResponseItem)
            availableFragment.arguments = bundle1

            gameListActiveItem = allGamesResponseItem.gameListActive!!
            val bundle2 = Bundle()
            bundle2.putParcelableArrayList("gameListActiveItem", gameListActiveItem)
            startedFragment.arguments = bundle2



            logD("**completedTripsList","Completed Trips list:    ${gameListInActiveItem.size}")

            collectionAdapter = GamificationCollectionAdapter(this, arrayList)

            binding.pager.adapter = collectionAdapter
            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                when (position) {
                    0 -> tab.text = "Available"
                    1 -> tab.text = "Started"
                    2 -> tab.text = "Completed"
                }

            }.attach()

        }
    }

    override fun onError(obj: RequestHandler) {
        Log.d(APP_TAG, "onError: ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchFromDatabase()
        observeDetails()

        binding.onClickListener = this

//        binding.actionBarCustom.accountTitle.setText(resources.getString(R.string.alt_games))
        binding.actionBarCustom.pBar.visibility = View.INVISIBLE
        binding.actionBarCustom.accountTitle.visibility = View.INVISIBLE


    }

    inner class GamificationCollectionAdapter(fragment: Fragment, val arrayList: ArrayList<Fragment>) : FragmentStateAdapter(fragment) {


        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
//            when (position) {
//                0 -> return availableFragment
//                1 -> return startedFragment
//                2 -> return completedFragment
//                else -> return availableFragment
//            }
            return arrayList.get(position)
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_gamification
    }

}