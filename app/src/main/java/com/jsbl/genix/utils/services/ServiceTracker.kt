package com.jsbl.genix.utils.services

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.jsbl.genix.BuildConfig
import com.jsbl.genix.R
import com.jsbl.genix.alt.MainActivity
import com.jsbl.genix.model.profileManagement.PostCarDetail
import com.jsbl.genix.utils.Constants.currentSpeed
import com.jsbl.genix.utils.Constants.tripEnded
import com.jsbl.genix.utils.FileLoggingTree
import com.jsbl.genix.utils.SharePreferencesHelper
import com.jsbl.genix.utils.logD
import com.jsbl.genix.views.activities.ActivityMain
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_ACTIVITY_RECOGNITION
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_BACKGROUNDlOCATION
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_CAM
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_COARSE_LOC
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_CODE_CAMERA
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_CODE_FINE_LOCATION
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_CODE_OTP
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_LOC
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_READ_PHONE_STATE
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_READ_PHONE_STATE_FP
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_READ_PHONE_STATE_REG
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_READ_ST
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_REQ_PERMISSIOM
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_SMS
import com.jsbl.genix.views.activities.ActivityMain.Companion.REQUEST_WRITE_ST
import com.scope.mhub.app.MHubConstants
import com.scope.mhub.app.MHubConstants.APP_NAME
import com.scope.mhub.app.SCPSmartDriveManager
import com.scope.mhub.mprofiler.MProfilerConfiguration
import com.scope.mhub.utils.SmartDriveUtils
import com.scope.mhub.utils.TripManagementHelper
import com.scope.portalapiclient.PortalApi
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


enum class ServiceState {
    STARTED,
    STOPPED,
}

enum class Actions {
    START,
    STOP
}

fun actionForService(context: Activity, start: Boolean) {
//    actionOnService(, Actions.START, viewModel.prefsHelper)
    if (start) {
        if (!checkGPS(context)) {
            return
        }
        if (checkLocationPermission(context)) {
            Timber.plant(FileLoggingTree(context.applicationContext))

            SCPSmartDriveManager.getInstance(context).appName = context.getString(R.string.app_name)
            SCPSmartDriveManager.getInstance(context)
                .setAppNameAbbreviation(context.getString(R.string.app_name))
            SCPSmartDriveManager.getInstance(context).setTripActivity(ActivityMain::class.java)
             val profileConfig =
                 MProfilerConfiguration.Builder().wpiUser(
                     SharePreferencesHelper(context).getScopeName()
                 ).wpiPassword(SharePreferencesHelper(context).getScopePass()).build()
             SCPSmartDriveManager.getInstance(context)
                 .setupProfilerConfiguration(profileConfig)
            SCPSmartDriveManager.getInstance(context).isTripAutoSendingEnabled = true
            SCPSmartDriveManager.getInstance(context).resendFailedTrips(true)
            SCPSmartDriveManager.getInstance(context).connectToCCFramework()
            SCPSmartDriveManager.getInstance(context).runRecordTripService()

//            com.scope.smartdrivedemo.FileLoggingTree.checkForObsoleteLogFiles(context)

            // copied from demo app

            SmartDriveUtils.setFloatPreference(
                context,
                MHubConstants.AUTOSTOP_THRESHOLD_PREF,
                0.25f
            )
            with (TripManagementHelper.getInstance(context)) {
                tripDistance = tripDistance
                tripDuration = tripDuration
                registerTripEventReceiver(object : TripManagementHelper.TripEventReceiver() {
                    override fun onTripStarted() {
                        Log.v("******TripStatus", "onTripStarted")
                    }

                    override fun onTripEnded() {
                        Log.v("******TripStatus", "onTripEnded")
                    }
                })
                startHelper()
            }


        }
        else {
            Timber.i("Logout when location permission not granted for ALL time")
            TripManagementHelper.getInstance(context).stopHelper()
            SCPSmartDriveManager.getInstance(context).logout()
            PortalApi.logout()
        }
    } else {
        Timber.i("Logout from Ai-Genix app")
        TripManagementHelper.getInstance(context).stopHelper()
        SCPSmartDriveManager.getInstance(context).logout()
        PortalApi.logout()
    }
}

fun findDefaultPosition(carDetails: ArrayList<PostCarDetail>): Int {

    for (i in carDetails.indices) {
        if (carDetails[i].isDefaultCar) {
            return i
        }
    }
    return -1

}


