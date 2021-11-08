package com.jsbl.genix.views.fragments

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.maps.android.PolyUtil
import com.jsbl.genix.R
import com.jsbl.genix.databinding.FragmentMapBinding
import com.jsbl.genix.model.maps.LocationDetail
import com.jsbl.genix.model.maps.MarkerCameraAnimation
import com.jsbl.genix.network.GoogleApiServices
import com.jsbl.genix.network.GoogleRetrofitClient
import com.jsbl.genix.utils.MarkerWrapper
import com.jsbl.genix.utils.callBacks.AddressListener
import com.jsbl.genix.utils.callBacks.MapCallbacks
import com.utsman.samplegooglemapsdirection.kotlin.model.DirectionResponses
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback,
    OnCameraIdleListener, OnCameraMoveListener, OnMapClickListener,
    View.OnClickListener, OnMarkerClickListener,
    OnSuccessListener<Location?> {
    private lateinit var fragmentMapBinding: FragmentMapBinding
    var map: GoogleMap? = null
        private set
    var mapFragment: SupportMapFragment? = null
    var mainHandler: Handler? = null
    var thresholdHandler: Handler? = null
    var handlerThread: HandlerThread? = null
    var looper: Looper? = null
    var addressRunnable: Runnable? = null

    //FusedLocation
    private var fusedLocationClient: FusedLocationProviderClient? = null
    var isFirstTime = true
    var mapCallbacks: MapCallbacks? = null
    var locationFlag = FLAG_PICKUP
    var indexPickup = 0
    var indexDropOff = 0
    var indexCornering = 1
    var isExplicitCameraMove = true
    var isCameraMove = false

    //setter and getters
    var isEnableLocationMarker = true
    var markerWrapper: MarkerWrapper? = null

    //    TaxiApisController taxiApisController;
    //    Result addressResult;
    var address = ""
    var placeId = ""
    var addressListener: AddressListener? = null
    private lateinit var googleApiServices: GoogleApiServices

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        fragmentMapBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        mainHandler = Handler(requireContext().mainLooper)
        return fragmentMapBinding.getRoot()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        //        fusedLocationClient.getLastLocation().addOnSuccessListener(this);
        googleApiServices = GoogleRetrofitClient.apiServices(requireContext())

        init_components()
    }

    fun init_components() {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        handlerThread = HandlerThread("thresholdHandler")
        handlerThread!!.start()
        looper = handlerThread!!.looper
        thresholdHandler = Handler(looper!!)
        addressRunnable = Runnable {
            // Things to be done
            mainHandler!!.post { if (map != null) prepareForAddress(map!!.cameraPosition.target) }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.uiSettings.setAllGesturesEnabled(true)
        checkLocationIsEnabled(context)
        map = googleMap
        markerWrapper = MarkerWrapper(googleMap, requireActivity())
        applyStyle(DARK_THEME)
        //        mapConfigStepMain();
        if (mapCallbacks != null) {
            mapCallbacks!!.onMapReady(googleMap)
        }
    }

    //TODO
    fun checkLocationIsEnabled(context: Context?) {
        val lm =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        if (!gps_enabled && !network_enabled) {

            AlertDialog.Builder(requireContext())
                .setMessage("GPS not found \n Want to enable?")
                .setPositiveButton(
                    "enable"
                ) { paramDialogInterface: DialogInterface?, paramInt: Int ->
                    requireContext().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    fun applyStyle(mapType: Int) {
        /* if (mapType == DARK_THEME) {
            if (mMap == null) return;
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.dark_map_style));
        } else if (mapType == FEMA_THEME) {
            if (mMap == null) return;
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.style_map));

        } else if (mapType == CARGO_THEME) {

        }*/
    }

    fun setAllListeners(setListeners: Boolean) {
        if (setListeners) {
            map!!.setOnCameraIdleListener(this)
            map!!.setOnCameraMoveListener(this)
            map!!.setOnMarkerClickListener(this)
            map!!.setOnMapClickListener(this)
        } else {
            map!!.setOnCameraIdleListener(null)
            map!!.setOnCameraMoveListener(null)
            map!!.setOnMarkerClickListener(null)
            map!!.setOnMapClickListener(null)
        }
    }

    fun mapConfigStepMain() {
        if (map != null) {
            map!!.uiSettings.setAllGesturesEnabled(true)
            map!!.uiSettings.isMyLocationButtonEnabled = false
            setAllListeners(false)
            map!!.setOnCameraIdleListener(this)
            map!!.setOnCameraMoveListener(this)
            map!!.setOnMarkerClickListener(this)
            DEFAULT_ZOOM = 12
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FINE_LOCATION
                )
            } else {
                map!!.isMyLocationEnabled = true
            }
            requestLocation()
        }
    }

    fun mapConfigTracking() {
        if (map != null) {
            map!!.uiSettings.setAllGesturesEnabled(true)
            map!!.uiSettings.isMyLocationButtonEnabled = false
            setAllListeners(false)
            map!!.setOnCameraIdleListener(this)
            map!!.setOnCameraMoveListener(this)
            map!!.setOnMarkerClickListener(this)
            DEFAULT_ZOOM = 12
            requestLocation()
        }
    }

    fun mapConfigShowCase() {
        if (map != null) {
            map!!.uiSettings.setAllGesturesEnabled(true)
            map!!.uiSettings.isMyLocationButtonEnabled = false
            setAllListeners(false) /*
            mMap.setOnCameraIdleListener(this);
            mMap.setOnCameraMoveListener(this);
            mMap.setOnMarkerClickListener(this);*/
            //            DEFAULT_ZOOM = 12;
        }
    }

    fun mapConfigEvent() {
        if (map != null) {
            map!!.uiSettings.setAllGesturesEnabled(true)
            map!!.uiSettings.isMyLocationButtonEnabled = false
            setAllListeners(false)
            map!!.setOnCameraIdleListener(this)
            map!!.setOnCameraMoveListener(this)
            map!!.setOnMarkerClickListener(this)
            DEFAULT_ZOOM = 12
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FINE_LOCATION
                )
            } else {
                map!!.isMyLocationEnabled = true
            }
        }
    }

    fun mapConfigEventDetails() {
        if (map != null) {
            map!!.uiSettings.setAllGesturesEnabled(false)
            map!!.uiSettings.isMyLocationButtonEnabled = false
            setAllListeners(false)
            DEFAULT_ZOOM = 12
        }
    }

    fun mapConfigStepLocation() {
        if (map != null) {
            map!!.uiSettings.setAllGesturesEnabled(true)
            map!!.uiSettings.isMyLocationButtonEnabled = false
            setAllListeners(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FINE_LOCATION
                )
            } else {
                map!!.isMyLocationEnabled = true
            }
            map!!.setOnCameraIdleListener(this)
            map!!.setOnCameraMoveListener(this)
            DEFAULT_ZOOM = 16
        }
    }

    fun mapConfigStepSelectRider() {
        if (map != null) {
            map!!.uiSettings.setAllGesturesEnabled(false)
            setAllListeners(false)
        }
    }

    fun mapConfigStepVehicleCategory() {
        if (map != null) {
            map!!.uiSettings.isMyLocationButtonEnabled = true
            map!!.uiSettings.setAllGesturesEnabled(false)
            //            mMap.getUiSettings().setAllGesturesEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FINE_LOCATION
                )
            } else {
                map!!.isMyLocationEnabled = true
            }
            setAllListeners(false)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_FINE_LOCATION
                    )
                } else {
                    map!!.isMyLocationEnabled = true
                }
                currentLocation
                map!!.isMyLocationEnabled = true

                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
