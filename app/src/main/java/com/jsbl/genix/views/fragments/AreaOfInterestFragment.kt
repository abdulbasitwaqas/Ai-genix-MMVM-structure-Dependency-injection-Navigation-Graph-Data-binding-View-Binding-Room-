package com.jsbl.genix.views.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.jsbl.genix.R
import com.jsbl.genix.databinding.AltFragmentAreaOfInterestBinding
import com.jsbl.genix.model.NetworkModel.GetCustomerInterestByIDRequest
import com.jsbl.genix.model.profileManagement.*
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.viewModel.AreaOfInterestViewModel
import com.jsbl.genix.views.adapters.AreaOfInterestAdapter


class AreaOfInterestFragment :
    BaseFragment<AreaOfInterestViewModel, AltFragmentAreaOfInterestBinding>(AreaOfInterestViewModel::class.java) {


    private var areaOfInterestList: List<InterestSubInterest>? = ArrayList()
    private var customerInterestIdsList: java.util.ArrayList<GetCustomerInterestByIdItem> =
        ArrayList()
    private lateinit var prefs: SharePreferencesHelper
//    private var selectedReasonPosition: Int = -1

    private var interestXList = arrayListOf<InterestX>()
    private var selectedAreaList = arrayListOf<Interest>()
    private var customerX = CustomerX()
    private lateinit var areaOfInterestAdapter: AreaOfInterestAdapter
    private lateinit var inflater: LayoutInflater
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        if (isNetworkAvailable(requireContext())) {

            val getCustomerInterestByIDRequest = GetCustomerInterestByIDRequest(
                SharePreferencesHelper.invoke(requireContext()).getCustomerId().toString()
            )
            viewModel.getInterestsByID(getCustomerInterestByIDRequest)
            observeDetails()
            viewModel.fetchFromDatabase()
            viewModel.fetchDropDown()
        } else {
            showShort(requireContext(), "" + resources.getString(R.string.internt_problem))
        }

//        showPDialog()
        binding.onClickListener = this
        prefs = SharePreferencesHelper(requireContext())



        binding.actionBarCustom.title.setText("Area of Interest")


    }

    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner, Observer {
            it?.let {
                customerX = it
                logD("**percentage", "area of interest ${it.percentage}")
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
        } else if (percentage == 60) {
            percentage = 50
        }
        binding.actionBarCustom.pBar.setProgress(percentage)
    }

    fun setDummyData() {
        interestXList.add(InterestX("", false, 1000, "", "", "Books"))
        interestXList.add(InterestX("", false, 1001, "", "", "Shoppings"))
        interestXList.add(InterestX("", false, 1003, "", "", "Games"))
        interestXList.add(InterestX("", false, 1004, "", "", "Movies"))
//        addViews()
//        addViewsButtons()
    }

    /*   fun addViews() {

           for (i in 0 until interestXList.size) {
               val checkBinding: LayoutCheckBoxBinding = DataBindingUtil.inflate(
                   layoutInflater,
                   R.layout.layout_check_box,
                   binding.areaLinear,
                   true
               )
               checkBinding.title.text = interestXList[i].title
               checkBinding.checkOption.setOnCheckedChangeListener { buttonView, isChecked ->
                   if (isChecked) {
                       selectedAreaList.add(getPostAreaObject(interestXList[i]))
                       checkBinding.imageTickBooks.visible()
                   } else {
                       selectedAreaList.removeAt(filterPostAreaInterest(interestXList[i].iD!!))
                       checkBinding.imageTickBooks.gone()
                   }
               }
               checkBinding.checkOption.setId(i)
               checkBinding.checkOption.isChecked =
                   isItemCheckedFromCustomerList(interestXList[i].iD!!)
   //            binding.areaLinear.addView(checkBinding.root)
           }
       }

       fun addViewsButtons() {

           for (i in 0 until interestXList.size) {
               val checkBinding: ButtonAreaOfIterestBinding = DataBindingUtil.inflate(
                   layoutInflater,
                   R.layout.button_area_of_iterest,
                   binding.areaLinear,
                   true
               )
               checkBinding.welcomeBtnText = interestXList[i].title
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                   checkBinding.welcomeBtnIcon = requireContext().getDrawable(R.drawable.new_ic_paper)
               }
               checkBinding.root.id = i
               checkBinding.innerCard.setOnClickListener {
                   val selectedButton = false
                   if (isItemSelectedFromSelectedAreaList(interestXList[i].iD!!)) {
                       toggleCardButton(it as CardView, false)
                       selectedAreaList.removeAt(filterPostAreaInterest(interestXList[i].iD!!))
                   } else {
                       toggleCardButton(it as CardView, true)
                       selectedAreaList.add(getPostAreaObject(interestXList[i]))
                   }
               }
               if (isItemCheckedFromCustomerList(interestXList[i].iD!!)) {
                   toggleCardButton(
                       checkBinding.innerCard as CardView,
                       isItemCheckedFromCustomerList(interestXList[i].iD!!)
                   )
                   selectedAreaList.add(getPostAreaObject(interestXList[i]))
               } else {
                   toggleCardButton(
                       checkBinding.innerCard as CardView,
                       isItemCheckedFromCustomerList(interestXList[i].iD!!)
                   )

               }

               */
    /*checkBinding.checkOption.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    selectedAreaList.add(getPostAreaObject(interestXList[i]))
                    checkBinding.imageTickBooks.visible()
                } else {
                    selectedAreaList.removeAt(filterPostAreaInterest(interestXList[i].iD!!))
                    checkBinding.imageTickBooks.gone()
                }
            }
            checkBinding.checkOption.setId(i)
            checkBinding.checkOption.isChecked = isItemChecked(interestXList[i].iD!!)*//*

            *//*if (i % 2 == 1)
                binding.areaLinear.addView(checkBinding.root)*//*
        }
    }*/