fun actionOnService(
    activity: Activity,
    action: Actions,
    preferences: SharePreferencesHelper
) {
    if (!checkGPS(activity)) {
        return
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        if (checkLocationPermission(activity)) {
            if (preferences.getForegroundServiceState() == ServiceState.STOPPED && action == Actions.STOP) return
            Intent(activity, GenixService::class.java).also {
                it.action = action.name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    logD("logGenix", "Starting the service in >=26 Mode")
                    activity.startForegroundService(it)
                    return@also
                }
                logD("logGenix", "Starting the service in < 26 Mode")
                activity.startService(it)
            }
        }
    } else {
        logD("logGenix", "Starting the service in < 23 Mode")
        Intent(activity, GenixService::class.java).also {
            it.action = action.name
            logD("logGenix", "Starting the service in < 23 Mode")
            activity.startService(it)
        }
        return
    }
}

fun toggleService(activity: Activity, preferences: SharePreferencesHelper) {
    var action: Actions = if (preferences.getForegroundServiceState() == ServiceState.STOPPED) {
        Actions.START
    } else {
        Actions.STOP
    }
    if (!checkGPS(activity)) {
        return
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        if (checkLocationPermission(activity)) {
            if (preferences.getForegroundServiceState() == ServiceState.STOPPED && action == Actions.STOP) return
            Intent(activity, GenixService::class.java).also {
                it.action = action.name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    logD("logGenix", "Starting the service in >=26 Mode")
                    activity.startForegroundService(it)
                    return@also
                }
                logD("logGenix", "Starting the service in < 26 Mode")
                activity.startService(it)
            }
        }
    } else {
        logD("logGenix", "Starting the service in < 23 Mode")
        Intent(activity, GenixService::class.java).also {
            it.action = action.name
            logD("logGenix", "Starting the service in < 23 Mode")
            activity.startService(it)
        }
        return
    }
}

fun checkGPS(context: Context): Boolean {
    val service =
        context.getSystemService(LOCATION_SERVICE) as LocationManager?
    val enabled = service
        ?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings
    if (!enabled!!) {
        AlertDialog.Builder(
            ContextThemeWrapper(
                context, R.style.my_dialog_theme
            )
        )
            .setTitle("Enable GPS")
            .setMessage("No Location Provider is Available, kindly Enable GPS !")
            .setPositiveButton(
                "Ok"
            ) { dialogInterface, i -> //Prompt the user once explanation has been shown
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()

        return false
    }
    return true
}

/*
fun checkNetworkProvider(context: Context): Boolean {
    val service =
        context.getSystemService(LOCATION_SERVICE) as LocationManager?
    val enabled = service
        ?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)!!

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings
    if (!enabled!!) {
        AlertDialog.Builder(
            ContextThemeWrapper(
                context, R.style.my_dialog_theme
            )
        )
            .setTitle("Enable Network")
            .setMessage("No Location Provider is Available, kindly Enable Network !")
            .setPositiveButton(
                "Ok"
            ) { dialogInterface, i -> //Prompt the user once explanation has been shown
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()

        return false
    }
    return true
}*/


fun checkLocationPermission(activity: Activity): Boolean {
    //check the location permissions and return true or false.
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permissions granted
            true
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            ) {
                /*// Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(
                    ContextThemeWrapper(
                        activity, R.style.my_dialog_theme
                    )
                )
                    .setTitle("Permissions request")
                    .setMessage("we need your permission for location and Activity!")
                    .setPositiveButton(
                        "Ok"
                    ) { dialogInterface, i -> *///Prompt the user once explanation has been shown
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACTIVITY_RECOGNITION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_CODE_FINE_LOCATION
                )
                /*  }
                  .create()
                  .show()*/
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACTIVITY_RECOGNITION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION

                    ),
                    REQUEST_CODE_FINE_LOCATION
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permissions granted
            true
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(
                    ContextThemeWrapper(
                        activity, R.style.my_dialog_theme
                    )
                )
                    .setTitle("Permissions request")
                    .setMessage("we need your permission for location and Activity!")
                    .setPositiveButton(
                        "Ok"
                    ) { dialogInterface, i -> //Prompt the user once explanation has been shown
                        requestPermissions(
                            activity,
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            REQUEST_CODE_FINE_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION

                    ),
                    REQUEST_CODE_FINE_LOCATION
                )
            }
            false
        }
    }

}

