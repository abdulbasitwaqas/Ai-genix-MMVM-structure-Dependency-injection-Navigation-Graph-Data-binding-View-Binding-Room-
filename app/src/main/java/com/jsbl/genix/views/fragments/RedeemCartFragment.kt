package com.jsbl.genix.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jsbl.genix.R
import com.jsbl.genix.databinding.*
import com.jsbl.genix.model.RedeemItem
import com.jsbl.genix.model.redeem.RedeemCartListItem
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.RequestHandler
import com.jsbl.genix.utils.SharePreferencesHelper
import com.jsbl.genix.utils.extensions.toast
import com.jsbl.genix.utils.getDummyRedeems
import com.jsbl.genix.viewModel.RedeemViewModel
import com.jsbl.genix.views.adapters.CartItemAdapter
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [RedeemCartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RedeemCartFragment : BaseFragment<RedeemViewModel, AltFragmentAddtocartBinding>(
    RedeemViewModel::class.java
), CartItemAdapter.CartUpdateListener, CartItemAdapter.UpdateIdsListListener {
    private lateinit var availableRedeemsModelItem: java.util.ArrayList<RedeemCartListItem>
    private var customerX = CustomerX()
    var idsList  = java.util.ArrayList<String>()
    var redeemPoints: Int = 0

    private  lateinit var adpter :CartItemAdapter

    var status = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        adpter = CartItemAdapter(activity as Context,ArrayList(),this,this)
        observeDetails()
//        setRequestHandler()
        //TODO

        binding.statsModel = SharePreferencesHelper.invoke(requireContext()).getStatsModel()
        binding.onClickListener = this
        viewModel.fetchFromDatabase()
//        val recyclerView: RecyclerView = view.findViewById(R.id.cart_list)
//        recyclerView.layoutManager = GridLayoutManager(context, 2)
        arguments?.let {
            availableRedeemsModelItem = it.getParcelableArrayList<RedeemCartListItem>("redeem_cart_list")!!
            availableRedeemsModelItem?.let {
                binding.cartList.layoutManager = LinearLayoutManager(context)
                binding.cartList.adapter = adpter
                adpter.updateList(availableRedeemsModelItem)
                adpter.notifyDataSetChanged()
            }
        }

//        setDummyTrips()
    }


    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner,
            Observer<CustomerX> { t ->
                t?.let {
                    customerX = it

                }
            })
    }

    fun updateList(){

    }

    fun setDummyTrips() {
        val tripItems = getDummyRedeems(requireContext())
        setDummyDetails(tripItems)
//        adpter.updateList(filterStatus(tripItems))
    }

    fun setDummyDetails(redeemItemList: ArrayList<RedeemItem>) {
        //Type 0 Lucky Draw , 1 Coffee
        //Status 0 available, 1 started, 2 completed
        for (item in redeemItemList) {
            if (item.type == 0) {
                item.title = "1 Lac Luck Draw"
                item.icon = R.drawable.redeem_lucky_draw
                item.subtitle = ""
            } else if (item.type == 1) {
                item.title = "Coffee Planet"
                item.icon = R.drawable.redeem_coffee_planet
                item.subtitle = "Get 1 Coffee"
            }

            if (item.status == 1) {
                item.subtitle = "Started 2 Days ago"

            } else if (item.status == 2) {
                item.subtitle = "Completed 1 week ago"

            }
        }
    }

    fun filterStatus(redeemItemList: ArrayList<RedeemItem>): ArrayList<RedeemItem> {
        var returningList = ArrayList<RedeemItem>()
        for (item in redeemItemList) {
            if (item.status == status) {
                returningList.add(item)
            }
        }
        return returningList
    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.back -> {
                requireActivity().onBackPressed()
            }
            R.id.btnNext -> {

                for (j in 0 until CartItemAdapter.rewardList1!!.size){
                    CartItemAdapter.rewardList1!!.get(j).reedeemCount.also {
                        if (it != null) {
                            redeemPoints = redeemPoints + (it * CartItemAdapter.rewardList1!!.get(j).point!!)
                        }
                    }
                }

                val availablePoints = binding.statsModel!!.totalPoints - binding.statsModel!!.reedeemPoints
                if (CartItemAdapter.cartList.size != 0) {
                    if (redeemPoints<availablePoints) {
                        viewModel.customer.observe(viewLifecycleOwner,
                            Observer<CustomerX> { t ->
                                t?.let {
                                    CartItemAdapter.cartList.forEach{
                                        idsList.addAll(it)
                                    }
                                    customerX = it
                                    showPDialog()
                                    viewModel.checkoutRedeem(
                                        customerX.iD.toString(),
                                        idsList.joinToString { it })
                                }
                            })
                    }else{
                        toast("You dont have enough points to redeem",Toast.LENGTH_LONG)
                    }
                }else{
                    toast("Please add items in cart first",Toast.LENGTH_LONG)
                    requireActivity().onBackPressed()
                }
            }
        }
    }



    override fun onLoading(obj: RequestHandler) {
    }

    override  fun onSuccess(obj: RequestHandler) {

        if (viewModel.isAddCartRedeem){
            CartItemAdapter.cartList.clear()
            CartItemAdapter.redeemPoints = 0
            viewModel.isAddCartRedeem = false
            if (obj.any is java.util.ArrayList<*>) {
                CartItemAdapter.redeemPoints = 0
                 availableRedeemsModelItem = obj.any as java.util.ArrayList<RedeemCartListItem>
                adpter.updateList(availableRedeemsModelItem)
                adpter.notifyDataSetChanged()
            }
        }
        if (viewModel.isCheckoutRedeem){
            viewModel.isCheckoutRedeem = false
            if (obj.isSuccess) {

                binding.statsModel!!.reedeemPoints = binding.statsModel!!.reedeemPoints + redeemPoints
                SharePreferencesHelper.invoke(requireContext()).saveStatsModel(binding.statsModel!!)
//                    binding.redeemedPoints.detailValue = redemedPoints.toString()
//                binding.redeemedPoints.detailValue = (binding.statsModel!!.totalPoints -  redemedPoints).toString()

//                toast("Success", Toast.LENGTH_LONG)

                CartItemAdapter.cartList.clear()
                CartItemAdapter.redeemPoints = 0
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onError(obj: RequestHandler) {
    }

    override fun getLayoutRes(): Int {
        val root= R.layout.alt_fragment_addtocart

    return root
    }

    override fun onCartUpdateClick(redeemCartListItem: RedeemCartListItem, isInsert: Boolean,count: String) {
        var action = "I"
        if (!isInsert){
            action = "D"
        }
        viewModel.customer.observe(viewLifecycleOwner,
            Observer<CustomerX> { t ->
                t?.let {
                    customerX = it
                    showPDialog()
                    viewModel.updateRedeemCart(customerX.iD.toString(),redeemCartListItem.reedeemID.toString(),action,count)

                }
            })
    }

    override fun onListUpdate(list: java.util.ArrayList<java.util.ArrayList<String>>, points:Int) {
        list.forEach{
//            idsList.addAll(it)
        }
//        redeemPoints = points
    }
}