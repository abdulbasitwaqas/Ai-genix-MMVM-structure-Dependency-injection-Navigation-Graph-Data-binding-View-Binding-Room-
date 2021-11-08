package com.jsbl.genix.views.fragments

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.databinding.*
import com.jsbl.genix.model.RedeemItem
import com.jsbl.genix.model.redeem.AvailableRedeemsModelItem
import com.jsbl.genix.model.redeem.MyRedeemResponse
import com.jsbl.genix.model.redeem.RedeemCartListItem
import com.jsbl.genix.model.redeem.ReedemListActiveItem
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.RequestHandler
import com.jsbl.genix.utils.extensions.showRedeemAlert
import com.jsbl.genix.utils.getDummyRedeems
import com.jsbl.genix.viewModel.RedeemViewModel
import com.jsbl.genix.views.adapters.CartItemAdapter
import com.jsbl.genix.views.adapters.MyRedeemItemAdapter
import com.jsbl.genix.views.adapters.RedeemItemAdapter
import kotlinx.android.synthetic.main.dialog_send_sms.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [RedeemListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RedeemListFragment : BaseFragment<RedeemViewModel, AltFragmentRedeemListBinding>(
    RedeemViewModel::class.java
), RedeemItemAdapter.ItemCLickListener,MyRedeemItemAdapter.ItemCLickListener {
    var redeemCartList: java.util.ArrayList<RedeemCartListItem> = ArrayList()
    private lateinit var availableRedeemsModelItem: java.util.ArrayList<AvailableRedeemsModelItem>
    private var customerX = CustomerX()

    var adpter = RedeemItemAdapter(ArrayList<AvailableRedeemsModelItem>(), this)
    var myRedeemAdpter = MyRedeemItemAdapter(ArrayList(),this)

    var status = 0

    lateinit var updateCountListener: UpdateCountListener

    fun updatelist(redeemList: java.util.ArrayList<RedeemCartListItem>) {
        redeemCartList = redeemList
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        observeDetails()
//        setRequestHandler()
        //TODO
        binding.onClickListener = this
        viewModel.fetchFromDatabase()
        binding.redeemList.layoutManager = LinearLayoutManager(context)
        binding.redeemList.adapter = adpter
//        setDummyTrips()
    }


    fun observeDetails() {
        showPDialog()
        viewModel.customer.observe(viewLifecycleOwner,
            Observer<CustomerX> { t ->
                t?.let {
                    customerX = it
                    if (status == 0) {
                        viewModel.getAvailableRedeems()
                    }
                    if (status == 2) {
                        viewModel.getMyRedeems(customerX!!.iD.toString())
                    }

                }
            })
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
            R.id.drawerImage -> {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
    }

    override fun onSuccess(obj: RequestHandler) {
        dismissDialog()
        if (viewModel.isAvailableRedeem) {
            viewModel.isAvailableRedeem = false
            if (obj.any is java.util.ArrayList<*>) {
                val availableRedeemsModelItem =
                    obj.any as java.util.ArrayList<AvailableRedeemsModelItem>
                adpter.updateList(availableRedeemsModelItem)
            }
        }
        if (viewModel.isMyRedeem) {
            viewModel.isMyRedeem = false
            if (obj.any is MyRedeemResponse) {
                val myRedeemResponse = obj.any as MyRedeemResponse
                val list:ArrayList<Any?>? = ArrayList()
                if (myRedeemResponse.reedemListActive!!.size>0) {
                    list!!.add("Started")
                    list!!.addAll(myRedeemResponse.reedemListActive!!)
                }
                if (myRedeemResponse.reedemListInActive!!.size>0) {
                    list!!.add("History")
                    list!!.addAll(myRedeemResponse.reedemListInActive!!)
                }
                binding.redeemList.adapter = myRedeemAdpter
                myRedeemAdpter.updateList(list)
            }
        }
        if (viewModel.isAddCartRedeem) {
            viewModel.isAddCartRedeem = false
            if (obj.any is java.util.ArrayList<*>) {
                val redeemCartList = obj.any as java.util.ArrayList<RedeemCartListItem>
                updateCountListener.onupdateCount(redeemCartList)
            }
        }
    }

    override fun onError(obj: RequestHandler) {
        dismissDialog()
    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_redeem_list
    }

    override fun onItemClick(availableRedeemsModelItem: AvailableRedeemsModelItem) {
        availableRedeemsModelItem.description?.let {

            var redeem: Int = 0
            for (i in 0 until redeemCartList.size) {
                if (availableRedeemsModelItem.iD == redeemCartList.get(i).reedeemID) {
                    redeem = redeem + redeemCartList.get(i).reedeemCount!!
                    break
                }
            }

            var action = RedeemFragmentDirections.actionAddToCart()
            val bundle = action.arguments
            bundle.putParcelable("redeem_item", availableRedeemsModelItem)
            bundle.putParcelableArrayList("redeem_cart_list", redeemCartList)
            bundle.putInt("redeem_count", redeem)
            Navigation.findNavController(requireActivity(),R.id.fragmentNavHos)
                .navigate(
                    R.id.redeemDetailsFragment, bundle
                )
            /*availableRedeemsModelItem.title?.let { it1 ->
                showRedeemAlert(
                    context = requireContext() as Activity,
                    title= it1,
                    message = it,
                    onPositiveClick = {

                        viewModel.customer.observe(viewLifecycleOwner,
                            Observer<CustomerX> { t ->
                                t?.let {

                                    customerX = it
                                    showPDialog()
                                    viewModel.updateRedeemCart(
                                        customerX.iD.toString(),
                                        availableRedeemsModelItem.iD.toString(),
                                        "I",
                                        redeem.toString()
                                    )

                                }
                            })
                    },
                    onNegativeClick = {

                    }
                )
            }*/
        }
    }

    interface UpdateCountListener {
        fun onupdateCount(list: java.util.ArrayList<RedeemCartListItem>)
    }

    override fun onItemClick(myRedeemActive: ReedemListActiveItem) {
        var action = RedeemFragmentDirections.actionRedeemRewardFragment()
        val bundle = action.arguments
        bundle.putString("expiry_date", myRedeemActive.expiryDate)
        bundle.putInt("redeem_id", myRedeemActive.iD!!)
        Navigation.findNavController(requireActivity(),R.id.fragmentNavHos)
            .navigate(R.id.redeemRewardFragment,bundle)
    }
}