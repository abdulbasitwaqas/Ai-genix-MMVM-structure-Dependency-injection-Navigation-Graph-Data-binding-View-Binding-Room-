package com.jsbl.genix.views.fragments

import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.jsbl.genix.databinding.AltFragmentMyTripDetailsBinding
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.trips.GetTripsFeedbackResponse
import com.jsbl.genix.trips.TripsEventModel
import com.jsbl.genix.utils.RequestHandler
import com.jsbl.genix.utils.SharePreferencesHelper
import com.jsbl.genix.utils.callBacks.MapCallbacks
import com.jsbl.genix.utils.getProfilePercent
import com.jsbl.genix.utils.logD
import com.jsbl.genix.viewModel.MainHomeViewModel
import com.jsbl.genix.views.fragments.MapFragment.Companion.FLAG_DROP_OFF
import com.jsbl.genix.views.fragments.MapFragment.Companion.FLAG_PICKUP
import java.lang.reflect.Array
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.graphics.drawable.VectorDrawable

import android.graphics.BitmapFactory

import android.graphics.drawable.BitmapDrawable

import androidx.core.content.ContextCompat

import android.graphics.drawable.Drawable

import android.graphics.Bitmap

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import java.lang.IllegalArgumentException
import android.R

import android.provider.MediaStore.Images.Media.getBitmap





/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class TripDetailsFragment : BaseFragment<MainHomeViewModel, AltFragmentMyTripDetailsBinding>(
    MainHomeViewModel::class.java
), MapCallbacks, OnMapReadyCallback, GoogleMap.OnPolylineClickListener,
    GoogleMap.OnPolygonClickListener {

    private var mGoogleMap: GoogleMap? = null
    private var pointsList: ArrayList<Double>? = ArrayList()
    private var customerX = CustomerX()

    private lateinit var dummyMapFragment: MapFragment
    var pickUpLL = LatLng(
        33.598961, 73.154420
    )
    var dropOffLL = LatLng(
        33.6664943, 73.0556458
    )

    /* val latlngLists: List<LatLng>
         get() {
             TODO()
         }*/
    var tripEventList: ArrayList<Double>? = ArrayList()
    val latlnglist: MutableList<LatLng> = java.util.ArrayList<LatLng>()
    var tripsEventModelList: ArrayList<TripsEventModel> = java.util.ArrayList()


    /*  var startingLat:Double = 0.0
      var startingLng:Double = 0.0
      var endingLat:Double = 0.0
      var endingLng:Double = 0.0*/

    var startTime = ""
    var endTime = ""
    var status = ""
    var acceleration = ""
    var braking = ""
    var speeding = ""
    var time = ""

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)


        /* val position: String? = arguments?.getString("position")

         val startingLats: Double = arguments?.getDouble("startingLat")!!
         val startingLngs: Double = arguments?.getDouble("startingLat")!!
         val endingLats: Double = arguments?.getDouble("endingLat")!!
         val endingLngs: Double = arguments?.getDouble("endingLng")!!


         val duration: String? = arguments?.getString("duration")
         tripEventList= arguments?.getParcelableArrayList("tripEventList")

         pickUpLL = LatLng(
             startingLats, startingLngs
         )
         dropOffLL = LatLng(
             endingLats, endingLngs
         )*/

        /*    binding.speedConstraint.value.text = "--"
            binding.corneringConstraint.value.text = "--"
            binding.brakingConstraint.value.text = "--"
            binding.accelerationConstraint.value.text = "--"
            binding.timeOfDayConstraint.value.text = "--"
            binding.totalDistanceConstraint.value.text = "--"*/




        arguments?.let {
            TripDetailsFragmentArgs.fromBundle(it).itemTrip
            startTime = TripDetailsFragmentArgs.fromBundle(it).itemTrip.startUtcTimestamp
            endTime = TripDetailsFragmentArgs.fromBundle(it).itemTrip.endUtcTimestamp

            /* startingLat = TripDetailsFragmentArgs.fromBundle(it).itemTrip.startLatitude
             startingLng = TripDetailsFragmentArgs.fromBundle(it).itemTrip.startLongitude
             endingLat = TripDetailsFragmentArgs.fromBundle(it).itemTrip.endLatitude
             endingLng = TripDetailsFragmentArgs.fromBundle(it).itemTrip.endLongitude*/



            tripsEventModelList = TripDetailsFragmentArgs.fromBundle(it).itemTrip.TripEvent as ArrayList<TripsEventModel>

            for (i in 0 until TripDetailsFragmentArgs.fromBundle(it).itemTrip.TripEvent.size) {
                pointsList = TripDetailsFragmentArgs.fromBundle(it).itemTrip.TripEvent[i].point
                status = TripDetailsFragmentArgs.fromBundle(it).itemTrip.TripEvent[i].EventDescription!!

                logD("**cornering",""+status)

                time = TripDetailsFragmentArgs.fromBundle(it).itemTrip.TripEvent[i].time.toString()
                for (j in 0 until pointsList!!.size) {
                    tripEventList!!.add(pointsList!![j])
                    logD("**latlngList", "     Latlng list: ${tripEventList} ")

                }

            }
            for (j in 0 until tripEventList!!.size step 2) {
                latlnglist.add(LatLng(tripEventList!!.get(j + 1), tripEventList!!.get(j)))
            }





//            latlngLists == latlngList


        }


