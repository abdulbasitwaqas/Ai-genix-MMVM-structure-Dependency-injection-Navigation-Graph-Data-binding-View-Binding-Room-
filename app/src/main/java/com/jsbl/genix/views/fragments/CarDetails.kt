package com.jsbl.genix.views.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.databinding.AltFragmentCarDetailsBinding
import com.jsbl.genix.model.policy.*
import com.jsbl.genix.model.profileManagement.*
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.registration.LoginMdl
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.extensions.showConfirmationDialog
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.viewModel.CarDetailsViewModel
import com.jsbl.genix.views.activities.ActCarDetails.Companion.INTENT_FROM_CREATE_NEW
import com.jsbl.genix.views.activities.ActCarDetails.Companion.INTENT_FROM_REG
import com.jsbl.genix.views.activities.ActCarDetails.Companion.INTENT_FROM_SELECTED_POSITION
import com.jsbl.genix.views.activities.ActivityMain
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_CUSTOMER
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_POLICY_PASSWORD
import com.jsbl.genix.views.adapters.*
import com.scope.portalapiclient.PortalApi
import com.scope.portalapiclient.ServiceError
import kotlinx.android.synthetic.main.alt_fragment_car_details.*
import kotlinx.android.synthetic.main.alt_fragment_car_list.*
import kotlinx.android.synthetic.main.filter_layout.*
import kotlinx.coroutines.launch

import java.util.*
import kotlin.collections.ArrayList


class CarDetails : MotorTypeAdapter.MotorClick, PurposeAdapter.PurposeClick,
    ColorAdapter.ColorsClicks, YearAdapter.YearClicks, VehicleAdapter.MakerClick,
    ManufacturingAdapter.Clicks,
    BaseFragment<CarDetailsViewModel, AltFragmentCarDetailsBinding>(
        CarDetailsViewModel::class.java
    ) {
    companion object {
        const val CHECKED = "1"
        const val UNCHECKED = "0"
    }

    private var manufactureList = arrayListOf<Manufacturer>()

    /*  private var deviceTypeList = arrayListOf<DeviceType>()
      private var deliveryMethodList = arrayListOf<DeliveryMethod>()*/
    private var motorTypeList = arrayListOf<MotorType>()
    private var colorList = arrayListOf<Color>()

    //    private var makerList = arrayListOf<CarModelss>()
    private var purposeList = arrayListOf<Purpose>()
    private var subManufacturerModelList = arrayListOf<SubManufacturerModel>()
    private var registrationYearModelList = arrayListOf<String>()

    val subManufacturerModelll: MutableList<SubManufacturerModel> =
        java.util.ArrayList<SubManufacturerModel>()

    /*  private var selectedManufacturerPosition: Int = -1
      private var selectedMakerPosition: Int = -1
      private var selectedColorPosition: Int = -1
      private var selectedDeliverMethodPosition: Int = -1
      private var selectedDeviceTypePosition: Int = -1
      private var selectedMotorTypePosition: Int = -1*/

    private var selectedManufacturerPos: Int = -1
    private var selectedMakerPos: Int = -1
    private var selectedColorPos: Int = -1
    private var selectedMotorTypePos: Int = -1
    private var selectPurposeTypePos: Int = -1
    private var SubManufacturerModelPos: Int = -1
    private var RegistrationYearModelPos: Int = -1

    private lateinit var linearLayoutManager: LinearLayoutManager


    private lateinit var searchAdapter: ManufacturingAdapter
    private lateinit var manufacturerAdapter: DropDownArrayAdapter

    //    private lateinit var makerAdapter: DropDownArrayAdapter
    lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var purposeAdapter: PurposeAdapter

    //    private lateinit var colorAdapter: DropDownArrayAdapter
    private lateinit var colorAdapter: ColorAdapter
    private lateinit var yearAdapter: YearAdapter
    private lateinit var deliveryMethodAdapter: DropDownArrayAdapter
    private lateinit var deviceTypeAdapter: DropDownArrayAdapter

    //    private lateinit var motorTypeAdapter: DropDownArrayAdapter
    private lateinit var motorTypeAdapter: MotorTypeAdapter


    var postCarDetail = PostCarDetail()
    lateinit var dialog: Dialog
    var searchingManString: String = ""
    var makerString: String = ""
    var colorString: String = ""
    var yearString: String = ""
    var motorTypeString: String = ""
    var purposeTypeString: String = ""
    var customerX: CustomerX? = null
    var stringIMEI: String? = ""

    //    var searchRV: RecyclerView
    lateinit var searchRVV: RecyclerView
    var manufacturerid: Long? = -1
    var isDefaultCar: Boolean = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onViewCreated(view, savedInstanceState)
        setSpinners()
        buttonGetIMEI()

        arguments?.let {
            viewModel.fromReg = it.getBoolean(INTENT_FROM_REG, false)
            viewModel.createNew = it.getBoolean(INTENT_FROM_CREATE_NEW, true)
            viewModel.selectedPosition = it.getInt(INTENT_FROM_SELECTED_POSITION, -1)
            isDefaultCar = it.getBoolean("isDefaultCar", false)

            if (viewModel.createNew) {
                binding.edRegistration.et.isClickable = true
            } else if (!viewModel.createNew) {
                binding.edRegistration.et.isClickable = false
            }
            customerX = it.getParcelable<CustomerX>(INTENT_CUSTOMER)
            viewModel.policyPassword = it.getString(INTENT_POLICY_PASSWORD)

//            fromReg = CarDetailsArgs.fromBundle(it).

        }
//        vehicleAdapter = VehicleAdapter(mutableListOf())
        vehicleAdapter = VehicleAdapter(
            mutableListOf(),
            this,
            context
        )
        notifySpinners()

        viewModel.fetchFromDatabase()
        observeDetails()
        if (!viewModel.fromReg) {

            viewModel.fetchDropDown()

            if (viewModel.createNew) {
                binding.markDefault.isChecked = true
                binding.markDefault.isEnabled = true
            } else {
                logD("**carListSize", "" + customerX?.carDetails?.size)
                if (customerX?.carDetails?.size == 1 || isDefaultCar) {
                    binding.markDefault.isEnabled = false
                } else {
                    binding.markDefault.isEnabled = true
                }

                binding.btnNextCarDetails.text = "Update"
                binding.edRegistration.et.isEnabled = false
                binding.edRegistration.til.isEnabled = false
            }
            viewModel.policyPassword =
                SharePreferencesHelper.invoke(requireContext()).getRegPassword()
            binding.spinnerManufacturer.dropDown.isClickable = true
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                              binding.markDefault.focusable = View.FOCUSABLE
                          }*/
        } else {

            if (customerX != null) {
                viewModel.setCustomerDetails(customerX!!)
//                    viewModel.setTempAuth()
            }
            showPDialog()
            viewModel.getDropDown()
            binding.btnNextCarDetails.visible()
            binding.markDefault.isChecked = true
            binding.markDefault.isEnabled = false
        }

