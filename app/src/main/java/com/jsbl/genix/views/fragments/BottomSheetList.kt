package com.jsbl.genix.views.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.databinding.AltFragmentBottomSheetListBinding
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.trips.TripItemResponse
import com.jsbl.genix.trips.TripsDetailModel
import com.jsbl.genix.trips.TripsResponse
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.extensions.toast
import com.jsbl.genix.viewModel.MainHomeViewModel
import com.jsbl.genix.views.activities.ActCaptureCamera
import com.jsbl.genix.views.activities.ActivityMain
import com.jsbl.genix.views.activities.ActivityRegistration
import com.jsbl.genix.views.adapters.TripsListAdapterHorizontal
import kotlinx.android.synthetic.main.alt_fragment_dashboard3.*
import java.util.*
import kotlin.collections.ArrayList


class BottomSheetList : BaseFragment<MainHomeViewModel, AltFragmentBottomSheetListBinding>(
    MainHomeViewModel::class.java) {

    private lateinit var navController: NavController

    private var customerX = CustomerX()
    private lateinit var tripsListAdapter: TripsListAdapterHorizontal
    private lateinit var recyclerView: RecyclerView
    private var tripDetailModelList : List<TripsDetailModel> = ArrayList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            viewModel.filter = it.getInt(DashBoard4.INTENT_FILTER_TRIP, 0)
        }
        observeDetails()
        binding.onClickListener = this
//
        viewModel.fetchFromDatabase()
        recyclerView = view.findViewById(R.id.tripListRV)

        arguments?.let {
            viewModel.filter = it.getInt(DashBoard4.INTENT_FILTER_TRIP, 0)
        }

        setHeaderTitle(viewModel.filter)


    }


    private fun setHeaderTitle(filter: Int) {
        binding.title = when (filter) {
            DashBoard4.FILTER_ACCELERATION -> {
                "Accelerations"
            }
            DashBoard4.FILTER_SPEEDING -> {
                "Speeding"

            }
            DashBoard4.FILTER_CORNERING -> {
                "Cornering"

            }
            DashBoard4.FILTER_BRAKING -> {
                "Brakes"
            }
            else -> {
                "Trips"
            }
        }
    }

    fun setAccountProgress(value: Int) {
        var percentage = value
        if (percentage >= 100) {
            percentage = 100
        } else if (percentage <= 0) {
            percentage = 0
        }else if (percentage == 60) {
            percentage = 50
        }
    }


    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.customer = it
                customerX = it
//                showPDialog()
                binding.progress.visibility = View.VISIBLE
                viewModel.getLatestFiveTrips(
                    ""+customerX.scopeToken,
                    "" + customerX.carDetails!![SharePreferencesHelper.invoke(requireContext()).getDefaultCarPos()].policyNumber,
                    ""+customerX!!.iD
                )
                setAccountProgress(getProfilePercent(it))
            }
        })
    }


    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.speedConstraint -> {
            }
            R.id.corneringConstraint -> {
            }
            R.id.brakingConstraint -> {
            }
            R.id.accelerationConstraint -> {
            }
            R.id.drawerIcon -> {
                (requireActivity() as ActivityMain).checkDrawer()
            }
            R.id.ivProfile -> {
                startActivityForResult(
                    Intent(context, ActCaptureCamera::class.java),
                    ActivityRegistration.REQUEST_CODE_PROFILE
                )
//                    viewModel.registerEntry(binding.edMessage.text.toString().trim())
            }
            R.id.see_more -> {
                navController = Navigation.findNavController(requireActivity(), R.id.fragmentNavHos)
                navController.navigate(R.id.myTrips)
            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
//        binding.progress.visible()
    }

    override  fun onSuccess(obj: RequestHandler) {
//        binding.progress.gone()
        binding.progress.visibility = View.GONE
        if (viewModel.getTrips) {
            val rr = obj.any as ArrayList<TripsDetailModel>
//            val tripDL = rr as ArrayList<TripItemResponse>
            viewModel.getTrips = false
            binding.progress.gone()
            tripDetailModelList = rr

            tripsListAdapter = TripsListAdapterHorizontal(rr, requireContext())
            if (rr!!.size>0){
                recyclerView.visibility = VISIBLE
                binding.tripListTV.visibility = GONE
            } else{
                recyclerView.visibility = GONE
                binding.tripListTV.visibility = VISIBLE
//                viewModel.getUserGames(customerX.iD!!)

            }
            viewModel.getUserGames(customerX.iD!!)
            val llm = LinearLayoutManager(requireContext())
            llm.orientation = LinearLayoutManager.HORIZONTAL
            recyclerView.setLayoutManager(llm)
            recyclerView.setAdapter(tripsListAdapter)

        } else {
            binding.progress.gone()
        }

    }

    override fun onError(obj: RequestHandler) {
        binding.progress.gone()
        binding.progress.visibility = View.GONE

    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_bottom_sheet_list
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ActivityRegistration.REQUEST_CODE_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                customerX.profileImagePath =
                    data?.getStringExtra(ActivityRegistration.INTENT_IMAGE_URL)!!
                viewModel.storeCustomerLocally(customerX)
                binding.customer = customerX
            }
        }
    }
}