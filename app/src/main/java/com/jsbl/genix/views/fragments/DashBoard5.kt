package com.jsbl.genix.views.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jsbl.genix.R
import com.jsbl.genix.databinding.AltFragmentDashboard5Binding
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.*
import com.jsbl.genix.viewModel.MainHomeViewModel
import com.jsbl.genix.views.activities.ActCaptureCamera
import com.jsbl.genix.views.activities.ActivityMain
import com.jsbl.genix.views.activities.ActivityRegistration

/**
 * A simple [Fragment] subclass.
 * Use the [DashBoard5.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashBoard5 : BaseFragment<MainHomeViewModel, AltFragmentDashboard5Binding>(
    MainHomeViewModel::class.java
) {


    private var customerX = CustomerX()

    private lateinit var settingSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeDetails()
//        setRequestHandler()
        binding.onClickListener = this
        //TODO
        viewModel.fetchFromDatabase()
        setSettingFragment()

    }


    fun setSettingFragment() {
        settingSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        settingSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })
        val bottomSheetList = BottomSheetList()
        val ft: FragmentTransaction = childFragmentManager.beginTransaction()
        ft.add(R.id.bottomSheet, bottomSheetList)
        ft.commit()
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
                if (it.name != null) {
                    if (it.name!!.isNotEmpty()) {
                        binding.name.text = it.name

                    }
                }
                if (it.birthPlace != null) {
                    if (it.birthPlace!!.isNotEmpty()) {
                        binding.city.text = it.birthPlace

                    }
                }
        /*        if (it.percentage == null) {
                    setAccountProgress(getProfilePercent(it))

                } else {
                    setAccountProgress(it.percentage!!)
                }*/
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
        return R.layout.alt_fragment_dashboard5
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