//        clickListeners()polic
        binding.onClickListener = this

        val thisYear: Int = Calendar.getInstance().get(Calendar.YEAR)
        for (i in 1900..thisYear) {
            registrationYearModelList.add(Integer.toString(i))
        }
        registrationYearModelList.reverse()
    }

    fun buttonGetIMEI() {
        val telephonyManager =
            context?.getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
        stringIMEI =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                telephonyManager.imei
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Settings.Secure.getString(context?.getContentResolver(), Settings.Secure.ANDROID_ID)


            } else {
                telephonyManager.deviceId

            }
        //TODO comment out below line for random imei
//        stringIMEI = stringIMEI + System.currentTimeMillis()
    }

    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner, Observer {
            it?.let {
                customerX = it

                //TODO not to use this until we get complete the default car user story
                setAccountProgress(getProfilePercent(it))
                if (!it.carDetails.isNullOrEmpty()) {
                    if (viewModel.createNew) {
                        return@let
                    }
                    if (viewModel.selectedPosition == -1) {
                        findDefaultPosition(it.carDetails!!)
                        if (viewModel.selectedPosition == -1) {
                            return@let
                        }
                    }
                    if (it.carDetails!!.size == 1) {
                        binding.markDefault.isClickable = true
                    }
                    binding.customer = it.carDetails!![viewModel.selectedPosition]
                    postCarDetail = it.carDetails!![viewModel.selectedPosition]

                    if (postCarDetail.colorID != null)
                        getColorSelected(postCarDetail.colorID!!)

                    if (postCarDetail.registrationYear != null)
                        getYearSelected(postCarDetail.registrationYear!!.toLong())

                    // for manufacturer
                    if (postCarDetail.manufacturerID != null) {
                        getSelectedModelList(postCarDetail.manufacturerID!!)
                        getManufactureSelected(postCarDetail.manufacturerID!!)
                    }



                    if (postCarDetail.modelID != null)
                        getSubManList(postCarDetail.modelID!!)




                    /*     if (postCarDetail.makeID != null)
                             getMakeSelected(postCarDetail.makeID!!)*/

                    if (postCarDetail.purposeID != null)
                        getPurposeSelected(postCarDetail.purposeID!!)


                    binding.markDefault.isChecked = postCarDetail.isDefaultCar

                    /* if (postCarDetail.policyDeliveryMethod != null)
                       try {
                           getDeliveryMethodSelected(postCarDetail.policyDeliveryMethod!!.toLong())
                       } catch (e: Exception) {

                       }*/
                    /*if (postCarDetail.policyDeviceType != null)
                        try {
                            getDeviceTypeSelected(postCarDetail.policyDeviceType!!.toLong())

                        } catch (e: Exception) {

                        }*/

                    if (postCarDetail.policyMotorType != null)
                        try {
                            getMotorTypeSelected(postCarDetail.policyMotorType!!.toLong())

                        } catch (e: Exception) {

                        }

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

    fun setAccountProgress(value: Int) {
        var percentage = value
        if (percentage > 100) {
            percentage = 100
        } else if (percentage < 0) {
            percentage = 0
        } else if (percentage == 60) {
            percentage = 50
        }
        binding.actionBarCustom.pBar.progress = percentage

        /*  val params =
                  binding.accountProgressLayout.ivDropdown.getLayoutParams() as ConstraintLayout.LayoutParams
              params.horizontalBias =
                  percentage.toFloat() / 100f // here is one modification for example. modify anything else you want :)
              binding.accountProgressLayout.ivDropdown.setLayoutParams(params)
              binding.accountProgressLayout.tvProgressDigits.text = "$percentage%"*/
    }

    fun getManufactureSelected(id: Long) {
        for (pos in manufactureList.indices) {
            if (manufactureList[pos].iD == id) {
                selectedManufacturerPos = pos
                binding.spinnerManufacturer.dropDown.setText(
                    manufactureList[pos].name,
                    false
                )

                break
            }
        }
    }

    fun getColorSelected(id: Long) {
        for (pos in colorList.indices) {
            if (colorList[pos].iD == id) {
                selectedColorPos = pos
                colorString = colorList[pos].name!!
                binding.spinnerColor.dropDown.setText(colorList[pos].name, false)
                break
            }
        }
    }
 /*   fun getColorSelectedDef(id: Long): Long {
        for (pos in colorList.indices) {
            if (colorList[pos].iD == id) {
                selectedColorPos = pos
                binding.spinnerColor.dropDown.setText(colorList[pos].name, false)
                break
            }
        }
        return colorList.
    }*/

    fun getYearSelected(year: Long) {
        for (pos in registrationYearModelList.indices) {
            if (registrationYearModelList[pos].toLong() == year) {
                RegistrationYearModelPos = pos
                binding.edRegistrationYear.dropDown.setText(registrationYearModelList[pos], false)
                break
            }
        }
    }

    fun getMakeSelected(id: Long) {
        for (pos in subManufacturerModelList.indices) {
            if (subManufacturerModelList[pos].iD == id) {
                selectedMakerPos = pos
                binding.spinnerMaker.dropDown.setText(subManufacturerModelList[pos].name, false)
                break
            }
        }
    }

    fun getSubManList(id: Long) {
        for (pos in subManufacturerModelll.indices) {
            if (subManufacturerModelll[pos].iD == id) {
                SubManufacturerModelPos = pos
                binding.spinnerMaker.dropDown.setText(subManufacturerModelll[pos].name, false)
                break
            }
        }
    }
    fun getManufacturerList(id: Long) {
        for (pos in manufactureList.indices) {
            if (manufactureList[pos].iD == id) {
                selectedManufacturerPos = pos
                binding.spinnerManufacturer.dropDown.setText(subManufacturerModelll[pos].name, false)
                break
            }
        }
    }

    fun getSelectedModelList(id: Long) {
        subManufacturerModelll.clear()
        for (pos in subManufacturerModelList.indices) {
            if (subManufacturerModelList[pos].ManufacturerID == id) {
                subManufacturerModelll.add(subManufacturerModelList.get(pos))
            }
        }
    }

    fun getPurposeSelected(id: Long) {
        for (pos in purposeList.indices) {
            if (purposeList[pos].iD == id) {
                selectPurposeTypePos = pos
                binding.spinnerPurpose.dropDown.setText(purposeList[pos].name, false)
                break
            }
        }
    }

/*
    fun getDeliveryMethodSelected(id: Long) {
        for (pos in deliveryMethodList.indices) {
            if (deliveryMethodList[pos].iD == id) {
                selectedDeliverMethodPosition = pos
                binding.spinnerDeliveryMethod.setSelection(pos)
                break
            }
        }
    }*/
/*

    fun getDeviceTypeSelected(id: Long) {
        for (pos in deviceTypeList.indices) {
            if (deviceTypeList[pos].iD == id) {
                selectedDeviceTypePosition = pos
                binding.spinnerDeviceType.setSelection(pos)
                break
            }
        }
    }
*/

    fun getMotorTypeSelected(id: Long) {
        for (pos in motorTypeList.indices) {
            if (motorTypeList[pos].iD == id) {
                selectedMotorTypePos = pos
//                binding.spinnerMotorType.setSelection(pos)
                binding.spinnerMotorType.dropDown.setText(motorTypeList[pos].name, false)

                break
            }
        }
    }

    fun setSpinners() {

//        setDummyData()
//        manufacturerAdapter = SpinnerAdapterManufacturer(requireContext(), manufactureList)
//        manufacturerAdapter = DropDownArrayAdapter(context = requireContext(), objList = manufactureList)

/*
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, arr)
        logD("**listItems",""+arr.size)*/
//        manufacturerAdapter = DropDownArrayAdapter(context = requireContext(), objList = arr)


/*
        binding.spinnerManufacturer.dropDown.threshold = 1
        binding.spinnerManufacturer.dropDown.setAdapter(manufacturerAdapter)
        manufacturerAdapter.notifyDataSetChanged()





        binding.spinnerManufacturer.dropDown.onItemClickListener =
            object : AdapterView.OnItemClickListener {
                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedManufacturerPosition = position

                }

            }*/


/*
        binding.spinnerManufacturer.dropDown.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    binding.spinnerManufacturer.searchAbleSpinner.selectedItem?.title?.let { title ->
                        selectedManufacturerPosition = position
                    }
                }
            }
*/


//        makerAdapter = SpinnerAdapterManufacturer(requireContext(), makerList)


        binding.spinnerManufacturer.dropDown.setOnClickListener() {
            showFilter()
        }

        binding.spinnerManufacturer.til.setEndIconOnClickListener {
            showFilter()
        }

        binding.spinnerMaker.dropDown.setOnClickListener() {
            if (binding.spinnerManufacturer.dropDown.equals("${resources.getString(R.string.manufacturer)}")) {
                showShort(requireContext(), "Kindly select Manufacturer")
            } else
                showMakerFilter(subManufacturerModelList)
        }

        binding.spinnerMaker.til.setEndIconOnClickListener() {
            if (binding.spinnerManufacturer.dropDown.equals("${resources.getString(R.string.manufacturer)}")) {
                showShort(requireContext(), "Kindly select Manufacturer")
            } else
                showMakerFilter(subManufacturerModelList)
        }

        binding.spinnerPurpose.dropDown.setOnClickListener() {
            showPurposeFilter()
        }

        binding.spinnerPurpose.til.setEndIconOnClickListener {
            showPurposeFilter()
        }

        binding.spinnerColor.dropDown.setOnClickListener() {
            colorMakerFilter()
        }
        binding.spinnerColor.til.setEndIconOnClickListener() {
            colorMakerFilter()
        }

        binding.spinnerMotorType.dropDown.setOnClickListener() {
            motorTypeFilter()
        }
        binding.spinnerMotorType.til.setEndIconOnClickListener() {
            motorTypeFilter()
        }

        binding.edRegistrationYear.dropDown.setOnClickListener() {
            yearMakerFilter()
        }
        binding.edRegistrationYear.til.setEndIconOnClickListener() {
            yearMakerFilter()
        }


        /*    makerAdapter = DropDownArrayAdapter(context = requireContext(), objList = makerList)

            binding.spinnerMaker.dropDown.setAdapter(makerAdapter)

            binding.spinnerMaker.dropDown.onItemClickListener =
                object : AdapterView.OnItemClickListener {
                    override fun onItemClick(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedMakerPosition = position

                    }
                }*/

//        colorAdapter = SpinnerAdapterManufacturer(requireContext(), colorList)
        /*  colorAdapter = DropDownArrayAdapter(context = requireContext(), objList = colorList)
          binding.spinnerColor.dropDown.setAdapter(colorAdapter)
          binding.spinnerColor.dropDown.onItemClickListener =
              AdapterView.OnItemClickListener { parent, view, position, id ->
                  selectedColorPosition = position
              }*/
        /* deliveryMethodAdapter = SpinnerAdapterManufacturer(requireContext(), deliveryMethodList)
         binding.spinnerDeliveryMethod.adapter = deliveryMethodAdapter
         binding.spinnerDeliveryMethod.onItemSelectedListener =
             object : AdapterView.OnItemSelectedListener {
                 override fun onNothingSelected(parent: AdapterView<*>?) {

                 }

                 override fun onItemSelected(
                     parent: AdapterView<*>?,
                     view: View?,
                     position: Int,
                     id: Long
                 ) {
                     selectedDeliverMethodPosition = position
                 }

             }
         deviceTypeAdapter = SpinnerAdapterManufacturer(requireContext(), deviceTypeList)
         binding.spinnerDeviceType.adapter = deviceTypeAdapter
         binding.spinnerDeviceType.onItemSelectedListener =
             object : AdapterView.OnItemSelectedListener {
                 override fun onNothingSelected(parent: AdapterView<*>?) {

                 }

                 override fun onItemSelected(
                     parent: AdapterView<*>?,
                     view: View?,
                     position: Int,
                     id: Long
                 ) {
                     selectedDeviceTypePosition = position
                 }

             }*/
//        motorTypeAdapter = SpinnerAdapterManufacturer(requireContext(), motorTypeList)
        /*    motorTypeAdapter =
                DropDownArrayAdapter(context = requireContext(), objList = motorTypeList)
            binding.spinnerMotorType.dropDown.setAdapter(motorTypeAdapter)
            binding.spinnerMotorType.dropDown.onItemClickListener =
                object : AdapterView.OnItemClickListener {
                    override fun onItemClick(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedMotorTypePosition = position

                    }

                }*/
    }

    /*   
       private fun showDialog() {

           val dialog = Dialog(this.requireContext())
           dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
           dialog.setCancelable(false)
           dialog.setContentView(R.layout.alert_dialog)

           val itemsRV = dialog.findViewById(R.id.itemsRV) as RecyclerView
           val searchET = dialog.findViewById(R.id.searchET) as EditText

           binding.spinnerManufacturer.dropDown.setAdapter(manufacturerAdapter)
           itemsRV.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
           manufacturerAdapter = DropDownArrayAdapter(context = requireContext(), objList = manufactureList)
           itemsRV.adapter = manufacturerAdapter


           */
    /* yesBtn.setOnClickListener {
             dialog.dismiss()
         }
         noBtn.setOnClickListener { dialog.dismiss() }*/
    /*




        dialog.show()
    }*/

    fun notifySpinners() {
        //commented by basit on 07/26/21 for testing
//        manufacturerAdapter.notifyDataSetChanged()
//        colorAdapter.notifyDataSetChanged()
//        makerAdapter.notifyDataSetChanged()
        /*   deliveryMethodAdapter.notifyDataSetChanged()
           deviceTypeAdapter.notifyDataSetChanged()*/
//        motorTypeAdapter.notifyDataSetChanged()

    }


    fun setDummyData() {
        manufactureList.add(Manufacturer(1000, "Honda"))
        manufactureList.add(Manufacturer(1001, "Toyota"))
        manufactureList.add(Manufacturer(1002, "Suzuki"))

        colorList.add(Color(1000, "Red"))
        colorList.add(Color(1001, "Blue"))
        colorList.add(Color(1002, "Green"))
        colorList.add(Color(1003, "Yellow"))

    }

    private fun clickListeners() {
        if (validateManufacturer() && validateMaker() && validateRegistrationNumber() && validateRegisterationYear() && validateColor() && validatePurPose() /*&& validateChassisNo()*/
            /* && validatePolicyNumber() && validateVehicleInsuranceNumber()*/ && validateMotorType()
        ) {
            if (!viewModel.createNew) {
                if (viewModel.selectedPosition == -1) {
                    showShort(requireContext(), "Kindly Select Car")
                    return
                }
            }
            val rnds = (0..10).random()
            postCarDetail.customerID = customerX!!.iD
            postCarDetail.isDefaultCar = binding.markDefault.isChecked


            postCarDetail.chasisNo = binding.edChassis.et.text.toString().trim()
            postCarDetail.engineNo = binding.edEngine.et.text.toString().trim()
            postCarDetail.colorID = colorList[selectedColorPos].iD!!.toLong()
            postCarDetail.CarColorPath = when (colorString) {
                "Black" -> {
                    Constants.BLACK_CAR_ICON
                }
                "White" -> {
                    Constants.WHITE_CAR_ICON
                }
                "Gray" -> {
                    Constants.GRAY_CAR_ICON
                }
                "Blue" -> {
                    Constants.BLUE_CAR_ICON
                }
                else -> {
                    Constants.BLACK_CAR_ICON
                }
            }
            postCarDetail.modelID = subManufacturerModelll[SubManufacturerModelPos].iD!!.toLong()
            postCarDetail.purposeID = purposeList[selectPurposeTypePos].iD!!.toLong()
            postCarDetail.manufacturerID = manufactureList[selectedManufacturerPos].iD!!.toLong()
            postCarDetail.registrationNo = binding.edRegistration.et.text.toString().trim()
            postCarDetail.registrationYear =
                binding.edRegistrationYear.dropDown.text.toString().trim()
            /*postCarDetail.policyNumber = binding.edPolicyNumber.et.text.toString().trim()
            postCarDetail.policyVin = binding.edVin.et.text.toString().trim()
            */postCarDetail.policyNumber = binding.edRegistration.et.text.toString().trim()
            postCarDetail.policyVin = binding.edRegistration.et.text.toString().trim() + rnds
            postCarDetail.policyDeliveryMethod = ""
//                    deliveryMethodList[selectedDeliverMethodPosition].iD!!.toString()
            postCarDetail.policyDeviceType = ""
//                    deviceTypeList[selectedDeviceTypePosition].iD!!.toString()
            postCarDetail.policyMotorType =
                motorTypeList[selectedMotorTypePos].iD!!
//            postCarDetail.purposeID =
//                purposeList[selectPurposeTypePos].iD!!


            //Populate Policy Request objects
            if (postCarDetail.policyRequest == null) {
                val policyRequest = PolicyRequest()
                postCarDetail.policyRequest = policyRequest
            }

            postCarDetail.policyRequest!!.number = binding.edRegistration.et.text.toString().trim()
//            postCarDetail.policyRequest!!.number = binding.edPolicyNumber.et.text.toString().trim()
            postCarDetail.policyRequest!!.requestedDeviceType = ""
//                    deviceTypeList[selectedDeviceTypePosition].name!!
            postCarDetail.policyRequest!!.deliveryMethod = ""
//                    deliveryMethodList[selectedDeliverMethodPosition].name!!


            //Populate PolicyRequest->Customer  objects
            if (postCarDetail.policyRequest!!.policyCustomer == null) {
                val policyCustomer = PolicyCustomer()
                postCarDetail.policyRequest!!.policyCustomer = policyCustomer
            }

            postCarDetail.policyRequest!!.policyCustomer!!.name = customerX!!.name
            postCarDetail.policyRequest!!.policyCustomer!!.number = "" + customerX!!.iD
//            postCarDetail.policyRequest!!.policyCustomer!!.number =
//                binding.edPolicyNumber.et.text.toString().trim()


            //Populate PolicyRequest->Customer->Contact  objects
            if (postCarDetail.policyRequest!!.policyCustomer!!.policyContacts == null) {
                val policyContacts = PolicyContacts()
                postCarDetail.policyRequest!!.policyCustomer!!.policyContacts =
                    policyContacts
            }

            postCarDetail.policyRequest!!.policyCustomer!!.policyContacts!!.city =
                customerX!!.birthPlace
            postCarDetail.policyRequest!!.policyCustomer!!.policyContacts!!.name =
                customerX!!.name
            postCarDetail.policyRequest!!.policyCustomer!!.policyContacts!!.firstName =
                customerX!!.name!!.split(" ")[0]
            postCarDetail.policyRequest!!.policyCustomer!!.policyContacts!!.email =
                customerX!!.email
            postCarDetail.policyRequest!!.policyCustomer!!.policyContacts!!.mobilePhone =
                customerX!!.mobile


            //Populate PolicyRequest->Vehicle  objects
            if (postCarDetail.policyRequest!!.policyVehicle == null) {
                val policyVehicle = PolicyVehicle()
                postCarDetail.policyRequest!!.policyVehicle = policyVehicle
            }

//            postCarDetail.policyRequest!!.policyVehicle!!.vIN =
//                binding.edVin.et.text.toString().trim()
            postCarDetail.policyRequest!!.policyVehicle!!.vIN =
                binding.edRegistration.et.text.toString().trim() + rnds
            postCarDetail.policyRequest!!.policyVehicle!!.motorType =
                motorTypeList[selectedMotorTypePos].name!!


            postCarDetail.policyRequest!!.policyVehicle!!.model =
                subManufacturerModelll[SubManufacturerModelPos].name

            postCarDetail.policyRequest!!.policyVehicle!!.makeModelCode =
                subManufacturerModelll[SubManufacturerModelPos].iD!!.toString()


            //Populate PolicyRequest->RegisterPolicyRequest  objects
            if (postCarDetail.policyRequest!!.registerPolicyRequest == null) {
                val registerPolicyRequest = RegisterPolicyRequest()
                postCarDetail.policyRequest!!.registerPolicyRequest = registerPolicyRequest
            }

            postCarDetail.policyRequest!!.registerPolicyRequest!!.userName =
                customerX!!.userName
//            postCarDetail.colorID = getColorSelected(postCarDetail!!.colorID!!)
//            postCarDetail.policyRequest!!.registerPolicyRequest!!.reference =
//                binding.edPolicyNumber.et.text.toString().trim()
            postCarDetail.policyRequest!!.registerPolicyRequest!!.reference =
                binding.edRegistration.et.text.toString().trim()


//            if (viewModel.policyPassword != null) {
            if (viewModel.fromReg) {
                postCarDetail.policyRequest!!.registerPolicyRequest!!.password =
                    viewModel.policyPassword
                logD("**password", "Password: ${viewModel.policyPassword}")
                postCarDetail.flag = "I"
                binding.markDefault.isChecked = postCarDetail.isDefaultCar
            }

            if (viewModel.createNew) {
                postCarDetail.flag = "I"
                binding.markDefault.isChecked = postCarDetail.isDefaultCar
            } else if (!viewModel.createNew) {
                postCarDetail.flag = "U"
                binding.markDefault.isChecked = postCarDetail.isDefaultCar
                postCarDetail.renewalDate = ""
            }

//            }


//            viewModel.showPDialog()


            if (binding.edRegistration.et.text!!.matches(Regex(viewModel.Regex1))
                || binding.edRegistration.et.text!!.matches(Regex(viewModel.Regex2))
                || binding.edRegistration.et.text!!.matches(Regex(viewModel.Regex3))
                || binding.edRegistration.et.text!!.matches(Regex(viewModel.Regex4))
                || binding.edRegistration.et.text!!.matches(Regex(viewModel.Regex5))
            ) {

                showPDialog()
                viewModel.addCarDetails(
                    postCarDetail, false
                )
            } else {
                showConfirmationDialog(
                    requireContext(),
                    title = "Car Details Verification",
                    msg = "Do you have some special registration number?",
                    onPositiveClick = {
                        showPDialog()
                        viewModel.addCarDetails(
                            postCarDetail, false
                        )
                    }
                )
            }
        }
    }


    private fun validateRegistrationNumber(): Boolean {

        if (binding.edRegistration.et.text.toString().trim().isNotEmpty()) {
            binding.edRegistration.til.error = null
            return true
        } else {
            binding.edRegistration.til.error = "Please Enter Registration Number"
            return false
        }
    }

    /* private fun validateRegistrationYear(): Boolean {

         return if (binding.edRegistrationYear.et.text.toString().trim().isNotEmpty()) {
             if (binding.edRegistrationYear.et.text.toString().trim()
                     .matches("^\\d{4}\$".toRegex())
             ) {
                 binding.edRegistrationYear.til.error = null
                 true
             } else {
                 binding.edRegistrationYear.til.error = "Please Enter Registration Year"
                 false
             }

         } else {
             binding.edRegistrationYear.til.error = "Please Enter Registration Year"
             false
         }
     }*/

    private fun validateEngineNo(): Boolean {
        if (binding.edEngine.et.text.toString().trim().isNotEmpty()) {
            binding.edEngine.til.error = null
            return true
        } else {
            binding.edEngine.til.error = "Please Enter Engine Number"
            return false
        }
    }

    private fun validateChassisNo(): Boolean {
        if (binding.edChassis.et.text.toString().trim().isNotEmpty()) {
            binding.edChassis.til.error = null
            return true
        } else {
            binding.edChassis.til.error = "Please Enter Chassis Number"
            return false
        }
    }

    /*
    private fun validatePolicyNumber(): Boolean {
        if (binding.edPolicyNumber.et.text.toString().trim().isNotEmpty()) {
            binding.edPolicyNumber.til.error = null
            return true
        } else {
            binding.edPolicyNumber.til.error = "Please Enter Policy Number"
            return false
        }
    }
*/

    /*private fun validateVehicleInsuranceNumber(): Boolean {
        return if (binding.edVin.et.text.toString().trim().isNotEmpty()) {
            binding.edVin.til.error = null
            true
        } else {
            binding.edVin.til.error = "Please Enter Vehicle Insurance Number"
            false
        }
    }
*/


    private fun validateColor(): Boolean {
        return if (selectedColorPos != -1) {
            binding.spinnerColor.til.error = null
            true
        } else {
            binding.spinnerColor.til.error = "Please Select color"
            false
        }
    }

    private fun validateManufacturer(): Boolean {
        return if (selectedManufacturerPos != -1) {
            binding.spinnerManufacturer.til.error = null
            true
        } else {
            binding.spinnerManufacturer.til.error = "Please Select Manufacturer"
            false
        }
    }

    private fun validateMaker(): Boolean {
        return if (SubManufacturerModelPos != -1) {
            binding.spinnerMaker.til.error = null
            true
        } else {
            binding.spinnerMaker.til.error = "Please Select Vehicle Model"
            false
        }
    }

    private fun validateRegisterationYear(): Boolean {
        return if (RegistrationYearModelPos != -1) {
            binding.edRegistrationYear.til.error = null
            true
        } else {
            binding.edRegistrationYear.til.error = "Please Select Vehicle Model"
            false
        }
    }

    private fun validatePurPose(): Boolean {
        return if (selectPurposeTypePos != -1) {
            binding.spinnerPurpose.til.error = null
            true
        } else {
            binding.spinnerPurpose.til.error = "Please Select Purpose"
            false
        }
    }

    private fun validateMotorType(): Boolean {
        return if (selectedMotorTypePos != -1) {
            binding.spinnerMotorType.til.error = null
            true
        } else {
            binding.spinnerMotorType.til.error = "Please Select Motor Type"
            false
        }
    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.btnNextCarDetails -> {
                clickListeners()
            }
            R.id.drawerImage -> {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
        showPDialog()

    }

    override fun onSuccess(obj: RequestHandler) {

        if (obj.any is CustomerX) {
            if (viewModel.fromReg) {
                viewModel.fromReg = false
                val rr = obj.any as CustomerX

                binding.btnNextCarDetails.isEnabled = false
                binding.btnNextCarDetails.alpha = 0.6f
                binding.carMainCL.alpha = 0.8f


                /* showOnlyAlertMessage(
                     context = requireContext(),
                     title = "Car Details",
                     msg = "Details Added Successfully",
                     onPositiveClick = {*/

//                showPDialog()

//                customerX?.scopeToken == (obj.any as PostCarDetail).scopeToken

//                        viewModel.showPDialog()


                val loginVal = LoginMdl(
                    customerX!!.userName!!,
                    viewModel.prefsHelper.getRegPassword(),
                    stringIMEI!!
                )

                viewModel.prefsHelper.setScopeToken(rr!!.scopeToken!!)

                viewModel.loginUser(loginVal)
                viewModel.fromLogin =true






//                customerX!!.scopeToken = viewModel.prefsHelper.getScopeToken()
//                viewModel.prefsHelper.updateAuth(customerX!!.scopeToken!!)



//                    }
//                )

            }

            else if (viewModel.fromLogin){
                viewModel.fromLogin=false

                logD("**userName", customerX!!.userName)
                logD("**userName", "SCOPE TOKEN:  "+customerX!!.scopeToken)
                logD("**userName", viewModel.prefsHelper.getRegPassword())
                lifecycleScope.launch {
                    val response =
                        PortalApi.standardAuth(
                            customerX!!.userName,
                            viewModel.prefsHelper.getRegPassword()
                        )
                    if (response.isSuccessful) {
                        // call method here
//                            dismissDialog()
                        startActivity(
                            Intent(
                                context,
                                ActivityMain::class.java
                            ).putExtra("show_referance_dialog",true)
                        )
                        dismissDialog()
                        logD("*PortalResponse", "${response.result}")
                        logD("*PortalResponse", "${response.isSuccessful}")
                        finishAffinity(requireActivity())
                        binding.btnNextCarDetails.isEnabled = false
                        binding.btnNextCarDetails.alpha = 0.6f
                    } else {
                        val msg = response.errorMessage ?: ServiceError.getErrorText(
                            context,
                            response.errorCode
                        )
                        binding.btnNextCarDetails.isEnabled = true
                        binding.btnNextCarDetails.alpha = 0.0f
                        showShort(requireContext(), "Scope Error: $msg")
                    }
                }
            }
            else {
                if (viewModel.getCustomer().percentage == null) {
                    setAccountProgress(getProfilePercent(viewModel.getCustomer()))
                } else {
                    setAccountProgress(viewModel.getCustomer().percentage!!)
                }
//                            showShort(requireContext(), )
                showOnlyAlertMessage(
                    context = requireContext(),
                    title = "Car Details",
                    msg = "Details Added Successfully",
                    onPositiveClick = {
                        requireActivity().onBackPressed()
                    }
                )

            }

        }
        else if (obj.any is ResponseFillDropDown)
        {
            val rr = obj.any as ResponseFillDropDown
            if (!rr.manufacturers.isNullOrEmpty()) {
                manufactureList.clear()
                manufactureList.addAll(rr.manufacturers as ArrayList<Manufacturer>)
            }
            if (!rr.motorTypes.isNullOrEmpty()) {

                motorTypeList.clear()
                motorTypeList.addAll(rr.motorTypes as ArrayList<MotorType>)
            }
            if (!rr.model.isNullOrEmpty()) {

                subManufacturerModelList.clear()
                subManufacturerModelList.addAll(rr.model as ArrayList<SubManufacturerModel>)
            }
            if (!rr.colors.isNullOrEmpty()) {

                colorList.clear()
                colorList.addAll(rr.colors as ArrayList<Color>)
            }
            if (!rr.purpose.isNullOrEmpty()) {
                purposeList.clear()
                purposeList.addAll(rr.purpose as ArrayList<Purpose>)
            }
            /*     if (!rr.subManufacturerModel.isNullOrEmpty()) {
                     subManufacturerModelList.clear()
                     subManufacturerModelList.addAll(rr.subManufacturerModel as ArrayList<SubManufacturerModel>)
                 }*/
            if (manufactureList.isEmpty() || colorList.isEmpty() || /*makerList.isEmpty() ||*/ motorTypeList.isEmpty()/*|| purposeList.isEmpty()*/) {
                showPDialog()
                viewModel.getDropDown()
                showShort(requireContext(), "Please wait while getting details")
            } else {
                notifySpinners()
                viewModel.fetchFromDatabase()

            }
        }
    }

    override fun onError(obj: RequestHandler) {
    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_car_details
    }


    fun printDialogShow() {
        dialog = Dialog(requireContext())
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.getWindow()?.setGravity(Gravity.CENTER)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.search_filter_layout)

        val searchET: EditText = dialog.findViewById(R.id.searchET)
        val searchRV: RecyclerView = dialog.findViewById(R.id.searchRV)
        val cancelDialogBtn: ImageView = dialog.findViewById(R.id.cancelDialogBtn)

        cancelDialogBtn.setOnClickListener { dialog.dismiss() }


//        val spinnerAdapter: ArrayAdapter<*> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, manufactureList)
        searchAdapter = ManufacturingAdapter(
            manufactureList,
            this,
            context
        )





        linearLayoutManager = LinearLayoutManager(context)
        searchRV.layoutManager = linearLayoutManager
        searchRV.adapter = searchAdapter



        searchET.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                val searchText: String = searchET.getText().toString()

                manufacturingFilter(searchText)
                // TODO Auto-generated method stub
            }
        })


        dialog.show()
    }


    fun makerDialog(subManufacturerModelList: List<SubManufacturerModel>) {
        dialog = Dialog(requireContext())
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.getWindow()?.setGravity(Gravity.CENTER)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.search_filter_layout)
        val cancelDialogBtn: ImageView = dialog.findViewById(R.id.cancelDialogBtn)
        val noResultFoundTV: TextView = dialog.findViewById(R.id.noResultFoundTV)

        cancelDialogBtn.setOnClickListener { dialog.dismiss() }
        val searchET: EditText = dialog.findViewById(R.id.searchET)
        searchRVV = dialog.findViewById(R.id.searchRV)


