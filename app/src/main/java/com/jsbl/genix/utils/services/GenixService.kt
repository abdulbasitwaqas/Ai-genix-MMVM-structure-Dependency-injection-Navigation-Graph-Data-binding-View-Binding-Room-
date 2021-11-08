package com.jsbl.genix.utils.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.jsbl.genix.R
import com.jsbl.genix.di.components.DaggerServiceComponent
import com.jsbl.genix.di.modules.AppModule
import com.jsbl.genix.di.modules.CONTEXT_APP
import com.jsbl.genix.di.modules.typeOfContext
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.eventBus.ObjectDepartureHelper
import com.jsbl.genix.views.activities.ActivityMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

class GenixService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false

    @Inject
    @field:typeOfContext(CONTEXT_APP)
    lateinit var prefsHelper: SharePreferencesHelper

    private lateinit var notification: Notification
    var counter = 0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationPermission by Delegates.notNull<Int>()
    private var activityRecognitionPermission by Delegates.notNull<Int>()
    private var destinationLocation: Location? = null
    private var distanceBetween: Float? = 0F
    private var tripStarted = false
    private lateinit var objectDepartureHelper: ObjectDepartureHelper

    override fun onBind(intent: Intent): IBinder? {
        logD(APP_TAG, "Some component want to bind with the service")
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logD(APP_TAG, "onStartCommand executed with startId: $startId")
//        prefsHelper = SharePreferencesHelper.invoke(this)
        DaggerServiceComponent.builder().appModule(AppModule(app = application)).build()
            .injectIntoService(this)
        if (intent != null) {
            val action = intent.action
            logD(APP_TAG, "using an intent with action $action")

            when (action) {
                Actions.START.name -> startService()
                Actions.STOP.name -> stopService()
                else -> logD(APP_TAG, "This should never happen. No action in the received intent")
            }
        } else {
            logD(
                APP_TAG, "with a null intent. It has been probably restarted by the system."
            )
        }

        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
//        objectDepartureHelper = ObjectDepartureHelper(this)
        fusedLocationRequestSetup(setLocationListener = true)
        logD(APP_TAG, "The service has been created".toUpperCase())
        notification = createNotification(getString(R.string.tripsLocationServiceLabel))
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        logD(APP_TAG, "The service has been destroyed".toUpperCase())
//        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, GenixService::class.java).also {
            it.setPackage(packageName)
        };
        val restartServicePendingIntent: PendingIntent =
            PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        applicationContext.getSystemService(Context.ALARM_SERVICE);
        val alarmService: AlarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmService.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 1000,
            restartServicePendingIntent
        );
    }

    @SuppressLint("MissingPermission")
    private fun startService() {
        if (isServiceStarted) return
        logD(APP_TAG, "Starting the foreground service task")
//        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
        isServiceStarted = true
        prefsHelper.updateForegroundServiceState(ServiceState.STARTED)

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GenixService::lock").apply {
                    acquire()
                }
            }

        counter = 0
        // we're starting a loop in a coroutine
        GlobalScope.launch(Dispatchers.IO) {
            while (isServiceStarted) {
                launch(Dispatchers.IO) {
//                    pingFakeServer()

                    getCurrentLocation()
                    /*  notification = createNotification(counter)
                      startForeground(1, notification, FOREGROUND_SERVICE_TYPE_LOCATION)*/
                }
                delay(1 * 10 * 1000)
            }
            logD(APP_TAG, "End of the loop for the service")
        }
    }

    private fun stopService() {
        logD(APP_TAG, "Stopping the foreground service")
//        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            logD(APP_TAG, "Service stopped without being started: ${e.message}")
        }
        isServiceStarted = false
        prefsHelper.updateForegroundServiceState(ServiceState.STOPPED)
    }

    private fun pingFakeServer() {
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmZ")
        val gmtTime = df.format(Date())

        val deviceId = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val json =
            """
                {
                    "deviceId": "$deviceId",
                    "createdAt": "$gmtTime"
                }
            """
        try {
            /*    Fuel.post("https://jsonplaceholder.typicode.com/posts")
                    .jsonBody(json)
                    .response { _, _, result ->
                        val (bytes, error) = result
                        if (bytes != null) {
                            logD("[response bytes] ${String(bytes)}")
                        } else {
                            logD("[response error] ${error?.message}")
                        }
                    }*/
        } catch (e: Exception) {
            logD(APP_TAG, "Error making the request: ${e.message}")
        }
    }

    private fun createNotification(msg: String): Notification {
        val notificationChannelId = "GENIX FOREGROUND CHANNEL"

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                notificationChannelId,
                "genix notifications channel",
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = "Genix Description"
                it.enableLights(false)
                it.enableVibration(false)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent =
            Intent(this, ActivityMain::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val builder: Notification.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
                this,
                notificationChannelId
            ) else Notification.Builder(this)

        return builder
            .setContentTitle(getString(R.string.app_name))
            .setContentText(msg)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }


    //location Related Methods

    private fun fusedLocationRequestSetup(setLocationListener: Boolean) {
        locationRequest = LocationRequest()
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
        if (setLocationListener)
            enableLocationListener()
    }

    private fun enableLocationListener() {
        if (!this@GenixService::fusedLocationClient.isInitialized) {
            return
        }
        locationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        activityRecognitionPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        if (locationPermission == PackageManager.PERMISSION_GRANTED && activityRecognitionPermission == PackageManager.PERMISSION_GRANTED) { // Request location updates and when an update is
            // received, store the location in Firebase
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location: Location = locationResult.getLastLocation()
                        if (location != null) {
                            /*latitude = location.latitude
                            longitude = location.longitude*/
                            logD(
                                APP_TAG,
                                "location update :: Lat : ${location.latitude} \nLng : ${location.longitude}"
                            )
                        }
                    }
                },
                null
            )
        }
    }

    private fun getCurrentLocation() {
        if (!this@GenixService::fusedLocationClient.isInitialized) {
            fusedLocationRequestSetup(true)
        }
        locationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        activityRecognitionPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        if (locationPermission == PackageManager.PERMISSION_GRANTED && activityRecognitionPermission == PackageManager.PERMISSION_GRANTED) { // Request location updates and when an update is
            // received, store the location in Firebase
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    if (it == null) return@addOnSuccessListener
                    if (destinationLocation != null) {
                        distanceBetween = it.distanceTo(destinationLocation)
                        /*showLong(
                            this@GenixService,
                            "Last Known Location::\ncounter : $counter \nLat : ${it.latitude}\nLng : ${it.longitude}\nDistance : $distanceBetween meters\nSpeed : ${mps_to_kmph(
                                it.speed
                            )} km/h"
                        )*/
                        /*if (distanceBetween!! > 10) {
                            if (!tripStarted) {
                                tripStarted = true
                                notification =
                                    createNotification("Trip Started at 1 meter/sec speed")
                                startForeground(1, notification, FOREGROUND_SERVICE_TYPE_LOCATION)
                            }
                            counter = 0
                        } else {
                            if (counter >= 3) {
                                if (tripStarted) {
                                    tripStarted = false
                                    notification =
                                        createNotification("Trip End, Have a nice day!")
                                    startForeground(
                                        1,
                                        notification,
                                        FOREGROUND_SERVICE_TYPE_LOCATION
                                    )
                                }
                            } else {
                                counter++
                            }

                        }*/

                    }
                    destinationLocation = it
                    logD(
                        APP_TAG,
                        "Last Known Location::\ncounter : $counter\nLat : ${it.latitude}\nLng : ${it.longitude}\n" +
                                "Distance : $distanceBetween meters\nSpeed : ${
                                    mps_to_kmph(
                                        it.speed
                                    )
                                } km/h"
                    )
                    // Got last known location. In some rare situations this can be null.
                }
        }
    }
}