//                    requestLocationPermission();
            }
        }
    }

    private val currentLocation: Unit
        private get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FINE_LOCATION
                )
            } else {
                fusedLocationClient!!.lastLocation.addOnSuccessListener(this)
            }
        }

    fun requestLocation() {
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FINE_LOCATION
                )
            } else {
//            mMap.setMyLocationEnabled(false);
                currentLocation
            }
        }
    }

    override fun onDestroy() {
        Log.d("tms:rolarappa", "hello")
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onClick(v: View) {}
    override fun onCameraIdle() {
        if (mapCallbacks != null) {
            mapCallbacks!!.onCameraIdle()
        }
        isCameraMove = false
        if (isExplicitCameraMove) {
            setLocationMarkers(map!!.cameraPosition.target)
            //            prepareForAddress(mMap.getCameraPosition().target);
            if (addressListener != null) addressListener!!.startLoading(true)
            addressRunnable?.let { thresholdHandler!!.postDelayed(it, 1500) }
        } else {
            isExplicitCameraMove = true
        }
    }

    override fun onCameraMove() {
        addressRunnable?.let { thresholdHandler!!.removeCallbacks(it) }
        if (mapCallbacks != null) {
            mapCallbacks!!.onCameraMove()
        }
        isCameraMove = true
        if (isExplicitCameraMove) setLocationMarkers(map!!.cameraPosition.target)
    }

    override fun onMapClick(latLng: LatLng) {
        if (mapCallbacks != null) {
            mapCallbacks!!.onMapClick(latLng)
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (mapCallbacks != null) {
            mapCallbacks!!.onMarkerClick(marker)
        }
        return false
    }

    override fun onSuccess(location: Location?) {
        if (location != null) {
            if (mapCallbacks != null) {
                mapCallbacks!!.onLocationSuccess(location)
            }
            updateLatestLocation(location)
        }
    }

    private fun updateLatestLocation(location: Location?) {
        if (location != null) {
            moveCamera(
                LatLng(location.latitude, location.longitude),
                DEFAULT_ZOOM.toFloat()
            )
            Log.d(
                "latlng",
                """
                    lat : ${location.latitude}
                    lng : ${location.longitude}
                    """.trimIndent()
            )
            setLocationMarkers(LatLng(location.latitude, location.longitude))
            //            prepareForAddress(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    private fun setLocationMarkers(location: LatLng) {
        if (addressListener == null) {
            return
        }
        if (!isEnableLocationMarker) {
            return
        }
        val locationDetail = LocationDetail()
        locationDetail.lat = location.latitude
        locationDetail.lng = location.longitude
        when (locationFlag) {
            FLAG_PICKUP -> markerWrapper!!.handlePickupLocationMarkers(
                locationDetail,
                indexPickup
            )
            FLAG_DROP_OFF -> markerWrapper!!.handleDropOffLocationMarkers(
                locationDetail,
                indexDropOff
            )
            FLAG_CORNERING -> markerWrapper!!.handleDropOffLocationMarkers(
                locationDetail,
                indexCornering
            )
        }
    }

    fun setLocationMarkersExplicitly(location: LatLng, locationFlag: Int) {
        val locationDetail = LocationDetail()
        locationDetail.lat = location.latitude
        locationDetail.lng = location.longitude
        when (locationFlag) {
            FLAG_PICKUP -> markerWrapper!!.handlePickupLocationMarkers(
                locationDetail,
                indexPickup
            )
            FLAG_DROP_OFF -> markerWrapper!!.handleDropOffLocationMarkers(
                locationDetail,
                indexDropOff
            )
            FLAG_CORNERING -> markerWrapper!!.handleDropOffLocationMarkers(
                locationDetail,
                indexCornering
            )
        }
    }

    fun moveToLocation(latLng: LatLng?) {
        isExplicitCameraMove = false
        moveCamera(
            latLng,
            DEFAULT_ZOOM.toFloat()
        )
    }

    fun moveCamera(latLng: LatLng?, zoom: Float) {
        if (!isFirstTime) mainHandler!!.post {
            map!!.animateCamera(
                CameraUpdateFactory.newLatLngZoom(latLng, zoom)
            )
        } else {
            mainHandler!!.post {
                map!!.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        zoom
                    )
                )
            }
            isFirstTime = false
        }
    }

    private val builder: LatLngBounds.Builder? = null
    fun zoomOnMarkers(pickUpLL: LatLng, dropOffLL: LatLng, padding: Int) {
        if (map != null) {
//            mMap.clear();
            val latLngBounds =
                MarkerCameraAnimation.getInstance().createBoundsWithMinDiagonal(pickUpLL, dropOffLL)
            //            mMap.moveCamera(cu1);
//            mMap.setPadding(0,320,0,0);
            mainHandler!!.post {

                // map is the GoogleMap object
                // marker is Marker object
                // ! here, map.getProjection().toScreenLocation(marker.getPosition()) will return (0, 0)
                // R.id.map is the ID of the MapFragment in the layout XML file
                val cu =
                    CameraUpdateFactory.newLatLngBounds(latLngBounds, padding)
                map!!.animateCamera(cu)
              /*  val mapView =
                    childFragmentManager.findFragmentById(R.id.map)!!.view
                if (mapView!!.viewTreeObserver.isAlive) {
                    mapView.viewTreeObserver
                        .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                // remove the listener
                                mapView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                                // ! you can query Projection object here
//                        Point markerScreenPosition = mMap.getProjection().toScreenLocation(latLngBounds.getCenter());
                            }
                        })
                }*/
            }
        }
    }

    fun getmMap(): GoogleMap? {
        return map
    }

    fun setmMap(mMap: GoogleMap?) {
        map = mMap
    }

    fun setPickupLocationFlag(): Int {
        return FLAG_PICKUP.also {
            locationFlag = it
        }
    }

    fun setDropOffLocationFlag(): Int {
        return FLAG_DROP_OFF.also {
            locationFlag = it
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    //RequestResponses
    fun prepareForAddress(latLng: LatLng) {
        if (addressListener == null) {
            return
        }
        if (latLng.latitude == 0.0 || latLng.longitude == 0.0) return
        addressListener!!.onGetLocation(latLng)
        addressListener!!.startLoading(true)
        //        DistanceRequestO distanceRequestO = new DistanceRequestO();
//        distanceRequestO.setUrl(MapHelper.getUrl(latLng));
//
//        TaxiApisController.getInstances(getActivity()).getAddressByServer(distanceRequestO, addressResponse);
    }

    @Synchronized
    fun removeRoute() {
        if (handler != null) {
            runnable?.let { handler.removeCallbacks(it) }
        }
        if (line != null) {
            mainHandler!!.post { line!!.remove() }
        }
    }

    fun drawRoute(origin: LatLng, destination: LatLng) {

        val fromFKIP = origin.latitude.toString() + "," + origin.longitude.toString()
        val toMonas = destination.latitude.toString() + "," + destination.longitude.toString()

//        removeRoute()
        if (map != null) {
            // TODO  draw route with api here...
            googleApiServices.getDirection(
                fromFKIP,
                toMonas,
                getString(R.string.google_maps_key)
               /* "metric",
                "driving",
                getString(R.string.google_maps_key)*/
            )
                .enqueue(object : Callback<DirectionResponses> {
                    override fun onResponse(
                        call: Call<DirectionResponses>,
                        response: Response<DirectionResponses>
                    ) {
                        drawPolyline(response)
                        Log.d("bisa dong oke", response.message())
                    }
                    override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                        Log.e("anjir error", t.localizedMessage)
                    }
                })

//            DistanceRequestO distanceRequestO = new DistanceRequestO();
//            distanceRequestO.setUrl(MapHelper.getUrl(origin, destination, "driving", getActivity()));
//
//            TaxiApisController.getInstances(getActivity()).getDistanceByServer(distanceRequestO, this);
        }
    }

    fun removeAllPickups() {
        markerWrapper!!.removeAllPickupMarkers()
    }

    fun removeAllDropOffs() {
        markerWrapper!!.removeAllDropOffMarkers()
    }

    fun addRipples(latLng: LatLng?) {
        mainHandler!!.post { map!!.uiSettings.setAllGesturesEnabled(false) }
        markerWrapper!!.showRipples(latLng)
    }

    fun removeRipple() {
        mainHandler!!.post { map!!.uiSettings.setAllGesturesEnabled(true) }
        markerWrapper!!.removeRipples()
    }

    var handler = Handler()
    var runnable: Runnable? = null
    var delay = 10000 //milliseconds // TODO need testing

    //
    //    @Override
    //    public void getObjectResponse(@Nullable Object object) {
    //        if (object == null) return;
    //        if (object instanceof RequestResponse) {
    //            RequestResponse response = (RequestResponse) object;
    //            if (response.getStatusCode() == 200) {
    //
    //                DistanceResponse responseOne = response.getDirectionData();
    //                ArrayList<LatLng> points = responseOne.getPolylineArray();
    //
    //                if (points != null && points.size() > 0) {
    //
    //                    for (LatLng latLng : points) {
    //                        Log.e("tms", "latLng : " + latLng.toString());
    //                    }
    //                    drawPolyLinesRoute(points);
    //                    handler.postDelayed(runnable = new Runnable() {
    //                        public void run() {
    //                            //do something
    //                            if (getActivity() != null && mMap != null && getActivity().isFinishing() && mMap != null && points != null) {
    //                                drawPolyLinesRoute(points);
    //                            }
    //                            handler.postDelayed(this, delay);
    //                        }
    //                    }, delay);
    //                }
    //                // TODO remove later...
    //                /**/
    ////                if (response.getDirectionData().getRoutesList().size() > 0) {
    ////                    PointsParser parserTask = new PointsParser(this, getContext(), "driving");
    ////                    /* Invokes the thread for parsing the JSON data*/
    ////                    parserTask.execute(response.getDirectionData().toString());
    ////                } else {
    ////                    Log.e("tms", "routes empty.");
    ////                }
    //            } else {
    //                Log.e("tms", "routes api failed.");
    //            }
    //        }
    //    }
    private var line: Polyline? = null

    @Synchronized
    private fun drawPolyLinesRoute(points: ArrayList<LatLng>) {
//        mMapAnimator = MapAnimator.getInstance(MapHelper.getColors(3));
//        mMapAnimator.animateRoute(getActivity(), mMap, points);
        if (line != null) {
            line!!.remove()
        }
        val options =
            PolylineOptions().width(8f).color(resources.getColor(R.color.cmPrimary))
                .geodesic(true)
        for (z in points.indices) {
            val point = points[z]
            options.add(point)
        }
        line = map!!.addPolyline(options)
    }

    companion object {
        const val REQUEST_FINE_LOCATION = 1090

        //Themes
        const val DARK_THEME = 1
        const val FEMA_THEME = 2
        const val CARGO_THEME = 3

        //MapConfig
        var DEFAULT_ZOOM = 12

        //Marker Controllers FLAGS
        const val FLAG_PICKUP = 1
        const val FLAG_DROP_OFF = 2
        const val FLAG_CHOOSE_RIDER = 3
        const val FLAG_CORNERING = 4
        const val UNKNOWN_ADDRESS = "Unknown Address"
        const val LOADING = "Loading..."
    }


    private fun drawPolyline(response: Response<DirectionResponses>) {
        if (response.body()?.routes?.size == 0) {
//            showShort(context,"Route nt")
            return
        }
        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points
        val polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(R.color.cmPrimary)
        map!!.addPolyline(polyline)
    }



}