//        val spinnerAdapter: ArrayAdapter<*> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, manufactureList)
        /*vehicleAdapter = VehicleAdapter(
            subManufacturerModelList,
            this,
            context
        )*/



        linearLayoutManager = LinearLayoutManager(context)
        searchRVV.layoutManager = linearLayoutManager
        searchRVV.adapter = vehicleAdapter



        searchET.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                val searchText: String = searchET.getText().toString()

                makerFilter(searchText)
                // TODO Auto-generated method stub
            }
        })



        dialog.show()

    }


    fun purposeDialog() {
        dialog = Dialog(requireContext())
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.getWindow()?.setGravity(Gravity.CENTER)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.search_filter_layout)
        val cancelDialogBtn: ImageView = dialog.findViewById(R.id.cancelDialogBtn)

        cancelDialogBtn.setOnClickListener { dialog.dismiss() }

        val searchET: EditText = dialog.findViewById(R.id.searchET)
        val searchRV: RecyclerView = dialog.findViewById(R.id.searchRV)


//        val spinnerAdapter: ArrayAdapter<*> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, manufactureList)
        purposeAdapter = PurposeAdapter(
            purposeList,
            this,
            context
        )



        linearLayoutManager = LinearLayoutManager(context)
        searchRV.layoutManager = linearLayoutManager
        searchRV.adapter = purposeAdapter



        searchET.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                val searchText: String = searchET.getText().toString()

                purposeFilter(searchText)
                // TODO Auto-generated method stub
            }
        })
        dialog.show()

    }


    // color Picker Dialog

    fun colorDialog() {
        dialog = Dialog(requireContext())
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.getWindow()?.setGravity(Gravity.CENTER)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.search_filter_layout)
        val cancelDialogBtn: ImageView = dialog.findViewById(R.id.cancelDialogBtn)

        cancelDialogBtn.setOnClickListener { dialog.dismiss() }

        val searchET: EditText = dialog.findViewById(R.id.searchET)
        val searchRV: RecyclerView = dialog.findViewById(R.id.searchRV)


