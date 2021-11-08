package com.jsbl.genix.views.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.jsbl.genix.R
import com.jsbl.genix.databinding.AltFragmentProfileManagementNewBinding
import com.jsbl.genix.model.profileManagement.*
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.extensions.selectorConstraintButton
import com.jsbl.genix.viewModel.ProfileManagementViewModel
import com.scope.portalapiclient.PreferencesHelper

class ProfileManagement :
    BaseFragment<ProfileManagementViewModel, AltFragmentProfileManagementNewBinding>(
        ProfileManagementViewModel::class.java
    ), View.OnTouchListener {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
//        showShort(requireContext(),"Profile Management")

        binding.onClickListener = this



        binding.btnProfileDetails.innerConstraint.setOnTouchListener(this)
        binding.btnCarDetails.innerConstraint.setOnTouchListener(this)
        binding.btnInsurance.innerConstraint.setOnTouchListener(this)
        binding.btnFeedback.innerConstraint.setOnTouchListener(this)
        binding.btnAreaOfInterest.innerConstraint.setOnTouchListener(this)
        observeDetails()
        viewModel.fetchFromDatabase()

//        binding.actionBarCustom.title.setText("Profile Management")
//        binding.actionBarCustom.drawerImage.setOnClickListener { requireActivity().onBackPressed() }
    }




    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner, Observer {
            it?.let {
                setAccountProgress(getProfilePercent(it))
                    logD("*profileCompletionInfo","car:"+it.carDetails?.size)
                    logD("*profileCompletionInfo","feedback:"+it.feedBacks)
                    logD("*profileCompletionInfo","interests:"+it.customerInterests)
                    logD("*profileCompletionInfo","customer insurance:"+it.Isinsured)
                    logD("*profileCompletionInfo","car insurance:"+it.carDetails!![SharePreferencesHelper.invoke(requireContext())
                        .getDefaultCarPos()].insured)

                    if (it.carDetails!!.size<1) {
                        binding.btnCarDetails.innerConstraint.setBackgroundResource(R.drawable.incomplete_btn_red_bg)
                    }else{
                        binding.btnCarDetails.innerConstraint.setBackgroundResource(R.drawable.completed_btn_bg_unselected)
                    }

                    if (it.carDetails!![SharePreferencesHelper.invoke(requireContext())
                            .getDefaultCarPos()].insured!!){
                        binding.btnInsurance.innerConstraint.setBackgroundResource(R.drawable.completed_round_green_btn)
                    } else if (it.Isinsured!!){
                        binding.btnInsurance.innerConstraint.setBackgroundResource(R.drawable.amber_border_round_line)
                    } else{
                        binding.btnInsurance.innerConstraint.setBackgroundResource(R.drawable.incomplete_btn_red_bg)
                    }


                    if (it.customerInterests == null || it.customerInterests!!.size<1) {
                        binding.btnAreaOfInterest.innerConstraint.setBackgroundResource(R.drawable.incomplete_btn_red_bg)
                    }else{
                        binding.btnAreaOfInterest.innerConstraint.setBackgroundResource(R.drawable.completed_round_green_btn)
                    }

            }
        })
    }


    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.btn_profile_details -> {
                Navigation.findNavController(view)
                    .navigate(ProfileManagementDirections.actionProfileManagementToPersonalDetails())
            }

            R.id.btn_share_with_friends ->{
                Navigation.findNavController(view)
                    .navigate(ProfileManagementDirections.actionProfileManagementToShareWithFriends())
            }
            R.id.btn_car_details -> {

                Navigation.findNavController(view)
                    .navigate(
                        ProfileManagementDirections.actionProfileManagementToCarList()
                    )

            }
            R.id.btn_insurance -> {

                Navigation.findNavController(view)
                    .navigate(
                        ProfileManagementDirections.actionCompleteYourProfileToInsuranceYes()
                    )
            }
            R.id.btn_feedback -> {
                Navigation.findNavController(view)
                    .navigate(ProfileManagementDirections.actionCompleteYourProfileToFeedback())
            }
            R.id.btn_area_of_interest -> {

                Navigation.findNavController(view)
                    .navigate(
                        ProfileManagementDirections.actionCompleteYourProfileToAreaOfInterest()
                    )
            }
            R.id.drawerImage -> {
                requireActivity().onBackPressed()
//                startActivity(Intent(this@CameraCapture, Verification::class.java))

            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
    }

    override  fun onSuccess(obj: RequestHandler) {

    }

    override fun onError(obj: RequestHandler) {
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (v?.id) {
            R.id.btn_profile_details -> {
                if (event != null) {
                    selectorConstraintButton(v as ConstraintLayout, event!!)
                }
            }
            R.id.btn_car_details -> {
                if (event != null) {
                    selectorConstraintButton(v as ConstraintLayout, event!!)
                }

            }
            R.id.btn_insurance -> {
                if (event != null) {
                    selectorConstraintButton(v as ConstraintLayout, event!!)
                }

            }
            R.id.btn_feedback -> {
                if (event != null) {
                    selectorConstraintButton(v as ConstraintLayout, event!!)
                }

            }
            R.id.btn_area_of_interest -> {
                if (event != null) {
                    selectorConstraintButton(v as ConstraintLayout, event!!)
                }
            }
        }
        return false
    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_profile_management_new

    }


    fun setAccountProgress(value: Int) {
        var percentage = value
        if (percentage > 100) {
            percentage = 100
        } else if (percentage < 0) {
            percentage = 0
        }else if (percentage == 60) {
            percentage = 50
        }
        binding.actionBarCustom.pBar.setProgress(percentage)

    }


}