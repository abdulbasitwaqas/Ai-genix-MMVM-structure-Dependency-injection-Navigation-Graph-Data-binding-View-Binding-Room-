package com.jsbl.genix.di.components

import android.app.Activity
import android.app.Service
import com.jsbl.genix.di.modules.PrefsModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Muhammad Ali on 03-Aug-20.
 * Email muhammad.ali9385@gmail.com
 */
@Singleton
@Component(modules = arrayOf(PrefsModule::class))
interface PreferenceComponent {
    fun getPrefs(activity: Activity)
    fun getPrefsForService(service: Service)
}