//        val spinnerAdapter: ArrayAdapter<*> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, manufactureList)
        colorAdapter = ColorAdapter(
            colorList,
            this,
            context
        )



        linearLayoutManager = LinearLayoutManager(context)
        searchRV.layoutManager = linearLayoutManager
        searchRV.adapter = colorAdapter



        searchET.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                val color: String = searchET.getText().toString()

                colorFilter(color)
                // TODO Auto-generated method stub
            }
        })



        dialog.show()

    }
    // year Picker Dialog

    fun yearDialog() {
        dialog = Dialog(requireContext())
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.getWindow()?.setGravity(Gravity.CENTER)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.search_filter_layout)
        val cancelDialogBtn: ImageView = dialog.findViewById(R.id.cancelDialogBtn)

        cancelDialogBtn.setOnClickListener { dialog.dismiss() }

        val searchET: EditText = dialog.findViewById(R.id.searchET)
        val searchRV: RecyclerView = dialog.findViewById(R.id.searchRV)


//        val spinnerAdapter: ArrayAdapter<*> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, manufactureList)
        yearAdapter = YearAdapter(
            registrationYearModelList,
            this,
            context
        )



        linearLayoutManager = LinearLayoutManager(context)
        searchRV.layoutManager = linearLayoutManager
        searchRV.adapter = yearAdapter



        searchET.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                val year: String = searchET.getText().toString()

                yearFilter(year)
                // TODO Auto-generated method stub
            }
        })



        dialog.show()

    }


    // motor Type Dialog

    fun motorTypeDialog() {
        dialog = Dialog(requireContext())
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.getWindow()?.setGravity(Gravity.CENTER)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.search_filter_layout)
        val cancelDialogBtn: ImageView = dialog.findViewById(R.id.cancelDialogBtn)

        cancelDialogBtn.setOnClickListener { dialog.dismiss() }

        val searchET: EditText = dialog.findViewById(R.id.searchET)
        val searchRV: RecyclerView = dialog.findViewById(R.id.searchRV)


