package com.jsbl.genix.views.activities

import android.location.Location
import android.os.Bundle
import android.view.View
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivityTripDetailsBinding
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.trips.TripItem
import com.jsbl.genix.utils.RequestHandler
import com.jsbl.genix.utils.callBacks.MapCallbacks
import com.jsbl.genix.utils.logD
import com.jsbl.genix.utils.showShort
import com.jsbl.genix.viewModel.MainHomeViewModel
import com.jsbl.genix.views.fragments.MapFragment
import retrofit2.Response


class TripDetailsActivity : BaseActivity<MainHomeViewModel, ActivityTripDetailsBinding>(
    MainHomeViewModel::class.java
), MapCallbacks, OnMapReadyCallback, GoogleMap.OnPolylineClickListener,
    GoogleMap.OnPolygonClickListener {
    private var customerX = CustomerX()

    private lateinit var dummyMapFragment: MapFragment
    private val mMap: GoogleMap? = null
    private val mGoogleApiClient: GoogleApiClient? = null

    var pickUpLL = LatLng(
        33.598961, 73.154420
    )
    var dropOffLL = LatLng(
        33.6664943, 73.0556458
    )

    var startingLatLng:Double = 0.0
    var endingLatLng:Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        addMap()

        binding.onClickListener = this
        viewModel.fetchFromDatabase()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }


    fun addMap() {

    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.drawerImage -> {
                this.onBackPressed()
            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
    }

    override  fun onSuccess(obj: RequestHandler) {

        if (viewModel.getTrips == true) {
            logD("**trips", "success :  " +viewModel.getTrips)
            if (obj.any is TripItem) {
                val rr = obj.any as TripItem
                logD("**onSuccss", "2")


            } else if (obj.any is Response<*>) {
            }

        }

    }

    override fun onError(obj: RequestHandler) {
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
        dummyMapFragment.setLocationMarkersExplicitly(pickUpLL, MapFragment.FLAG_PICKUP)
        dummyMapFragment.setLocationMarkersExplicitly(dropOffLL, MapFragment.FLAG_DROP_OFF)
        dummyMapFragment.drawRoute(pickUpLL, dropOffLL)
        dummyMapFragment.mapConfigShowCase()
        dummyMapFragment.zoomOnMarkers(pickUpLL, dropOffLL, 100)

        val polyline1 = googleMap?.addPolyline(PolylineOptions()
            .clickable(true)
            .add(
                LatLng(-35.016, 143.321),
                LatLng(-34.747, 145.592),
                LatLng(-34.364, 147.891),
                LatLng(-33.501, 150.217),
                LatLng(-32.306, 149.248),
                LatLng(-32.491, 147.309)))

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.

        // Set listeners for click events.
        googleMap?.setOnPolylineClickListener(this)
        googleMap?.setOnPolygonClickListener(this)
        if (polyline1 != null) {
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
                    BitmapDescriptorFactory.fromResource(R.drawable.alt_arrow), 10f
                )
            }
            "End" -> {
                // Use a round cap at the start of the line.
                polyline.startCap = RoundCap()
            }
        }
        polyline.endCap = RoundCap()
        polyline.width = 12f
        polyline.jointType = JointType.ROUND
    }

    private val PATTERN_GAP_LENGTH_PX = 20
    private val DOT: PatternItem = Dot()
    private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX.toFloat())

    // Create a stroke pattern of a gap followed by a dot.
    private val PATTERN_POLYLINE_DOTTED = listOf(GAP, DOT)

    /**
     * Listens for clicks on a polyline.
     * @param polyline The polyline object that the user has clicked.
     */
    override fun onPolylineClick(polyline: Polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if (polyline.pattern == null || !polyline.pattern!!.contains(DOT)) {
            polyline.pattern = PATTERN_POLYLINE_DOTTED
        } else {
            // The default pattern is a solid stroke.
            polyline.pattern = null
        }
//        Toast.makeText(this, "Route type " + polyline.tag.toString(),
//            Toast.LENGTH_SHORT).show()
    }

    /**
     * Listens for clicks on a polygon.
     * @param polygon The polygon object that the user has clicked.
     */
    override fun onPolygonClick(polygon: Polygon) {
        // Flip the values of the red, green, and blue components of the polygon's color.
        var color = polygon.strokeColor xor 0x00ffffff
        polygon.strokeColor = color
        color = polygon.fillColor xor 0x00ffffff
        polygon.fillColor = color
        showShort(this, "Area type ${polygon.tag?.toString()}")
//        Toast.makeText(this, "Area type ${polygon.tag?.toString()}", Toast.LENGTH_SHORT).show()
    }

    private val COLOR_WHITE_ARGB = -0x1
    private val COLOR_GREEN_ARGB = -0xc771c4
    private val COLOR_PURPLE_ARGB = -0x7e387c
    private val COLOR_ORANGE_ARGB = -0xa80e9
    private val COLOR_BLUE_ARGB = -0x657db
    private val POLYGON_STROKE_WIDTH_PX = 8
    private val PATTERN_DASH_LENGTH_PX = 20

    private val DASH: PatternItem = Dash(PATTERN_DASH_LENGTH_PX.toFloat())

    // Create a stroke pattern of a gap followed by a dash.
    private val PATTERN_POLYGON_ALPHA = listOf(GAP, DASH)

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private val PATTERN_POLYGON_BETA = listOf(DOT, GAP, DASH, GAP)

    /**
     * Styles the polygon, based on type.
     * @param polygon The polygon object that needs styling.
     */
    private fun stylePolygon(polygon: Polygon) {
        // Get the data object stored with the polygon.
        val type = polygon.tag?.toString() ?: ""
        var pattern: List<PatternItem>? = null
        var strokeColor = resources.getColor(R.color.black)
        var fillColor = COLOR_WHITE_ARGB
        when (type) {
            "alpha" -> {
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA
                strokeColor = COLOR_GREEN_ARGB
                fillColor = COLOR_PURPLE_ARGB
            }
            "beta" -> {
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA
                strokeColor = COLOR_ORANGE_ARGB
                fillColor = COLOR_BLUE_ARGB
            }
        }
        polygon.strokePattern = pattern
        polygon.strokeWidth = POLYGON_STROKE_WIDTH_PX.toFloat()
        polygon.strokeColor = strokeColor
        polygon.fillColor = fillColor
    }

    override fun initViewModel(viewModel: MainHomeViewModel) {

    }

    override fun getLayoutRes(): Int {
        TODO("Not yet implemented")
    }
}