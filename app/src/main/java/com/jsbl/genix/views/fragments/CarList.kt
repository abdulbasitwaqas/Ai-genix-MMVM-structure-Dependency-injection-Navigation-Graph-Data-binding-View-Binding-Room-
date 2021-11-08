package com.jsbl.genix.views.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.jsbl.genix.R
import com.jsbl.genix.databinding.*
import com.jsbl.genix.model.profileManagement.*
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.registration.LoginMdl
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.callBacks.OnItemClickListener
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.viewModel.CarListViewModel
import com.jsbl.genix.views.activities.ActCarDetails
import com.jsbl.genix.views.adapters.CarsItemAdapter
import com.jsbl.genix.views.adapters.MotorTypeAdapter
import kotlinx.android.synthetic.main.alt_fragment_car_list.*
import java.util.*


class CarList : BaseFragment<CarListViewModel, AltFragmentCarListBinding>(
    CarListViewModel::class.java
), OnItemClickListener {

    private var customerX = CustomerX()


    private lateinit var adpter: CarsItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        binding.carsList.layoutManager = LinearLayoutManager(context)
        adpter = CarsItemAdapter(ArrayList(), this, requireContext())
        binding.carsList.adapter = adpter
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
                    if (!it.carDetails.isNullOrEmpty()) {
                        adpter.updateList(it.carDetails!!)
                        logD("*carLit", "" + it.carDetails)

                    }
                    customerX = it
                    logD("**percentage", "car list    ${it.percentage}")
                    setAccountProgress(getProfilePercent(it))
                }
            })
    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.drawerImage -> {
                requireActivity().onBackPressed()
            }
            R.id.btnNewCar -> {
//        navController.setGraph(R.navigation.<you_nav_graph_xml>, bundle)
                Navigation.findNavController(view)
                    .navigate(
                        CarListDirections.actionCarListToCarDetails()
                    )
            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
    }

    override fun onResume() {
        super.onResume()
        observeDetails()
    }

    override fun onSuccess(obj: RequestHandler) {
        if (viewModel.fromDelCar) {
            dismissDialog()
            when (obj.any) {
                is PostCarDetail -> {
                    showOnlyAlertMessage(
                        context = requireContext(),
                        title = "Delete",
                        msg = "Car deleted successfully.",
                        onPositiveClick = { }
                    )
                }
            }
            viewModel.fromDelCar = false
        } else {

        }
    }

    override fun onError(obj: RequestHandler) {
    }

    override fun onItemClick(view: View, pos: Int, obj: Any) {
        when (view.id) {
            R.id.btnViewCarDetail -> {
                var action = CarListDirections.actionCarListToCarDetails()
                val bundle = action.arguments
                bundle.putBoolean(ActCarDetails.INTENT_FROM_CREATE_NEW, false)
                bundle.putInt(ActCarDetails.INTENT_FROM_SELECTED_POSITION, pos)
                bundle.putBoolean("isDefaultCar", customerX.carDetails!!.get(pos).isDefaultCar)
                Navigation.findNavController(view)
                    .navigate(
                        R.id.carDetails, bundle
                    )
            }
            R.id.starting_icon -> {
                if (adpter.getItem(pos).iD != 0L) {
                    if (!adpter.getItem(pos).isDefaultCar) {
                        viewModel.selectingDefault(adpter.getItem(pos).iD!!)
                    } else {
                        showShort(requireContext(), "Default Car Already Selected")
                    }
                } else {
                    showShort(requireContext(), "Detail are not available")
                }
            }
            R.id.btnCarDel -> {
                if (customerX.carDetails!!.size > 1) {
                    if (adpter.getItem(pos).iD != 0L) {
                        if (!adpter.getItem(pos).isDefaultCar) {
                            showPDialog()
                            viewModel.fromDelCar = true
                            logD("**delCarID", "Deleted car id:   " + adpter.getItem(pos).iD)
                            adpter.getItem(pos)!!.scopeToken = viewModel.prefsHelper.getAuth()
//                            val deleteCarRequest : DeleteCarRequest
                            val deleteCarRequest = DeleteCarRequest(adpter.getItem(pos).iD, adpter.getItem(pos).registrationNo!!)
                            viewModel.deleteCar(
                                adpter.getItem(pos)!!,
                                deleteCarRequest
                            )
                        } else {
                            showShort(
                                requireContext(),
                                "" + getString(R.string.change_default_car)// Hamza changes
                            )
                        }
                    }
                } else {
                    showShort(
                        requireContext(),
                        "You must have a car in the list. Please set your it your default car from car detail."
                    )
                }
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_car_list
    }

}