fun checkOtpPermission(activity: Activity): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED

        ) {
            //permissions granted
            true
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.SEND_SMS
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(
                    ContextThemeWrapper(
                        activity, R.style.my_dialog_theme
                    )
                )
                    .setTitle("OTP Permissions Request")
                    .setMessage("We need your permission")
                    .setPositiveButton(
                        "Ok"
                    ) { dialogInterface, i -> //Prompt the user once explanation has been shown
                        requestPermissions(
                            activity,
                            arrayOf(
                                Manifest.permission.SEND_SMS
                            ),
                            REQUEST_CODE_FINE_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.SEND_SMS
                    ),
                    REQUEST_CODE_FINE_LOCATION
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permissions granted
            true
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.SEND_SMS
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(
                    ContextThemeWrapper(
                        activity, R.style.my_dialog_theme
                    )
                )
                    .setTitle("OTP Permissions Request")
                    .setMessage("We need your permissions")
                    .setPositiveButton(
                        "Ok"
                    ) { dialogInterface, i -> //Prompt the user once explanation has been shown
                        requestPermissions(
                            activity,
                            arrayOf(
                                Manifest.permission.SEND_SMS
                            ),
                            REQUEST_CODE_FINE_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    activity,
                    arrayOf(

                        Manifest.permission.SEND_SMS

                    ),
                    REQUEST_CODE_FINE_LOCATION
                )
            }
            false
        }
    }
}


fun otpPermission(activity: Activity): Boolean {
    //check the location permissions and return true or false.
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permissions granted
            true
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,

                    Manifest.permission.SEND_SMS
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(
                    ContextThemeWrapper(
                        activity, R.style.my_dialog_theme
                    )
                )
                    .setTitle("OTP Permissions Request")
                    .setMessage("we need your permissions")
                    .setPositiveButton(
                        "Ok"
                    ) { dialogInterface, i -> //Prompt the user once explanation has been shown
                        requestPermissions(
                            activity,
                            arrayOf(

                                Manifest.permission.SEND_SMS
                            ),
                            REQUEST_CODE_OTP
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.SEND_SMS

                    ),
                    REQUEST_CODE_OTP
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permissions granted
            true
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,

                    Manifest.permission.SEND_SMS
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(
                    ContextThemeWrapper(
                        activity, R.style.my_dialog_theme
                    )
                )
                    .setTitle("OTP Permissions Request")
                    .setMessage("we need your permissions")
                    .setPositiveButton(
                        "Ok"
                    ) { dialogInterface, i -> //Prompt the user once explanation has been shown
                        requestPermissions(
                            activity,
                            arrayOf(
                                Manifest.permission.SEND_SMS
                            ),
                            REQUEST_CODE_OTP
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.SEND_SMS

                    ),
                    REQUEST_CODE_OTP
                )
            }
            false
        }
    }

}


fun cameraPermission(activity: Activity): Boolean {
    //check the location permissions and return true or false.
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permissions granted
            true
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,

                    Manifest.permission.CAMERA
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(
                    ContextThemeWrapper(
                        activity, R.style.my_dialog_theme
                    )
                )
                    .setTitle("Camera Permissions Request")
                    .setMessage("we need your permissions")
                    .setPositiveButton(
                        "Ok"
                    ) { dialogInterface, i -> //Prompt the user once explanation has been shown
                        requestPermissions(
                            activity,
                            arrayOf(

                                Manifest.permission.SEND_SMS
                            ),
                            REQUEST_CODE_CAMERA
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.CAMERA

                    ),
                    REQUEST_CODE_CAMERA
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permissions granted
            true
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,

                    Manifest.permission.CAMERA
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
//                AlertDialog.Builder(
//                    ContextThemeWrapper(
//                        activity, R.style.my_dialog_theme
//                    )
//                )
//                    .setTitle("Camera Permissions Request")
//                    .setMessage("we need your permissions")
//                    .setPositiveButton(
//                        "Ok"
//                    ) { dialogInterface, i -> //Prompt the user once explanation has been shown
//                        requestPermissions(
//                            activity,
//                            arrayOf(
//                                Manifest.permission.CAMERA
//                            ),
//                            REQUEST_CODE_CAMERA
//                        )
//                    }
//                    .create()
//                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.CAMERA

                    ),
                    REQUEST_CODE_CAMERA
                )
            }
            false
        }
    }

}


