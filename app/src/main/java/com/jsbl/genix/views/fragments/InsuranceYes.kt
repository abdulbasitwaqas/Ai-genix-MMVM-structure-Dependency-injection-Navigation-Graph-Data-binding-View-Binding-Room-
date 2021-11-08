package com.jsbl.genix.views.fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jsbl.genix.R
import com.jsbl.genix.databinding.AltFragmentInsuranceYesBinding
import com.jsbl.genix.model.profileManagement.*
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.viewModel.CarDetailsViewModel
import com.jsbl.genix.views.adapters.DropDownArrayAdapter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [InsuranceYes.newInstance] factory method to
 * create an instance of this fragment.
 */
class InsuranceYes : BaseFragment<CarDetailsViewModel, AltFragmentInsuranceYesBinding>(
    CarDetailsViewModel::class.java
) {

    var datepicker: DatePickerDialog? = null
    private lateinit var prefs: SharePreferencesHelper
    private var selectedReasonPosition: Int = -1
    private lateinit var manufacturerAdapter: DropDownArrayAdapter
    var edDate: Date = Date()

    private var reasons = arrayListOf<NotInsuredReason>()

    var postCarDetail = PostCarDetail()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = SharePreferencesHelper(requireContext())
        binding.onClickListener = this
        clickListeners()
        setSpinners()
        viewModel.fetchFromDatabase()
        observeDetails()
        viewModel.fetchDropDown()
        /*if (manufactureList.isEmpty() || colorList.isEmpty() || makerList.isEmpty() ||*//* deliveryMethodList.isEmpty()|| deviceTypeList.isEmpty()||*//* motorTypeList.isEmpty()) {
            showPDialog()
            viewModel.getDropDown()
            showShort(requireContext(), "Please wait while getting details")
            return
        }*/
        setEditTypes()
    }


    private fun setEditTypes() {

        binding.edDate.et.inputType = InputType.TYPE_CLASS_DATETIME

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
        /*val params =
            binding.accountProgressLayout.ivDropdown.getLayoutParams() as ConstraintLayout.LayoutParams
        params.horizontalBias =
            percentage.toFloat() / 100f // here is one modification for example. modify anything else you want :)
        binding.accountProgressLayout.ivDropdown.setLayoutParams(params)
        binding.accountProgressLayout.tvProgressDigits.text = "$percentage%"*/
    }

    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner, Observer {
            it?.let {
                //TODO not to use this until we get complete the default car user story

                logD("**percentage", "insurance ${it.percentage}")
                setAccountProgress(getProfilePercent(it))

                if (!it.carDetails.isNullOrEmpty()) {
                    findDefaultPosition(it.carDetails!!)
                    if (viewModel.selectedPosition == -1) {
                        return@let
                    }
                    binding.customer = it.carDetails!![viewModel.selectedPosition]
                    postCarDetail = it.carDetails!![viewModel.selectedPosition]

                    getReasonSelected(postCarDetail.notInsuredReasonID!!)


                    val sDate1 = postCarDetail.renewalDate.toString()

                  /*  val simpleDateFormat =
                        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    val date = simpleDateFormat.parse(sDate1)
                    simpleDateFormat.applyPattern("dd-MM-yyyy")*/

                    binding.edDate.et.setText(sDate1)




                    logD(
                        "**insured", "insured: " + it.carDetails?.get(
                            SharePreferencesHelper.invoke(requireContext())
                                .getDefaultCarPos()
                        )!!.insured
                    )
                    if (it.carDetails?.get(
                            SharePreferencesHelper.invoke(requireContext())
                                .getDefaultCarPos()
                        )!!.insured
                    ) {
                        binding.radioGroup.check(R.id.radioYes)
                    } else {
                        binding.radioGroup.check(R.id.radioNo)
                    }


/*
                    it?.let {
                        if (it.carDetails?.get(
                            SharePreferencesHelper.invoke(requireContext())
                                .getDefaultCarPos()
                            )!!.insured) {
                            binding.radioGroup.check(R.id.radioYes)
                        } else {
                            binding.radioGroup.check(R.id.radioNo)
                        }
                    }*/


                }
            }
        })
    }

    private fun findDefaultPosition(carDetails: java.util.ArrayList<PostCarDetail>) {

        for (i in carDetails.indices) {
            if (carDetails[i].isDefaultCar) {
                viewModel.selectedPosition = i
                break
            }
        }

    }


    fun getReasonSelected(id: Long) {
        for (pos in 1..reasons.size - 1) {
            if (reasons[pos].iD == id) {
                selectedReasonPosition = pos
                binding.spinnerReason.dropDown.setText(reasons[pos].title, false)
                break
            }
        }
    }


    fun setSpinners() {
//        setDummyData()
//        val manufacturerAdapter = SpinnerAdapterManufacturer(requireContext(), reasons)
        manufacturerAdapter = DropDownArrayAdapter(context = requireContext(), objList = reasons)
        binding.spinnerReason.dropDown.setAdapter(manufacturerAdapter)
        binding.spinnerReason.dropDown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                selectedReasonPosition = position


                logD("**selectedItem", "" + reasons[position].title.toString())
                if (reasons[position].title.toString() == ("Other")) {
                    binding.commentET.til.visible()
                } else {
                    binding.commentET.til.gone()
                }
            }


    }

    fun setDummyData() {
        reasons.add(NotInsuredReason("", false, 1000, "", "", "test reason"))
        reasons.add(NotInsuredReason("", false, 1001, "", "", "test reason1"))
        reasons.add(NotInsuredReason("", false, 1003, "", "", "test reason2"))
        reasons.add(NotInsuredReason("", false, 1004, "", "", "test reason3"))

    }


    /*private void removeFocus(View view) {
        view.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }*/

    fun hideKeyboard(view: View?) {
        // Check if no view has focus:
        if (view != null) {
            val inputManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    var insuranceStatus = true

    private fun clickListeners() {

        binding.radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                // checkedId is the RadioButton selected
                when (checkedId) {
                    R.id.radioYes -> {
                        binding.bigForm.visible()
                        binding.smallForm.gone()
                        insuranceStatus = true
                    }
                    R.id.radioNo -> {
                        binding.bigForm.gone()
                        binding.smallForm.visible()
                        insuranceStatus = false
                    }
                }
            }
        })


        binding.edDate!!.et.setOnClickListener {
            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            var month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
//            month = month + 1
            var m = ""
            var d = ""
            if (month < 10) {
                m = "0" + month

            } else {
                month.toString()
            }
            if (day < 10) {
                d = "0" + day

            } else {
                day.toString()
            }
            // date picker dialog
            datepicker = DatePickerDialog(
                requireActivity(), R.style.my_dialog_theme,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    var selMonth = monthOfYear + 1
                    if (selMonth < 10) {
                        m = "0" + selMonth

                    } else {
                        m = selMonth.toString()
                    }

                    if (dayOfMonth < 10) {
                        d = "0" + dayOfMonth

                    } else {
                        d = dayOfMonth.toString()
                    }


                    binding.edDate!!.et.setText(
                        d + "" + "-" + (m) + "-" + year
                    )
                }, year, month, day
            )

            datepicker!!.show()

        }
        /*   binding.btnFeedback!!.setOnClickListener {

           }
           binding.btnFeedbackNo!!.setOnClickListener {

           }*/
    }


    private fun validateEdDate(): Boolean {

        if (binding.edDate.et.text.toString().trim().isNotEmpty()) {
            binding.edDate.til.error = null
            return true
        } else {
            binding.edDate.til.error = "Please Enter Date"
            return false
        }
    }

    private fun validateedNameCompany(): Boolean {
        if (binding.edNameCompany.et.text.toString().trim().isNotEmpty()) {
            binding.edNameCompany.til.error = null
            return true
        } else {
            binding.edNameCompany.til.error = "Please Enter CompanyName"
            return false
        }
    }

    private fun validateedDeductible(): Boolean {
        if (binding.edDeductible.et.text.toString().trim().isNotEmpty()) {
            binding.edDeductible.til.error = null
            return true
        } else {
            binding.edDeductible.til.error = "Please Enter Deductible"
            return false
        }
    }

    private fun validateedInsurancePremium(): Boolean {
        if (binding.edInsurancePremium.et.text.toString().trim().isNotEmpty()) {
            binding.edInsurancePremium.til.error = null
            return true
        } else {
            binding.edInsurancePremium.til.error = "Please Enter Insurance"
            return false
        }
    }

    private fun validateedReason(): Boolean {
        if (binding.edReason.et.text.toString().trim().isNotEmpty()) {
            binding.edReason.til.error = null
            return true
        } else {
            binding.edReason.til.error = "Please Enter Reason"
            return false
        }
    }

    private fun validateedImporvements(): Boolean {
        if (binding.edImporvements.et.text.toString().trim().isNotEmpty()) {
            binding.edImporvements.til.error = null
            return true
        } else {
            binding.edImporvements.til.error = "Please Enter Improvements"
            return false
        }
    }


    private fun validateMotorType(): Boolean {
        return if (selectedReasonPosition != -1) {
            binding.spinnerReason.til.error = null
            true
        } else {
            binding.spinnerReason.til.error = "Please Select Reason for not having insurance"
            false
        }
    }


    override fun onDetach() {
        super.onDetach()

    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.btnFeedbackNo -> {
                if (validateMotorType()) {
                    if (viewModel.selectedPosition == -1) {
                        showShort(requireContext(), "kindly Select Default Vehicle")
                        return
                    }
                    postCarDetail.insured = false
                    postCarDetail.flag = "U"
                    postCarDetail.InsFlag = "I"
                    postCarDetail.notInsuredReasonID = reasons[selectedReasonPosition].iD!!
                    val sDate1 = postCarDetail.renewalDate.toString()
                    postCarDetail.renewalDate = sDate1
                    showPDialog()
                    viewModel.addCarDetails(
                        postCarDetail, true
                    )
                }
            }
            R.id.btnFeedback -> {
                if (validateEdDate() && validateedNameCompany() && validateedDeductible() && validateedInsurancePremium() && validateedReason()
                ) {
                    if (viewModel.selectedPosition == -1) {
                        showShort(requireContext(), "kindly Select Default Vehicle")
                        return
                    }
                    postCarDetail.deductible = binding.edDeductible.et.text.toString().trim()
                    postCarDetail.insuranceCompany = binding.edNameCompany.et.text.toString().trim()
                    postCarDetail.insurancePremium =
                        binding.edInsurancePremium.et.text.toString().trim()
                    postCarDetail.insured = true
                    postCarDetail.InsFlag = "I"
                    postCarDetail.reasonForCurrentInsurer =
                        binding.edReason.et.text.toString().trim()

                    if (datepicker==null){
                        val sDate1 = postCarDetail.renewalDate.toString()

//                        val simpleDateFormat =
//                            SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
//                        val date = simpleDateFormat.parse(sDate1)
//                        simpleDateFormat.applyPattern("dd-MM-yyyy")
                        postCarDetail.renewalDate = sDate1



                    } else{
                        val date = getDateFromDatePicker(datepicker!!)

                        val simpleDateFormat =
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        val strDate: String = simpleDateFormat.format(date)


                        postCarDetail.renewalDate = strDate
                    }


                    postCarDetail.thingsThatCanBeImproved =
                        binding.edImporvements.et.text.toString().trim()
                    postCarDetail.flag = "U"

                    showPDialog()
                    viewModel.addCarDetails(
                        postCarDetail, true
                    )
                    /* viewModel.addCarDetails(
                        PostCarDetail(
                            null,
                            null,
                            0,
                            binding.edDeductible.et.text.toString().trim(),
                            null,
                            binding.edNameCompany.et.text.toString().trim(),
                            binding.edInsurancePremium.et.text.toString().trim(),
                            insuranceStatus.toString(),
                            null,
                            null,
                            null,
                            binding.edReason.et.text.toString().trim(),
                            null,
                            binding.edDate.et.text.toString().trim(),
                            binding.edImporvements.et.text.toString().trim()

                        )
                    )*/
                }
            }
            R.id.drawerImage -> {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
    }

    override fun onSuccess(obj: RequestHandler) {
        if (insuranceStatus) {
//                            showShort(requireContext(), "Details Added Successfully")
            if (obj.any is ResponseFillDropDown) {
                val rr = obj.any as ResponseFillDropDown
                if (!rr.notInsuredReasons.isNullOrEmpty()) {
                    reasons.clear()
                    reasons.addAll(rr.notInsuredReasons as ArrayList<NotInsuredReason>)
                }
                if (reasons.isEmpty()) {
                    showPDialog()
                    viewModel.getDropDown()
                    showShort(requireContext(), "Please wait while getting details")
                    return
                } else {
                    manufacturerAdapter.notifyDataSetChanged()
                    viewModel.fetchFromDatabase()
                }
            } else {
                if (viewModel.getCustomer().percentage == null) {
                    setAccountProgress(getProfilePercent(viewModel.getCustomer()))
                } else {
                    setAccountProgress(viewModel.getCustomer().percentage!!)
                }
                showOnlyAlertMessage(
                    context = requireContext(),
                    title = "Insurance Details",
                    msg = "Details Added Successfully",
                    onPositiveClick = {
                        requireActivity().onBackPressed()
                    }
                )
            }
        } else {
//                            showShort(requireContext(), "Thanks for your feedback")
            if (obj.any is ResponseFillDropDown) {
                val rr = obj.any as ResponseFillDropDown
                reasons.clear()
                reasons.addAll(rr.notInsuredReasons as ArrayList<NotInsuredReason>)
                if (reasons.isEmpty()) {
                    showPDialog()
                    viewModel.getDropDown()
                    showShort(requireContext(), "Please wait while getting details")
                    return
                } else {
                    manufacturerAdapter.notifyDataSetChanged()
                    viewModel.fetchFromDatabase()
                }
            } else
                showOnlyAlertMessage(
                    context = requireContext(),
                    title = "Feedback",
                    msg = "Thanks for your feedback",
                    onPositiveClick = {
                        requireActivity().onBackPressed()
                    }
                )

        }
        if (viewModel.getCustomer().percentage == null) {
            setAccountProgress(getProfilePercent(viewModel.getCustomer()))
        } else {
            setAccountProgress(viewModel.getCustomer().percentage!!)
        }
    }

    override fun onError(obj: RequestHandler) {
    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_insurance_yes
    }


    fun getDateFromDatePicker(datePickerDialg: DatePickerDialog): Date {
        val datePicker = datePickerDialg.datePicker
        var day = datePicker.getDayOfMonth()
        var month = datePicker.getMonth()
        var year = datePicker.getYear()
        var calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return calendar.time
    }
}