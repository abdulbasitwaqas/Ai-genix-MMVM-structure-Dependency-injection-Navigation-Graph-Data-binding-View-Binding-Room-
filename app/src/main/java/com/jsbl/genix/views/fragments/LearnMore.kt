package com.jsbl.genix.views.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.jsbl.genix.R
import com.jsbl.genix.databinding.AltFragmentHelpDeskBinding
import com.jsbl.genix.databinding.ButtonHelpDeskBinding
import com.jsbl.genix.model.help.HelpItem
import com.jsbl.genix.model.help.HelpList
import com.jsbl.genix.model.help.HelpResponseModelItem
import com.jsbl.genix.model.profileManagement.*
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.extensions.selectorHelpButton
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.viewModel.AreaOfInterestViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [LearnMore.newInstance] factory method to
 * create an instance of this fragment.
 */
class LearnMore :
    BaseFragment<AreaOfInterestViewModel, AltFragmentHelpDeskBinding>(AreaOfInterestViewModel::class.java) {


    private lateinit var prefs: SharePreferencesHelper
//    private var selectedReasonPosition: Int = -1

    private lateinit var helpList: ArrayList<HelpResponseModelItem>
    private var customerX = CustomerX()

    private lateinit var inflater: LayoutInflater
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
//        showPDialog()
        binding.onClickListener = this
        prefs = SharePreferencesHelper(requireContext())

        viewModel.fetchFromDatabase()
        observeDetails()
//        setDummyData()
//        viewModel.fetchDropDown()

    }

    fun observeDetails() {
        showPDialog()
        viewModel.customer.observe(viewLifecycleOwner, Observer {
            it?.let {
                customerX = it
//                addViews()
//                addViewsButtons()
                viewModel.getHelp()
                setAccountProgress(getProfilePercent(it))
            }
        })
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
//        binding.actionBarCustom.pBar.setProgress(percentage)
        /* val params =
             binding.accountProgressLayout.ivDropdown.getLayoutParams() as ConstraintLayout.LayoutParams
         params.horizontalBias =
             percentage.toFloat() / 100f // here is one modification for example. modify anything else you want :)
         binding.accountProgressLayout.ivDropdown.setLayoutParams(params)
         binding.accountProgressLayout.tvProgressDigits.text = "$percentage%"*/
    }

  /*  private fun setDummyData() {
        helpList.clear()
        helpList.add(
            HelpItem(
                id=1,
                icon = "R.drawable.ic_detail_scope",
                drawable = R.drawable.ic_progress_cornering,
                question = "How to Corner?",
                description = "Some Description"
            )
        )
        helpList.add(
            HelpItem(
                id=1,
                icon = "R.drawable.ic_detail_scope",
                drawable = R.drawable.ic_progress_breaking,
                question = "How to Improve Breaking?",
                description = "Some Description"
            )
        )
        helpList.add(
            HelpItem(
                id=1,
                icon = "R.drawable.ic_detail_scope",
                drawable =R.drawable.ic_progress_acceleration,
                question = "How to Improve Acceleration?",
                description = "Some Description"
            )
        )

        helpList.add(
            HelpItem(
                id=1,
                icon = "R.drawable.ic_detail_scope",
                drawable =R.drawable.ic_tripspeed_black,
                question = "How to Improve Speed?",
                description = "Some Description"
            )
        )

        helpList.add(
            HelpItem(
                id=1,
                icon = "R.drawable.ic_detail_scope",
                drawable =R.drawable.ic_dashboardistance_blac,
                question = "How to distance?",
                description = "Some Description"
            )
        )

        addViewsButtons()
    }*/


    fun addViewsButtons() {
        binding.areaLinear.removeAllViews()
        for (i in 0 until helpList.size) {
            val checkBinding: ButtonHelpDeskBinding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.button_help_desk,
                binding.areaLinear,
                true
            )
            checkBinding.welcomeBtnText = helpList[i].question
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkBinding.welcomeBtnIcon =helpList[i].imagePath
            }
            checkBinding.root.id = i
            checkBinding.innerCard.setOnClickListener {
                val selectedButton = false
                val action = LearnMoreDirections.actionLearnMoreToHelpDetail(helpList[i])
                Navigation.findNavController(it)
                    .navigate(action)
            }
            checkBinding.innerCard.setOnTouchListener { v, event ->
                if (event != null) {
                    selectorHelpButton((v as CardView).getChildAt(0) as ConstraintLayout, event)
                }
                false
            }
        }
    }

/*
    fun getPostAreaObject(interestX: InterestX): Interest {
        return Interest(customerX.iD!!, interestX.iD!!)
    }

    fun isItemCheckedFromCustomerList(id: Long): Boolean {
        for (i in customerX.customerInterests!!.indices) {
            if (customerX.customerInterests!![i].interestID == id) {
                return true
            }
        }
        return false
    }
*/


    override fun onClick(view: View, obj: Any) {
        when (view.id) {

            R.id.back -> {
                requireActivity().onBackPressed()
//                startActivity(Intent(this@CameraCapture, Verification::class.java))

            }
        }
    }

    override fun onLoading(obj: RequestHandler) {

    }

    override  fun onSuccess(obj: RequestHandler) {
        if (obj.any is ArrayList<*>){
            helpList = obj.any as ArrayList<HelpResponseModelItem>

            addViewsButtons()
        }
//        if (obj.any is ResponseFillDropDown) {
//            val rr = obj.any as ResponseFillDropDown
//            if (!rr.interests.isNullOrEmpty()) {
//
//                helpList.clear()
////                helpList.addAll(rr.interests as ArrayList<InterestX>)
//            }
//            if (helpList.isEmpty()) {
//                showPDialog()
//                viewModel.getDropDown()
//                showShort(requireContext(), "Please wait while getting details")
//                return
//            } else
//                viewModel.fetchFromDatabase()
//        } else  {
//            if (viewModel.getCustomer().percentage == null) {
//                setAccountProgress(getProfilePercent(viewModel.getCustomer()))
//            } else {
//                setAccountProgress(viewModel.getCustomer().percentage!!)
//            }
////                        showShort(requireContext(), "Details Added Successfully")
//            showOnlyAlertMessage(
//                context = requireContext(),
//                title = "Area Of Interest",
//                msg = "Details Added Successfully",
//                onPositiveClick = {
//                    requireActivity().onBackPressed()
//                }
//            )
//        }
    }

    override fun onError(obj: RequestHandler) {

    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_help_desk
    }


}