//        val spinnerAdapter: ArrayAdapter<*> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, manufactureList)
        motorTypeAdapter = MotorTypeAdapter(
            motorTypeList,
            this,
            requireContext()
        )



        linearLayoutManager = LinearLayoutManager(context)
        searchRV.layoutManager = linearLayoutManager
        searchRV.adapter = motorTypeAdapter



        searchET.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                val motorType: String = searchET.getText().toString()

                motorTypeFilterCalling(motorType)
                // TODO Auto-generated method stub
            }
        })



        dialog.show()

    }


    private fun motorTypeFilterCalling(motorType: String) {
        logD("**motorT", "" + motorType)
        logD("**searchingWord", "" + motorTypeList)

        val productByModel: MutableList<MotorType> = java.util.ArrayList<MotorType>()
        for (motorTypeModel in motorTypeList) {
            if (motorTypeModel.name.toString().toLowerCase()
                    .contains("" + motorType.toString().toLowerCase())
            ) {
                productByModel.add(motorTypeModel)
            } else if (motorType.toString() == "") {
                productByModel.add(motorTypeModel)
            }
        }

        motorTypeAdapter.setProductList(productByModel)
        if (productByModel.size == 0) {
            showShort(requireContext(), "No result found")
        }
    }

    private fun colorFilter(color: String) {
        logD("**searchingWord", "" + color)
        logD("**searchingWord", "" + colorList)

        val productByModel: MutableList<Color> = java.util.ArrayList<Color>()
        for (colorModel in colorList) {
            if (colorModel.name.toString().toLowerCase()
                    .contains("" + color.toString().toLowerCase())
            ) {
                productByModel.add(colorModel)
            } else if (color.toString() == "") {
                productByModel.add(colorModel)
            }
        }

        colorAdapter.setProductList(productByModel)
        if (productByModel.size == 0) {
            showShort(requireContext(), "No result found")
        }
    }

    private fun yearFilter(year: String) {

        val productByModel: MutableList<String> = java.util.ArrayList<String>()
        for (yearModel in registrationYearModelList) {
            if (yearModel.lowercase()
                    .contains("" + year.lowercase())
            ) {
                productByModel.add(yearModel)
            } else if (year.toString() == "") {
                productByModel.add(yearModel)
            }
        }

        yearAdapter.setProductList(productByModel)
        if (productByModel.size == 0) {
            showShort(requireContext(), "No result found")
        }
    }

    private fun makerFilter(searchText: String) {
        logD("**searchingWord", "" + searchText)
        logD("**searchingWord", "" + subManufacturerModelList)

        val subManufacturerModel: MutableList<SubManufacturerModel> =
            java.util.ArrayList<SubManufacturerModel>()
        for (makersModel in subManufacturerModelList) {
            if (makersModel.name.toString().toLowerCase()
                    .contains("" + searchText.toString().toLowerCase())
            ) {
                subManufacturerModel.add(makersModel)
            } else if (searchText.toString() == "") {
                subManufacturerModel.add(makersModel)
            }
        }


        vehicleAdapter.setProductList(subManufacturerModel)
        if (subManufacturerModel.size == 0) {
            showShort(requireContext(), "No result found")
        }
    }

    private fun purposeFilter(searchText: String) {
        logD("**searchingWord", "" + searchText)

        val productByModel: MutableList<Purpose> = java.util.ArrayList<Purpose>()
        for (purposeModel in purposeList) {
            if (purposeModel.name.toString().toLowerCase()
                    .contains("" + searchText.toString().toLowerCase())
            ) {
                productByModel.add(purposeModel)
            } else if (searchText.toString() == "") {
                productByModel.add(purposeModel)
            }
        }

        purposeAdapter.setProductList(productByModel)
        if (productByModel.size == 0) {
            showShort(requireContext(), "No result found")
        }
    }


    fun showFilter() {
        logD("**manfacList", "size:: " + manufactureList.size)
        if (manufactureList.size > 0) {
            printDialogShow()
        } else {
            showShort(requireContext(), "No Manufacturer founds")
        }
    }

    fun showMakerFilter(subManufacturerModelList: List<SubManufacturerModel>) {
        logD("**manfacList", "size:: " + subManufacturerModelList.size)

        if (subManufacturerModelList.size > 0) {

            makerDialog(subManufacturerModelList)
        } else {
//            showShort(requireContext(), "No Makers found")
        }


    }

    fun showPurposeFilter() {
        logD("**manfacList", "size:: " + manufactureList.size)

        if (purposeList.size > 0) {
            purposeDialog()
        } else {
            showShort(requireContext(), "No Makers found")
        }


    }

    fun colorMakerFilter() {
        logD("**manfacList", "size:: " + colorList.size)

        if (colorList.size > 0) {
            colorDialog()
        } else {
            showShort(requireContext(), "No Makers found")
        }
    }

    fun yearMakerFilter() {

        if (registrationYearModelList.size > 0) {
            yearDialog()
        } else {
            showShort(requireContext(), "No Year found")
        }
    }


    fun motorTypeFilter() {
        logD("**motorTypeList", "size:: " + motorTypeList.size)

        if (motorTypeList.size > 0) {
            motorTypeDialog()
        } else {
            showShort(requireContext(), "No Motor found")
        }
    }

    fun manufacturingFilter(model: String) {
        logD("**searchingWord", "" + model)
        logD("**searchingWord", "" + manufactureList)

        val productByModel: MutableList<Manufacturer> = java.util.ArrayList<Manufacturer>()
        for (manufactruingModel in manufactureList) {
            logD("**searchingWord", "full list::::   " + manufactruingModel)
            if (manufactruingModel.name.toString().toLowerCase()
                    .contains("" + model.toString().toLowerCase())
            ) {
                logD("**searchingWord", "list" + manufactruingModel.name?.toLowerCase())
                logD("**searchingWord", "search::" + model.toLowerCase())
                productByModel.add(manufactruingModel)
            } else if (model.toString() == "") {
                productByModel.add(manufactruingModel)
            }
        }
        searchAdapter.setProductList(productByModel)
        if (productByModel.size == 0) {
//            showShort(requireContext(), "No data to show")
        }
    }


    override fun manufacturer(position: Int, manufacturerName: String?, manufacturerID: Long) {
        binding.spinnerManufacturer.dropDown.setText(manufacturerName)
        searchingManString == manufacturerName
        manufacturerid == manufacturerID

/*
        val subManufacturerModel: MutableList<SubManufacturerModel> =
            java.util.ArrayList<SubManufacturerModel>()
        for (manufacIDD in subManufacturerModelList) {
            logD("**manfacIDD", "$manufacturerID      sub list:   ${manufacIDD.ManufacturerID}")
            if (manufacIDD.ManufacturerID == manufacturerID) {
                logD(
                    "**manfacIDD",
                    "$manufacIDD.ManufacturerID      searchingID:   $manufacturerID"
                )
                subManufacturerModel.add(manufacIDD)
            }
        }

        vehicleAdapter.setProductList(subManufacturerModel)
        logD("**sortList","$")



*/

        subManufacturerModelll.clear()
        for (makersModel in subManufacturerModelList) {
            if (makersModel.ManufacturerID == manufacturerID) {
                subManufacturerModelll.add(makersModel)
            }
        }
        vehicleAdapter = VehicleAdapter(
            subManufacturerModelll,
            this,
            context
        )
        searchRVV = dialog.findViewById(R.id.searchRV)

//        showMakerFilter(subManufacturerModelll)
//        subManufacturerModelll == subManufacturerModelList
        vehicleAdapter.setProductList(subManufacturerModelll)
        linearLayoutManager = LinearLayoutManager(context)
        searchRVV.layoutManager = linearLayoutManager
        searchRVV.adapter = vehicleAdapter
//        searchRVV = dialog.findViewById(R.id.searchRV)

//        linearLayoutManager = LinearLayoutManager(context)
//        searchRVV.layoutManager = linearLayoutManager
//        searchRVV.adapter = vehicleAdapter
//        vehicleAdapter.notifyDataSetChanged()


        if (subManufacturerModelll.size == 0) {
            showShort(requireContext(), "No result found")
        }

        selectedManufacturerPos = position
        if (dialog.isShowing)
            dialog.dismiss()
        binding.spinnerMaker.dropDown.setText("")
        makerString == ""
        SubManufacturerModelPos = -1
    }

    override fun vehicle(position: Int, vehicleName: String?) {
        binding.spinnerMaker.dropDown.setText(vehicleName)
        makerString == vehicleName
        SubManufacturerModelPos = position
        if (dialog.isShowing)
            dialog.dismiss()
    }

    override fun colorPicker(position: Int, colorName: String?) {
        binding.spinnerColor.dropDown.setText(colorName)
        colorString = colorName!!
        selectedColorPos = position
        if (dialog.isShowing)
            dialog.dismiss()
    }

    override fun motorTypePicker(position: Int, typeName: String?) {
        binding.spinnerMotorType.dropDown.setText(typeName)
        motorTypeString = typeName!!
        selectedMotorTypePos = position
        if (dialog.isShowing)
            dialog.dismiss()
    }

    override fun purpose(position: Int, purposeName: String?) {
        binding.spinnerPurpose.dropDown.setText(purposeName)
        purposeTypeString == purposeName
        selectPurposeTypePos = position
        if (dialog.isShowing)
            dialog.dismiss()
    }

    override fun yearPicker(position: Int, yearName: String?) {

        binding.edRegistrationYear.dropDown.setText(yearName)
        yearString = yearName!!
        RegistrationYearModelPos = position
        if (dialog.isShowing)
            dialog.dismiss()
    }


}