fun checkAllPermission(activity: Activity): Boolean {
    //check the location permissions and return true or false.
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED*/
        ) {
            //permissions granted
            true
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.CAMERA
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.SEND_SMS
                )/*|| ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_PHONE_STATE
                )*/
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(
                    ContextThemeWrapper(
                        activity, R.style.my_dialog_theme
                    )
                )
                    .setTitle("Permissions request")
                    .setMessage("we need your permissions")
                    .setPositiveButton(
                        "Ok"
                    ) { dialogInterface, i -> //Prompt the user once explanation has been shown
                        requestPermissions(
                            activity,
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACTIVITY_RECOGNITION,
                                Manifest.permission.CAMERA,
                                Manifest.permission.SEND_SMS,
//                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ),
                            REQUEST_CODE_FINE_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACTIVITY_RECOGNITION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.SEND_SMS,
//                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION

                    ),
                    REQUEST_CODE_FINE_LOCATION
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED*/
        ) {
            //permissions granted
            true
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.CAMERA
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.SEND_SMS
                ) /*|| ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_PHONE_STATE
                )*/
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(
                    ContextThemeWrapper(
                        activity, R.style.my_dialog_theme
                    )
                )
                    .setTitle("Permissions request")
                    .setMessage("we need your permissions")
                    .setPositiveButton(
                        "Ok"
                    ) {

                            dialogInterface, i -> //Prompt the user once explanation has been shown
                        requestPermissions(
                            activity,
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.CAMERA,
//                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.SEND_SMS
                            ),
                            REQUEST_CODE_FINE_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
//                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.SEND_SMS

                    ),
                    REQUEST_CODE_FINE_LOCATION
                )
            }
            false
        }
    }

}

fun checkAllPermissionReg(activity: Activity): Boolean {
    //check the location permissions and return true or false.
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                /* Manifest.permission.ACCESS_BACKGROUND_LOCATION
             ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                 activity,*/
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED*/
        ) {
            //permissions granted
            true
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    /*Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,*/
                    Manifest.permission.CAMERA
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.SEND_SMS
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACTIVITY_RECOGNITION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                                Manifest.permission.READ_PHONE_STATE,
//                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_REQ_PERMISSIOM
                )
//                    }
//                    .create()
//                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACTIVITY_RECOGNITION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_PHONE_STATE,
//                        Manifest.permission.ACCESS_BACKGROUND_LOCATION

                    ),
                    REQUEST_REQ_PERMISSIOM
                )
            }
            false
        }
    }



    else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permissions granted
            true
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.CAMERA
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.SEND_SMS
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_PHONE_STATE
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                                Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.SEND_SMS
                    ),
                    REQUEST_REQ_PERMISSIOM
                )
//                    }
//                    .create()
//                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.SEND_SMS

                    ),
                    REQUEST_REQ_PERMISSIOM
                )
            }
            false
        }
    }

}




fun checkSmsPermission(activity: Activity): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permissions granted
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.SEND_SMS
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.SEND_SMS
                    ),
                    REQUEST_SMS
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.SEND_SMS
                    ),
                    REQUEST_SMS
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.SEND_SMS
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.SEND_SMS
                    ),
                    REQUEST_SMS
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.SEND_SMS
                    ),
                    REQUEST_BACKGROUNDlOCATION
                )
            }
            false
        }
    }

}

fun checkAccessBgLocation(activity: Activity): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permissions granted
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_BACKGROUNDlOCATION
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_BACKGROUNDlOCATION
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_BACKGROUNDlOCATION
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_BACKGROUNDlOCATION
                )
            }
            false
        }
    }

}



fun checkRecognitionPermission(activity: Activity): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permissions granted
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ),
                    REQUEST_ACTIVITY_RECOGNITION
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ),
                    REQUEST_ACTIVITY_RECOGNITION
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ),
                    REQUEST_ACTIVITY_RECOGNITION
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ),
                    REQUEST_ACTIVITY_RECOGNITION
                )
            }
            false
        }
    }

}


fun checkCameraPermission(activity: Activity): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permissions granted
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.CAMERA
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.CAMERA
                    ),
                    REQUEST_CAM
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.CAMERA
                    ),
                    REQUEST_CAM
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.CAMERA
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.CAMERA
                    ),
                    REQUEST_CAM
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.CAMERA
                    ),
                    REQUEST_CAM
                )
            }
            false
        }
    }

}

