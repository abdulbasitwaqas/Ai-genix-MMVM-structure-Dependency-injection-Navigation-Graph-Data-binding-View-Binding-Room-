package com.jsbl.genix

import android.app.Application
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.multidex.MultiDex
import com.jsbl.genix.BuildConfig.*
import com.jsbl.genix.utils.services.GenixService
import com.jsbl.genix.views.activities.ActivityMain
import com.scope.mhub.app.SCPSmartDriveManager
import com.scope.portalapiclient.PortalApi
import com.scope.portalapiclient.PortalApiConfig
import com.scope.smartdrivedemo.FileLoggingTree
import timber.log.Timber
import java.io.File
import java.io.IOException

/**
 * Created by Muhammad Ali on 01-Aug-18.
 */
class Genix : Application() {
    override fun onCreate() {
        super.onCreate()
        /* Fabric.with(this, new Crashlytics());
        FirebaseAnalytics.getInstance(this);*/

        Timber.plant(FileLoggingTree(applicationContext, getDeviceDescription()))
        initPortalApi()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(
            true
        ) //add this
        //crashkar();



    }

    private fun getDeviceDescription(): String {
        val deviceId: String = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

        val sb = StringBuilder()
            .append("DEVICE ID:").append(deviceId).append("\n")
            .append("DEVICE MANUFACTURER:").append(Build.MANUFACTURER).append(",")
            .append("DEVICE MODEL:").append(Build.MODEL).append("\n")
            .append("APP VERSION:").append(VERSION_NAME).append("(").append(VERSION_CODE).append(")").append("\n")
            .append("ANDROID:").append(Build.VERSION.SDK_INT).append("\n")
            .append("APP ID:").append(APPLICATION_ID).append("\n\n")

        return sb.toString()
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }

    private fun initPortalApi() {
        val config = PortalApiConfig("https://aigenix-stg.connectedcar360.net/", "AiGenix", ""+BuildConfig.VERSION_NAME)
        PortalApi.init(applicationContext, config, listOf())


    }

    override fun onTerminate() {
//        SCPSmartDriveManager.getInstance(this).disconnectFromCCFramework()
        super.onTerminate()
    }
}
