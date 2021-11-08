package com.jsbl.genix.views.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jsbl.genix.R
import com.jsbl.genix.databinding.AltFragmentMyTripsBinding
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.trips.TripsResponse
import com.jsbl.genix.utils.*
import com.jsbl.genix.viewModel.MainHomeViewModel
import com.jsbl.genix.views.adapters.TripListDateAdapter
import com.jsbl.genix.views.fragments.DashBoard4.Companion.FILTER_ACCELERATION
import com.jsbl.genix.views.fragments.DashBoard4.Companion.FILTER_BRAKING
import com.jsbl.genix.views.fragments.DashBoard4.Companion.FILTER_CORNERING
import com.jsbl.genix.views.fragments.DashBoard4.Companion.FILTER_SPEEDING
import com.jsbl.genix.views.fragments.DashBoard4.Companion.INTENT_FILTER_TRIP


class MyTrips : BaseFragment<MainHomeViewModel, AltFragmentMyTripsBinding>(
    MainHomeViewModel::class.java
) {
    private var pageIndex: Int = 0
    private var tripsList: java.util.ArrayList<TripsResponse?> = java.util.ArrayList()
    private var customerX = CustomerX()
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false

    //    var adpter = TripItemAdpter(ArrayList())
    private lateinit var adapter: TripListDateAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        showPDialog()
        observeDetails()
//        setRequestHandler()
        //TODO
        binding.onClickListener = this
        viewModel.fetchFromDatabase()

        arguments?.let {
            viewModel.filter = it.getInt(INTENT_FILTER_TRIP, 0)
        }

        setHeaderTitle(viewModel.filter)
//        val recyclerView: RecyclerView = view.findViewById(R.id.tripsRecyclerView)
//        setDummyTrips()


        adapter = TripListDateAdapter(tripsList, requireContext())
        val layoutManager = LinearLayoutManager(context)
        binding.tripsRecyclerView.layoutManager = layoutManager
        binding.tripsRecyclerView.adapter = adapter

        binding.tripsRecyclerView.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
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

                    customerX!!.scopeToken?.let { it1 ->
                        viewModel.getAllTrip(
                            it1,
                            customerX.carDetails!![SharePreferencesHelper.invoke(requireContext())
                                .getDefaultCarPos()].registrationNo!!,
                            customerX!!.iD.toString()!!,
                            pageIndex
                        )
                    }

                    // Hamza
                    binding.actionBarCustom.accountTitle.visibility = View.INVISIBLE
                    binding.actionBarCustom.pBar.visibility = View.GONE

                }
            })
    }

    private fun setHeaderTitle(filter: Int) {
        binding.title = when (filter) {
            FILTER_ACCELERATION -> {
                "Accelerations"
            }
            FILTER_SPEEDING -> {
                "Speeding"

            }
            FILTER_CORNERING -> {
                "Cornering"

            }
            FILTER_BRAKING -> {
                "Brakes"
            }
            else -> {
                "Trips"
            }
        }
    }


    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.drawerImage -> {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
        isLoading = true
    }

    override fun onSuccess(obj: RequestHandler) {
        isLoading = false
        dismissDialog()
        val responseList = obj.any as java.util.ArrayList<TripsResponse>

        if (tripsList.size>0){
            tripsList.remove(null)
        }
        if (responseList.size>0){
            pageIndex++
            tripsList.addAll(responseList)
            tripsList.add(null)
        }else{
            isLastPage = true
        }

        adapter.notifyDataSetChanged()


    }

    override fun onError(obj: RequestHandler) {
//        binding.progress.gone()
        dismissDialog()
    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_my_trips
    }

}