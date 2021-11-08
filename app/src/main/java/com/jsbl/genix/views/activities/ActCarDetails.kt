package com.jsbl.genix.views.activities

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivityCardetailsActivitiyBinding
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.callBacks.OnViewClickListener
import com.jsbl.genix.viewModel.MainHomeViewModel
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_CUSTOMER
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_POLICY_PASSWORD
import com.jsbl.genix.views.dialogs.ProgressDialog

class ActCarDetails : AppCompatActivity(), OnViewClickListener {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityCardetailsActivitiyBinding
    private lateinit var viewModel: MainHomeViewModel
    lateinit var dialogP: ProgressDialog
    private var onDash = true
    private var customerX: CustomerX? = null
    var policyPassword: String? = null

    companion object {
        const val INTENT_FROM_REG = "fromReg"
        const val INTENT_TRIP_FILTER = "tripFilter"
        const val INTENT_FROM_CREATE_NEW = "createNew"
        const val INTENT_FROM_SELECTED_POSITION = "selectedPosition"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardetailsActivitiyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.onClickListener = this
        binding.title = "Title"
        customerX = intent.getParcelableExtra(ActivityRegistration.INTENT_CUSTOMER)
        policyPassword = intent.getStringExtra(ActivityRegistration.INTENT_POLICY_PASSWORD)

        viewModel = ViewModelProvider(this).get(MainHomeViewModel::class.java)
        //TODO
        navController =
            Navigation.findNavController(this, R.id.fragmentNavHos)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destination.id)
            }
            binding.title = try {
                destination.label as String?
            } catch (e: Resources.NotFoundException) {
                "Title"
            }

//            showShort(this@MainActivity, "Navigated to $dest")
        }
        val bundle = Bundle()
        bundle.putParcelable(INTENT_CUSTOMER, customerX)
        bundle.putString(INTENT_POLICY_PASSWORD, policyPassword)
        bundle.putBoolean(INTENT_FROM_REG, true)
//        navController.setGraph(R.navigation.<you_nav_graph_xml>, bundle)
        navController.navigate(R.id.carDetails, bundle)

    }

    override fun onBackPressed() {
        finish()

    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.drawerButton -> {
                onBackPressed()
            }
        }

    }

}