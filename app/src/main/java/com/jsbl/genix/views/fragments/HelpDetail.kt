package com.jsbl.genix.views.fragments

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivityHelpDetailBinding
import com.jsbl.genix.databinding.AltFragmentMyTripDetailsBinding
import com.jsbl.genix.databinding.AltFragmentMyTripsBinding
import com.jsbl.genix.model.help.HelpItem
import com.jsbl.genix.model.help.HelpResponseModelItem
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.RequestHandler
import com.jsbl.genix.utils.callBacks.MapCallbacks
import com.jsbl.genix.utils.callBacks.OnViewClickListener
import com.jsbl.genix.utils.getProfilePercent
import com.jsbl.genix.viewModel.MainHomeViewModel
import com.jsbl.genix.views.fragments.MapFragment.Companion.FLAG_DROP_OFF
import com.jsbl.genix.views.fragments.MapFragment.Companion.FLAG_PICKUP

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class HelpDetail : BaseFragment<MainHomeViewModel, ActivityHelpDetailBinding>(
    MainHomeViewModel::class.java
) {

    private var customerX = CustomerX()
    private lateinit var helpItem: HelpResponseModelItem

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
           helpItem= HelpDetailArgs.fromBundle(it).helpItem
        }
        observeDetails()
        /*Navigation.findNavController(view)
            .navigate(R.id.mapFragment)*/
//        setRequestHandler()
        //TODO
        binding.onClickListener = this
        binding.helpItem = helpItem
        viewModel.fetchFromDatabase()
    }

    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner,
            Observer<CustomerX> { t ->
                t?.let {
                    customerX = it
                }
            })
    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.back -> {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
    }

    override  fun onSuccess(obj: RequestHandler) {
    }

    override fun onError(obj: RequestHandler) {
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_help_detail
    }

}