fun checkLocPermission(activity: Activity): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_LOC
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_LOC
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_LOC
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_LOC
                )
            }
            false
        }
    }

}

fun checkCoarseLocPermission(activity: Activity): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_COARSE_LOC
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_COARSE_LOC
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_COARSE_LOC
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_COARSE_LOC
                )
            }
            false
        }
    }

}


fun checkReadExterStPermission(activity: Activity): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    REQUEST_READ_ST
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    REQUEST_READ_ST
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    REQUEST_READ_ST
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    REQUEST_READ_ST
                )
            }
            false
        }
    }

}

fun checkWriteExterStPermission(activity: Activity): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQUEST_WRITE_ST
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQUEST_WRITE_ST
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQUEST_WRITE_ST
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQUEST_WRITE_ST
                )
            }
            false
        }
    }

}

fun checkPhysicalActtPermission(activity: Activity): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ),
                    REQUEST_ACTIVITY_RECOGNITION
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ),
                    REQUEST_ACTIVITY_RECOGNITION
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ),
                    REQUEST_ACTIVITY_RECOGNITION
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ),
                    REQUEST_ACTIVITY_RECOGNITION
                )
            }
            false
        }
    }

}





fun checkPhoneStatePermission(activity: Activity): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_PHONE_STATE
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    REQUEST_READ_PHONE_STATE
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    REQUEST_READ_PHONE_STATE
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_PHONE_STATE
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    REQUEST_READ_PHONE_STATE
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    REQUEST_READ_PHONE_STATE
                )
            }
            false
        }
    }

}






fun checkPhoneStatePermissionFP(activity: Activity): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_PHONE_STATE
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    REQUEST_READ_PHONE_STATE_FP
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    REQUEST_READ_PHONE_STATE_FP
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_PHONE_STATE
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    REQUEST_READ_PHONE_STATE_FP
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    REQUEST_READ_PHONE_STATE_FP
                )
            }
            false
        }
    }

}





fun checkPhoneStatePermissionReg(activity: Activity): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // Do something for Android 10 and above versions
        return if (ActivityCompat.checkSelfPermission(
                activity,

                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_PHONE_STATE
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    REQUEST_READ_PHONE_STATE_REG
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    REQUEST_READ_PHONE_STATE_REG
                )
            }
            false
        }
    } else {
        // do something for phones running an SDK before lollipop
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_PHONE_STATE
                )
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    REQUEST_READ_PHONE_STATE_REG
                )
            } else {
                requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    REQUEST_READ_PHONE_STATE_REG
                )
            }
            false
        }
    }

}


/*Manifest.permission.ACCESS_FINE_LOCATION,
Manifest.permission.ACCESS_COARSE_LOCATION,
Manifest.permission.ACTIVITY_RECOGNITION,
Manifest.permission.CAMERA,
Manifest.permission.SEND_SMS,
Manifest.permission.WRITE_EXTERNAL_STORAGE,
Manifest.permission.READ_EXTERNAL_STORAGE,
//                                Manifest.permission.READ_PHONE_STATE,
Manifest.permission.ACCESS_BACKGROUND_LOCATION*/

fun checkAndRequestPermissions(activity: Activity): Boolean {
    try {
        val permissionFineLocation = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        val permissionAccessCoarseLoc = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        val permissionActivityRecognition = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACTIVITY_RECOGNITION)
        val permissionCamera = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
        val permissionSendSms = ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS)
        val permissionWriteExternalSt = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionReadExternalSt = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        val permissionReadPhoneState = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
        val permissionAccessBGLocation = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)

        val listPermissionsNeeded: MutableList<String> = ArrayList()

        if (permissionFineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissionAccessCoarseLoc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (permissionActivityRecognition != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (permissionSendSms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS)
        }
        if (permissionWriteExternalSt != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionReadExternalSt != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (permissionAccessBGLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(
                activity,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_REQ_PERMISSIOM
            )
            return false
        }
    } catch (ignored: Exception) {
    }
    return true
}




fun kmph_to_mps(kmph: Float): Float {
    return (0.277778 * kmph).toFloat()
}


// function to convert speed
// in m/sec to km/hr
fun mps_to_kmph(mps: Float): Float {
    return (3.6 * mps).toFloat()
}
