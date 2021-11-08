package com.jsbl.genix.views.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.jsbl.genix.R
import com.jsbl.genix.utils.APP_TAG
import com.jsbl.genix.utils.SharePreferencesHelper
import com.jsbl.genix.utils.logD
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_SPLASH_FLOW
import com.scope.mhub.utils.TripManagementHelper


class ActivitySplash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        dontWaitJustStart()
//        TripManagementHelper.getInstance(this).startHelper()

    }

    fun dontWaitJustStart() {
        logD(APP_TAG, "Don't wait just start")
        if (SharePreferencesHelper(this).getAuth().isEmpty()) {
            Handler().postDelayed({
//            startActivity(Intent(this@Splash, ProfileManagement::class.java))
                startActivity(Intent(this@ActivitySplash, ActivityWelcome::class.java))
                finish()
            }, 3000)
        } else {
            Handler().postDelayed({
//            startActivity(Intent(this@Splash, ProfileManagement::class.java))
                val intent = Intent(this@ActivitySplash, ActivityMain::class.java).putExtra("show_referance_dialog",false
                )
                intent.putExtra(INTENT_SPLASH_FLOW,true)

                startActivity(intent)
                finish()
            }, 3000)
        }
    }



}