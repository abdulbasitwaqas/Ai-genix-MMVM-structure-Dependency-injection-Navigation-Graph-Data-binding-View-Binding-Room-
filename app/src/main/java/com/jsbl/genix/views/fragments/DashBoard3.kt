package com.jsbl.genix.views.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.databinding.AltFragmentDashboard3Binding
import com.jsbl.genix.trips.TripItem
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.*
import com.jsbl.genix.viewModel.MainHomeViewModel
import com.jsbl.genix.views.activities.ActCaptureCamera
import com.jsbl.genix.views.activities.ActivityMain
import com.jsbl.genix.views.activities.ActivityRegistration
import com.jsbl.genix.views.adapters.TripItemDashboardAdpter
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [DashBoard3.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashBoard3 : BaseFragment<MainHomeViewModel, AltFragmentDashboard3Binding>(
    MainHomeViewModel::class.java
) {


    private var customerX = CustomerX()
    var adpter = TripItemDashboardAdpter(ArrayList())


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeDetails()
//        setRequestHandler()
        binding.onClickListener = this
        viewModel.fetchFromDatabase()
        val recyclerView: RecyclerView = view.findViewById(R.id.tripList)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adpter
//        setDummyTrips()
    }


    fun setDummyTrips() {
        val tripItems = ArrayList<TripItem>()
        tripItems.add(
            TripItem(
                getString(R.string.address3),
                getString(R.string.address2),
                "Distance 10 KM"
            )
        )
        tripItems.add(
            TripItem(
                getString(R.string.address3),
                getString(R.string.address2),
                "Distance 10 KM"
            )
        )
        tripItems.add(
            TripItem(
                getString(R.string.address3),
                getString(R.string.address2),
                "Distance 10 KM"
            )
        )
        tripItems.add(
            TripItem(
                getString(R.string.address3),
                getString(R.string.address2),
                "Distance 10 KM"
            )
        )
        tripItems.add(
            TripItem(
                getString(R.string.address3),
                getString(R.string.address2),
                "Distance 10 KM"
            )
        )
        tripItems.add(
            TripItem(
                getString(R.string.address3),
                getString(R.string.address2),
                "Distance 10 KM"
            )
        )
        adpter.updateList(tripItems)
    }

    fun setAccountProgress(value: Int) {
        var percentage = value
        if (percentage >= 100) {
            percentage = 100
//            binding.completeProfileLabel.gone()
        } else if (percentage <= 0) {
            percentage = 0
        }else if (percentage == 60) {
            percentage = 50
        }
        binding.circularProgressBar.progress = percentage.toFloat()
        binding.score.text = percentage.toString()
        /* val params =
             binding.accountProgressLayout.ivDropdown.getLayoutParams() as ConstraintLayout.LayoutParams
         params.horizontalBias =
             percentage.toFloat() / 100f // here is one modification for example. modify anything else you want :)
         binding.accountProgressLayout.ivDropdown.setLayoutParams(params)
         binding.accountProgressLayout.tvProgressDigits.text = "$percentage%"*/
    }


    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.customer = it
                customerX = it
                if (it.percentage == null) {
                    setAccountProgress(getProfilePercent(it))

                } else {
                    setAccountProgress(it.percentage!!)
                }
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
        }
    }

    override fun onLoading(obj: RequestHandler) {

    }

    override  fun onSuccess(obj: RequestHandler) {
        showShort(requireContext(), "Details Added Successfully")
    }

    override fun onError(obj: RequestHandler) {

    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_dashboard3
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ActivityRegistration.REQUEST_CODE_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                customerX.profileImagePath =
                    data?.getStringExtra(ActivityRegistration.INTENT_IMAGE_URL)!!
                viewModel.storeCustomerLocally(customerX)
                binding.customer = customerX

//                viewModel.setSuccess(customerX)
                // OR
                // String returnedResult = data.getDataString();
            }
        }
    }
}