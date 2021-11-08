package com.jsbl.genix.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.jsbl.genix.R
import com.jsbl.genix.databinding.*
import com.jsbl.genix.model.RedeemItem
import com.jsbl.genix.model.redeem.AvailableRedeemsModelItem
import com.jsbl.genix.model.redeem.RedeemCartListItem
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.RequestHandler
import com.jsbl.genix.utils.SharePreferencesHelper
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.utils.extensions.toast
import com.jsbl.genix.utils.getDummyRedeems
import com.jsbl.genix.viewModel.RedeemViewModel
import com.jsbl.genix.views.adapters.CartItemAdapter
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [RedeemDetailstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RedeemDetailstFragment : BaseFragment<RedeemViewModel, AltFragmentRedeemDetailsBinding>(
    RedeemViewModel::class.java
), CartItemAdapter.CartUpdateListener, CartItemAdapter.UpdateIdsListListener {
    private lateinit var redeemCartList: java.util.ArrayList<RedeemCartListItem>
    private var redeemCount: Int = 0
    private lateinit var availableRedeemsModelItem: AvailableRedeemsModelItem
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

        binding.onClickListener = this
        viewModel.fetchFromDatabase()
//        val recyclerView: RecyclerView = view.findViewById(R.id.cart_list)
//        recyclerView.layoutManager = GridLayoutManager(context, 2)
        arguments?.let {
            availableRedeemsModelItem = it.getParcelable<AvailableRedeemsModelItem>("redeem_item")!!
            redeemCartList = it.getParcelableArrayList<RedeemCartListItem>("redeem_cart_list")!!
            redeemCount = it.getInt("redeem_count",0)
            binding.model = availableRedeemsModelItem
            if (redeemCartList.size == 0){
                binding.cartCount.visibility = View.GONE
            }else{
                binding.cartCount.visibility = View.VISIBLE
                binding.cartCount.text = redeemCartList.size.toString()
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

            R.id.cart -> {
                CartItemAdapter.redeemPoints  = 0
                var action = RedeemFragmentDirections.actionAddToCart()
                val bundle = action.arguments
                bundle.putParcelableArrayList("redeem_cart_list", redeemCartList)
                Navigation.findNavController(view)
                    .navigate(
                        R.id.addToCart, bundle
                    )
            }
            R.id.btnNext -> {
                redeemCount++
                showPDialog()
                viewModel.updateRedeemCart(
                    customerX.iD.toString(),
                    availableRedeemsModelItem.iD.toString(),
                    "I",
                    redeemCount.toString()
                )
            }
        }
    }



    override fun onLoading(obj: RequestHandler) {
    }

    override  fun onSuccess(obj: RequestHandler) {
        if (viewModel.isAddCartRedeem){
          showOnlyAlertMessage(requireContext(),"Great!!!","Item added to cart"){
              requireActivity().onBackPressed()
          }
        }
    }

    override fun onError(obj: RequestHandler) {
    }

    override fun getLayoutRes(): Int {
        val root= R.layout.alt_fragment_redeem_details

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