/*
    fun filterPostAreaInterest(id: Long): Int {
        for (i in selectedAreaList.indices) {
            if (selectedAreaList[i].interestID!!.toLong() == id) {
                return i
            }
        }

        return -1
    }*/
/*
    fun getPostAreaObject(interestX: InterestX): Interest {
//        return Interest(customerX.iD!!, interestX.iD!!)
    }*/

    /*  fun isItemCheckedFromCustomerList(id: Long): Boolean {
        *//*  for (i in customerX.customerInterests!!.indices) {
            if (customerX.customerInterests!![i].interestID == id) {
                return true
            }
        }*//*
        return false
    }*/


    /*  fun isItemSelectedFromSelectedAreaList(id: Long): Boolean {
        *//*  for (i in selectedAreaList!!.indices) {
            if (selectedAreaList!![i].interestID == id) {
                return true
            }
        }
        return false*//*
    }
*/

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            /*R.id.btnInterestSubmit -> {
                if (selectedAreaList.size == 0) {
                    showShort(requireContext(), "Please Select an option")
                    return
                }
                showPDialog()
                viewModel.addAreaOfInterest(selectedAreaList)
            }*/
            R.id.drawerImage -> {
                requireActivity().onBackPressed()
//                startActivity(Intent(this@CameraCapture, Verification::class.java))

            }
        }
    }

    override fun onLoading(obj: RequestHandler) {

    }

    override fun onSuccess(obj: RequestHandler) {
        if (obj.any is ResponseFillDropDown) {
            val rr = obj.any as ResponseFillDropDown
            if (!rr.InterestSubInterest.isNullOrEmpty()) {

//                interestXList.clear()
//                interestXList.addAll(rr.interests as ArrayList<InterestX>)
                logD("**areaOfInterest", "" + rr.InterestSubInterest)
                areaOfInterestList = rr.InterestSubInterest
                areaOfInterestAdapter = AreaOfInterestAdapter(areaOfInterestList, requireContext())
                val gridLayoutManager = GridLayoutManager(requireContext(), 2)
                binding.interestRV.setLayoutManager(gridLayoutManager)
                binding.interestRV.adapter = areaOfInterestAdapter

            }
//            viewModel.getDropDown()

            /*if (interestXList.isEmpty()) {
                showPDialog()
                viewModel.getDropDown()
                showShort(requireContext(), "Please wait while getting details")
                return
            } else
                viewModel.fetchFromDatabase()*/
        } else if (obj.any is ArrayList<*>) {
            customerInterestIdsList = obj.any as ArrayList<GetCustomerInterestByIdItem>
            areaOfInterestList?.forEach { interest ->
                for (model in customerInterestIdsList) {
                    if (model.interestID == interest.interestID) {
                        interest.isSelected = true
                        areaOfInterestAdapter.notifyDataSetChanged()
                        break
                    }
                }
            }

        } else {
            if (viewModel.getCustomer().percentage == null) {
                setAccountProgress(getProfilePercent(viewModel.getCustomer()))
            } else {
                setAccountProgress(viewModel.getCustomer().percentage!!)
            }
//                        showShort(requireContext(), "Details Added Successfully")
            showOnlyAlertMessage(
                context = requireContext(),
                title = "Area Of Interest",
                msg = "Details Added Successfully",
                onPositiveClick = {
                    requireActivity().onBackPressed()
                }
            )
        }
    }

    override fun onError(obj: RequestHandler) {

    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_area_of_interest
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}