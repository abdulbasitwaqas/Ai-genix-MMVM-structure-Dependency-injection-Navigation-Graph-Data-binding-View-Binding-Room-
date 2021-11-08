package com.jsbl.genix.views.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.databinding.*
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.trips.TripsDetailModel
import com.jsbl.genix.trips.TripsResponse
import com.jsbl.genix.utils.*
import com.jsbl.genix.viewModel.MainHomeViewModel
import com.jsbl.genix.views.adapters.RewardItemAdpter
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [RewardsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RewardsFragment : BaseFragment<MainHomeViewModel, AltFragmentRewardsBinding>(
    MainHomeViewModel::class.java
) {
    val tripsDetailsModelList: java.util.ArrayList<TripsDetailModel?> = java.util.ArrayList()
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private var customerX = CustomerX()

    private var pageIndex: Int = 0
    var adapter = RewardItemAdpter(ArrayList())


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

//        setRequestHandler()
        //TODO
        binding.onClickListener = this
        viewModel.fetchFromDatabase()

        showPDialog()
        observeDetails()


        adapter = RewardItemAdpter(tripsDetailsModelList)
        val layoutManager = LinearLayoutManager(context)
        binding.tripsRecyclerView.layoutManager = layoutManager
        binding.tripsRecyclerView.setAdapter(adapter)

        binding.tripsRecyclerView.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                if (!isLoading){
                    isLoading = true
                    observeDetails()
                }
            }
        })
    }

    fun setAccountProgress(value: Int) {
        var percentage = value
        if (percentage > 100) {
            percentage = 100
        } else if (percentage < 0) {
            percentage = 0
        } else if (percentage == 60) {
            percentage = 50
        }
        binding.actionBarCustom.pBar.setProgress(percentage)

    }


    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner,
            Observer<CustomerX> { t ->
                t?.let {
                    customerX = it

                    viewModel.getAllTrips(
                        "" + customerX.scopeToken,
                        "" + customerX.carDetails!![SharePreferencesHelper.invoke(requireContext())
                            .getDefaultCarPos()].policyNumber,
                        "" + customerX!!.iD,
                        pageIndex
                    )


                    binding.actionBarCustom.accountTitle.visibility = View.INVISIBLE
                    binding.actionBarCustom.pBar.visibility = View.GONE
//                    if (it.percentage == null) {
//                        setAccountProgress(getProfilePercent(it))
//
//                    } else {
//                        setAccountProgress(it.percentage!!)
//                    }
                }
            })
    }
/*

    fun setRequestHandler() {
        requestObserver = object : androidx.lifecycle.Observer<RequestHandler> {
            override fun onChanged(t: RequestHandler?) {

                if (t != null) {
                    if (t.loading && !t.isSuccess) {
                    } else if (!t.loading && !t.isSuccess) {
                        if (dialogP.isAdded)
                            dialogP.dismiss()
                        logout(t.any!!, requireActivity())

                    } else if (!t.loading && t.isSuccess) {
                        if (dialogP.isAdded)
                            dialogP.dismiss()
                        //TODO add into DB later
                        showShort(requireContext(), "Details Added Successfully")


                    }
                } else {
                }
            }

        }
        viewModel.requestHandlerMLD.observe(viewLifecycleOwner, requestObserver)
    }
*/


/*    fun setDummyTrips() {
        val tripItems = getDummyTrips(requireContext())
        tripItems.add(
            0,
            TripItem(
                trip = "trips",
                startingAddress = getString(R.string.address3),
                endingAddress = getString(R.string.address2),
                startingPoint = 0.0,
                filterLabel = 0.0.toString(),
                endingPoint = 0.0,
                score = 10,
                time = 0
            )

        )
        adpter.updateList(tripItems)
    }*/

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.drawerImage -> {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
        isLoading = true
//        showPDialog()
    }

    override fun onSuccess(obj: RequestHandler) {
        isLoading = false
        dismissDialog()
        if (viewModel.getTrips) {

            val rr = obj.any as TripsResponse

            if (tripsDetailsModelList.size > 0) {
                tripsDetailsModelList.remove(null)
            }
            if (rr.tripsDetailsModelList.size > 0) {
                pageIndex++
                tripsDetailsModelList.addAll(rr.tripsDetailsModelList)
                tripsDetailsModelList.add(null)
            } else {
                isLastPage = true
            }

            adapter.notifyDataSetChanged()
            if (tripsDetailsModelList.size > 0) {
                binding.tripsRecyclerView.visibility = View.VISIBLE
                binding.noItemFound.visibility = View.GONE
            } else {
                binding.tripsRecyclerView.visibility = View.GONE
                binding.noItemFound.visibility = View.VISIBLE
            }
        }
    }

    override fun onError(obj: RequestHandler) {
        dismissDialog()
    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_rewards
    }

}