package com.jsbl.genix.views.fragments

import android.annotation.SuppressLint
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayout
import com.jsbl.genix.R
import com.jsbl.genix.databinding.*
import com.jsbl.genix.model.redeem.AvailableRedeemsModelItem
import com.jsbl.genix.model.redeem.RedeemCartListItem
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.RequestHandler
import com.jsbl.genix.utils.SharePreferencesHelper
import com.jsbl.genix.utils.extensions.toast
import com.jsbl.genix.utils.logD
import com.jsbl.genix.viewModel.RedeemViewModel
import com.jsbl.genix.views.activities.ActCarDetails
import com.jsbl.genix.views.adapters.CartItemAdapter
import com.jsbl.genix.views.adapters.FragmentAdapter
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [RedeemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RedeemFragment : BaseFragment<RedeemViewModel, AltFragmentRedeemBinding>(
        RedeemViewModel::class.java
), RedeemListFragment.UpdateCountListener {
    private lateinit var redeemCartListItem: ArrayList<RedeemCartListItem>
    private var customerX = CustomerX()

    private lateinit var adapter: FragmentAdapter
    private lateinit var availableFragment: RedeemListFragment
    private lateinit var startedFragment: RedeemListFragment
    private lateinit var completedFragment: RedeemListFragment

    override fun onViewCreated(
            view: View,
            savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        observeDetails()
//        setRequestHandler()
        //TODO
        binding.onClickListener = this
//        if (binding.statsModel != null) {
        binding.statsModel = SharePreferencesHelper.invoke(requireContext()).getStatsModel()
//        }
        viewModel.fetchFromDatabase()
        setupViewPager()
        binding.cart.isEnabled =false
        /* val recyclerView: RecyclerView = view.findViewById(R.id.tripsRecyclerView)
 //        recyclerView.layoutManager = GridLayoutManager(context, 2)
         recyclerView.layoutManager = LinearLayoutManager(context)
         recyclerView.adapter = adpter
         setDummyTrips()*/
    }


    fun observeDetails() {
        showPDialog()
        viewModel.customer.observe(viewLifecycleOwner,
                Observer<CustomerX> { t ->
                    t?.let {
                        customerX = it
                        logD("**customerID", "$customerX!!.iD")
                        viewModel.getCartRedeem(it.iD.toString())

                    }
                })
    }


    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.back -> {
                requireActivity().onBackPressed()
            }
            R.id.cart -> {
                CartItemAdapter.redeemPoints = 0
                var action = RedeemFragmentDirections.actionAddToCart()
                val bundle = action.arguments
                bundle.putParcelableArrayList("redeem_cart_list", redeemCartListItem)
                Navigation.findNavController(view)
                        .navigate(
                                R.id.addToCart, bundle
                        )
            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
        binding.cart.isEnabled =false

    }

    override fun onSuccess(obj: RequestHandler) {
        binding.cart.isEnabled =true
        dismissDialog()
        if (viewModel.isRedeemCartCount) {
            viewModel.isRedeemCartCount = false
            if (obj.any is ArrayList<*>) {
                redeemCartListItem = obj.any as ArrayList<RedeemCartListItem>
                availableFragment.updatelist(redeemCartListItem)
                if (redeemCartListItem.size == 0) {
                    binding.cartCount.visibility = View.GONE
                } else {
                    binding.cartCount.visibility = View.VISIBLE
                    binding.cartCount.text = redeemCartListItem.size.toString()
                }
            }
        }
    }

    override fun onError(obj: RequestHandler) {
        binding.cart.isEnabled =false
        dismissDialog()
    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_redeem
    }

    fun setupViewPager() {
        initFragments()
        setupFragments()
    }

    fun initFragments() {
        availableFragment = RedeemListFragment()
        availableFragment.status = 0

        availableFragment.updateCountListener = this
        startedFragment = RedeemListFragment()
        startedFragment.status = 1
        completedFragment = RedeemListFragment()
        completedFragment.status = 2
        adapter = FragmentAdapter(
                childFragmentManager,
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        adapter.addFragment(availableFragment, getString(R.string.redeem_available_title))
//        adapter.addFragment(startedFragment, getString(R.string.redeem_started_title))
        adapter.addFragment(completedFragment, getString(R.string.my_redeem_title))
    }

    fun setupFragments() {
        binding.homeViewPager.adapter = (adapter)
        binding.homeViewPager.offscreenPageLimit = 2

        binding.homeTabLay.setupWithViewPager(binding.homeViewPager)
        binding.homeTabLay.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @SuppressLint("ResourceType")
            override fun onTabSelected(tab: TabLayout.Tab?) {
//                if (tab?.position == 1){
//                    completedFragment.status
//                    completedFragment.observeDetails()
//                }
            }

            @SuppressLint("ResourceType")
            override fun onTabUnselected(tab: TabLayout.Tab?) {


            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

    }

    override fun onupdateCount(list: ArrayList<RedeemCartListItem>) {
        redeemCartListItem = list

        if (list.size == 0) {
            binding.cartCount.visibility = View.GONE
        } else {
            binding.cartCount.visibility = View.VISIBLE
            binding.cartCount.text = list.size.toString()
        }
    }

}