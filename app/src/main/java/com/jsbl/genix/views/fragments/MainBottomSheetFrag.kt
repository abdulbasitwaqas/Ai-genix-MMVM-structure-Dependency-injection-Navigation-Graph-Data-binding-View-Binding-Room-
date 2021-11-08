package com.jsbl.genix.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkResponse
import com.android.volley.VolleyError
import com.jsbl.genix.Presenter.Presenter
import com.jsbl.genix.R
import com.jsbl.genix.db.dao.CustomerDao
import com.jsbl.genix.interfaces.IPresenter
import com.jsbl.genix.interfaces.RequestViews
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.trips.TripItem
import com.jsbl.genix.url.APIsURL
import com.jsbl.genix.utils.SharePreferencesHelper
import com.jsbl.genix.views.adapters.TripsListAdapterHorizontal

class MainBottomSheetFrag : AppCompatActivity(), IPresenter, RequestViews {

    var context: Context? = null
    var tripList: RecyclerView? = null
    val allTripsDetail: TripsListAdapterHorizontal? = null
    val allTripsDetailList: List<TripItem>? = null
    var see_more: TextView? = null
    private  var bottom_caption:TextView? = null
    var minimizeImage: ImageView? = null
    val customerX: CustomerX? = null
    val prefsHelper: SharePreferencesHelper? = null
    val customer: String? = null
    val customerDao: CustomerDao? = null

    fun BottomSheetTripsListFrag(context: Context?) {
        this.context = context
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.alt_fragment_bottom_sheet_list, container, false)
        tripList = view.findViewById(R.id.tripList)
        see_more = view.findViewById(R.id.see_more)
        minimizeImage = view.findViewById(R.id.minimizeImage)
        bottom_caption = view.findViewById<TextView>(R.id.bottom_caption)
        tripsRequest()
        return view
    }

    open fun tripsRequest() {
        val presenter = Presenter(this, context, this)
        presenter.setGetMethod(APIsURL.DEV_TRIPS, "loginRequest")
    }


    override fun getResponse(response: String?, requestMessage: String?) {}

    override fun getError(error: VolleyError?) {}

    override fun getSuccessNetwork(response: NetworkResponse?, requestMessage: String?) {}

    override fun showProgress() {}

    override fun hideProgress() {}
}