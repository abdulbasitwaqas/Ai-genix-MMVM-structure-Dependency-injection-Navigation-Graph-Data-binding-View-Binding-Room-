package com.jsbl.genix.views.activities

import android.annotation.SuppressLint
import android.app.backup.SharedPreferencesBackupHelper
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivitySubAreaOfInterestBinding
import com.jsbl.genix.model.profileManagement.GetCustomerInterestModel
import com.jsbl.genix.model.profileManagement.Interest
import com.jsbl.genix.model.profileManagement.SubInterestIdsModel
import com.jsbl.genix.model.profileManagement.SubInterestList
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.callBacks.OnViewClickListener
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.utils.extensions.toast
import com.jsbl.genix.viewModel.AreaOfInterestViewModel
import com.jsbl.genix.views.adapters.SubAOIAdapter
import com.scope.smartdrivedemo.AbsActivity
import okhttp3.internal.notify

class SubAreaOfInterestActivity :
    SubAOIAdapter.onClickOnItem, BaseActivity<AreaOfInterestViewModel, ActivitySubAreaOfInterestBinding>(AreaOfInterestViewModel::class.java) {
    /* lateinit var categoryId :String
     lateinit var catName :String
     lateinit var catPosition :String*/
    var userID :Long?=1
    var idd: String=""
    private lateinit var customerX: CustomerX
    private lateinit var subAOIAdapter: SubAOIAdapter
    val arraylist = ArrayList<Interest>()
    var subInterest =  ArrayList<String>()
    var interestModel:Interest? = null

    private lateinit var subInterestList: ArrayList<SubInterestList>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_area_of_interest)
        observeDetails()
        viewModel.fetchFromDatabase()




        /*categoryId = intent.getStringExtra("id").toString()
        catPosition = intent.getStringExtra("position").toString()
        catName = intent.getStringExtra("interestName").toString()*/

        val bundle = intent.extras
        subInterestList = bundle!!.getParcelableArrayList<SubInterestList>("subInterestList") as ArrayList<SubInterestList>
        idd = bundle.getString("id").toString()
        logD("**subInterestID",""+idd)

        val customerInterestModel =  GetCustomerInterestModel(idd , SharePreferencesHelper.invoke(this).getCustomerId())
        viewModel.getInterests(customerInterestModel)

        /*   val subInterestList = JSONArray(
               intent.getStringExtra("subInterestList")

           )*/


//        val bundle = intent.extras
//        val subInterestList: ArrayList<SubInterestList>? = intent.getSerializableExtra("subInterestList") as ArrayList<SubInterestList>?
//        binding.actionBarCustom.title.text = catName
//                interestXList.clear()
//                interestXList.addAll(rr.interests as ArrayList<InterestX>)

        subAOIAdapter = SubAOIAdapter(subInterestList, this, this)
        val gridLayoutManager = GridLayoutManager(this, 2)
        binding.subInterestRV.setLayoutManager(gridLayoutManager)
        binding.onClickListener = this
        binding.subInterestRV.adapter = subAOIAdapter
        
        if (subInterestList.size>0){
            binding.btnInterestSubmit.setBackgroundResource(R.drawable.bg_login_next)
            binding.btnInterestSubmit.isEnabled =true
        } else {
            binding.btnInterestSubmit.setBackgroundResource(R.drawable.invisible_btn)
            binding.btnInterestSubmit.isEnabled =false

        }

        binding.btnInterestSubmit.setOnClickListener {
//            showShort(this,"Click")
            logD("**subInterestIDSize",""+subInterest.size)
            if (subInterest.size>0){
            if (interestModel!=null){
                showPDialog()
                viewModel.addInterests(interestModel!!)
                viewModel.fromSubInterestAdapter=   true
            }else{
                toast("Select your interest please")
            }
            } else {
                showShort(this, "Please select one interest")
            }


        }

    }


    fun observeDetails() {
        viewModel.customer.observe(this, androidx.lifecycle.Observer {
            it.let {
                customerX = it

                logD("******userIDDD",""+customerX.iD)
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
        binding.actionBarCustom.pBar.setProgress(percentage)
        /* val params =
             binding.accountProgressLayout.ivDropdown.getLayoutParams() as ConstraintLayout.LayoutParams
         params.horizontalBias =
             percentage.toFloat() / 100f // here is one modification for example. modify anything else you want :)
         binding.accountProgressLayout.ivDropdown.setLayoutParams(params)
         binding.accountProgressLayout.tvProgressDigits.text = "$percentage%"*/
    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.drawerImage -> {
                onBackPressed()
            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
    }

    override fun onSuccess(obj: RequestHandler) {
//        dismissDialog()
        if (viewModel.fromSubInterestAdapter){

            showOnlyAlertMessage(
                context = this,
                title = "Interests",
                msg = "Your interests added successfully",
                onPositiveClick = {
                    this.onBackPressed()
                }
            )
            if (viewModel.getCustomer().percentage == null) {
                setAccountProgress(getProfilePercent(viewModel.getCustomer()))
            } else {
                setAccountProgress(viewModel.getCustomer().percentage!!)
            }
            viewModel.fromSubInterestAdapter = false
        }

        if (viewModel.fromSubInterestIds == true){
            viewModel.fromSubInterestIds = false
            if (obj.any is SubInterestIdsModel){
                val subInterestIdsModel = obj.any as SubInterestIdsModel

                val ids = subInterestIdsModel.subInterest
                val lstValues: List<String>? = ids?.split(",")?.map { it -> it.trim() }
                lstValues?.forEach { ids ->
                    subInterestList?.forEach {
                        if (ids.equals(it.iD.toString())){
                            it.isSelected = true
                        }
                    }
                }
                subAOIAdapter.notifyDataSetChanged()
            }
        }




    }

    override fun onError(obj: RequestHandler) {
    }

    override fun getLayoutRes() =
        R.layout.activity_sub_area_of_interest

    override fun initViewModel(viewModel: AreaOfInterestViewModel) {
    }

    @SuppressLint("Range")
    override fun subInterestList(SubInterest: ArrayList<String>, InterestID : String) {
        logD("**userID","user id:   "+viewModel.prefsHelper.getCustomerId())
//        if (SubInterest.size>0) {
            interestModel = Interest(
                viewModel.prefsHelper.getCustomerId(),
                "" + InterestID,
                SubInterest.joinToString { it })
            arraylist.add(interestModel!!)
            subInterest = SubInterest
            binding.btnInterestSubmit.isEnabled = true
//        }
    /*else
        {
            binding.btnInterestSubmit.isEnabled = false
        }*/

    }
}