/*
       pickUpLL = LatLng(
            startingLat, startingLng
        )
        dropOffLL = LatLng(
            endingLat, endingLng
        )*/







        observeDetails()
        addMap()
        binding.onClickListener = this
        viewModel.fetchFromDatabase()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
//        binding.actionBarCustom.pBar.setProgress(percentage)

    }


    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner,
            Observer<CustomerX> { t ->
                t?.let {
                    customerX = it
//                    viewModel.getAllTrips(it)

                    showPDialog()
                    if (customerX != null) {
                        if (startTime != null && endTime != null) {
                            logD("**time", "start time: $startTime   end time: $endTime")
                            viewModel.getFeedbackSpecificTrip(
                                "" + customerX.scopeToken,
                                "" + customerX.carDetails!![SharePreferencesHelper.invoke(
                                    requireContext()
                                ).getDefaultCarPos()].policyNumber,
                                "" + customerX!!.iD,
                                startTime, endTime
                            )
                        }
                        setAccountProgress(getProfilePercent(it))
                    }
                }
            })
    }



    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            com.jsbl.genix.R.id.backBtnTDF -> {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
//        showPDialog()
    }

    override fun onSuccess(obj: RequestHandler) {
        dismissDialog()
        val rr = obj.any as GetTripsFeedbackResponse

        logD("**statsRes", "" + rr!!)

        binding.speedingTV.text =rr.items!![7].value.toString()
        binding.corneringTV.text =rr.items!![9].value.toString()
        binding.distanceTV.text =rr.items!![2].value.toString()
        binding.brakingTV.text =rr.items!![6].value.toString()
        binding.acceletationTV.text =rr.items!![5].value.toString()
        binding.timeOfDayTV.text =rr.items!![4].value.toString()

    }

    override fun onError(obj: RequestHandler) {
        dismissDialog()
    }


    fun addMap() {

        dummyMapFragment = MapFragment()
        dummyMapFragment.mapCallbacks = this
        dummyMapFragment.isEnableLocationMarker = false
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(com.jsbl.genix.R.id.frame_map, dummyMapFragment, "MapFragment")
        fragmentTransaction.commit()

        //Map Changes


//        taxiFragmentManager.addFragment(dummyMapFragment, R.id.frame_map, FragmentTags.TAG_MAP, false);
    }

    override fun onCameraMove() {
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        return false
    }

    override fun onLocationSuccess(location: Location?) {
    }

    override fun onMapClick(latLng: LatLng?) {
    }

    override fun onCameraIdle() {
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        dropOffLL = latlnglist[0]
        pickUpLL = latlnglist[latlnglist.size - 1]
        mGoogleMap = googleMap
//        dummyMapFragment.setLocationMarkersExplicitly(pickUpLL, FLAG_PICKUP)
//        dummyMapFragment.setLocationMarkersExplicitly(dropOffLL, FLAG_DROP_OFF)
//        dummyMapFragment.drawRoute(pickUpLL, dropOffLL)
        dummyMapFragment.mapConfigShowCase()
        dummyMapFragment.zoomOnMarkers(pickUpLL, dropOffLL, 300)
        logD("**latlnglist", "" + latlnglist.size)
        logD("**latlnglist", "status: " +status)

        for (j in 0 until tripsEventModelList!!.size) {

            val formatter =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
                } else {
                    SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
                }
//            formatter.setTimeZone(TimeZone.getTimeZone("UTC"))

            if (tripsEventModelList.get(j).EventDescription!!.contains("Corner")){
                corneringMarker(LatLng(tripsEventModelList.get(j).point!!.get(1),tripsEventModelList.get(j).point!!.get(0)),tripsEventModelList.get(j).EventDescription +" "+formatter.format(tripsEventModelList.get(j).time))
            }

            else if (tripsEventModelList.get(j).EventDescription!!.contains("Brak")){
                brakingMarker(LatLng(tripsEventModelList.get(j).point!!.get(1),tripsEventModelList.get(j).point!!.get(0)),tripsEventModelList.get(j).EventDescription +" "+formatter.format(tripsEventModelList.get(j).time))
            }

            else if (tripsEventModelList.get(j).EventDescription!!.contains("Acceleration")){
                accelerationMarker(LatLng(tripsEventModelList.get(j).point!!.get(1),tripsEventModelList.get(j).point!!.get(0)),tripsEventModelList.get(j).EventDescription +" "+formatter.format(tripsEventModelList.get(j).time))
            }

            else if (tripsEventModelList.get(j).EventDescription!!.contains("Speed")){
                speedingMarker(LatLng(tripsEventModelList.get(j).point!!.get(1),tripsEventModelList.get(j).point!!.get(0)),tripsEventModelList.get(j).EventDescription +" "+formatter.format(tripsEventModelList.get(j).time))
            }

            startMarker(dropOffLL,tripsEventModelList.get(0).EventDescription +" "+formatter.format(tripsEventModelList.get(0).time))
            endMarker(pickUpLL,tripsEventModelList.get(latlnglist.size - 1).EventDescription +" "+formatter.format(tripsEventModelList.get(latlnglist.size - 1).time))


        }

        val polyline1 = googleMap?.addPolyline(
            PolylineOptions()
                .clickable(true)
                .addAll(
                    latlnglist
                )
                .width(0.6f).color(Color.BLACK).geodesic(true)
        )
        googleMap?.setOnPolylineClickListener(this)
        googleMap?.setOnPolygonClickListener(this)
        if (polyline1 != null) {
            logD("**polyLine", "$polyline1")
            stylePolyline(polyline1)
        }


    }

    private fun stylePolyline(polyline: Polyline) {
        // Get the data object stored with the polyline.
        val type = polyline.tag?.toString() ?: ""
        when (type) {
            "Start" -> {
                // Use a custom bitmap as the cap at the start of the line.
                polyline.startCap = CustomCap(
                    BitmapDescriptorFactory.fromResource(com.jsbl.genix.R.drawable.start), 10f
                )
            }
            "End" -> {
                // Use a round cap at the start of the line.
                polyline.startCap = CustomCap(
                    BitmapDescriptorFactory.fromResource(com.jsbl.genix.R.drawable.end), 10f
                )
            }
        }
        polyline.endCap = RoundCap()
        polyline.width = 10f
        polyline.jointType = JointType.DEFAULT
    }

    override fun getLayoutRes(): Int {
        return com.jsbl.genix.R.layout.alt_fragment_my_trip_details
    }

    override fun onPolylineClick(p0: Polyline) {
    }

    override fun onPolygonClick(p0: Polygon) {
    }


    private fun corneringMarker(point: LatLng, text: String) {
        val markerOptions = MarkerOptions()
        val bitmap = getBitmap(requireContext(), com.jsbl.genix.R.drawable.ic_cornering)
        markerOptions.position(point).title(text)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        mGoogleMap?.addMarker(markerOptions)
    }
    private fun brakingMarker(point: LatLng, text: String) {
        val markerOptions = MarkerOptions()
        val bitmap = getBitmap(requireContext(), com.jsbl.genix.R.drawable.ic_braking)
        markerOptions.position(point).title(text)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        mGoogleMap?.addMarker(markerOptions)
    }
    private fun accelerationMarker(point: LatLng, text: String) {
        val markerOptions = MarkerOptions()
        val bitmap = getBitmap(requireContext(), com.jsbl.genix.R.drawable.ic_acceleration)
        markerOptions.position(point).title(text)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        mGoogleMap?.addMarker(markerOptions)
    }
    private fun speedingMarker(point: LatLng, text: String) {
        val markerOptions = MarkerOptions()
        val bitmap = getBitmap(requireContext(), com.jsbl.genix.R.drawable.ic_speeding)
        markerOptions.position(point).title(text)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        mGoogleMap?.addMarker(markerOptions)
    }
    private fun startMarker(point: LatLng, text: String) {
        val markerOptions = MarkerOptions()
        val bitmap = getBitmap(requireContext(), com.jsbl.genix.R.drawable.ic_start)
        markerOptions.position(point).title(text)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        mGoogleMap?.addMarker(markerOptions)
    }
    private fun endMarker(point: LatLng, text: String) {
        val markerOptions = MarkerOptions()
        val bitmap = getBitmap(requireContext(), com.jsbl.genix.R.drawable.ic_end)

        markerOptions.position(point).title(text)
            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        mGoogleMap?.addMarker(markerOptions)
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap? {
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        vectorDrawable.draw(canvas)
        return bitmap
    }

    private fun getBitmap(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        return if (drawable is BitmapDrawable) {
            BitmapFactory.decodeResource(context.getResources(), drawableId)
        } else if (drawable is VectorDrawable) {
            getBitmap(drawable)
        } else {
            throw IllegalArgumentException("unsupported drawable type")
        }
    }

}

