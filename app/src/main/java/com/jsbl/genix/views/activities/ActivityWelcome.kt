package com.jsbl.genix.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivityWelcomeBinding
import com.jsbl.genix.utils.APP_TAG
import com.jsbl.genix.utils.callBacks.OnViewClickListener
import com.jsbl.genix.utils.extensions.selectorWelcomeCardButton
import com.jsbl.genix.utils.extensions.selectorWelcomeCardButtonEM
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.utils.logD
import com.jsbl.genix.utils.services.actionForService
import com.jsbl.genix.utils.services.checkAllPermission
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.scottyab.rootbeer.RootBeer
import java.io.File


class ActivityWelcome : AppCompatActivity(), OnViewClickListener ,View.OnTouchListener{

    private lateinit var binding: ActivityWelcomeBinding

    companion object {
        const val REQUEST_READ_PHONE_STATE = 25
    }

    private var selectedButton: CardView? = null
    var nexActivityIntent: Intent? = null

    var mkey:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.onClickListener = this
//        checkAllPermission(this)
        binding.btnNewCustomer.outerCard.setOnTouchListener(this)
        binding.btnExistingMember.outerCard.setOnTouchListener(this)
//        cropImage()
        mkey=Constaint.mKey
    }
/*    fun cropImage() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAutoZoomEnabled(false)
            .setAllowFlipping(false)
            .setAllowRotation(false)
            .setActivityTitle("Crop Profile Image")
            .setActivityMenuIconColor(Color.RED)
            .start(this)
    }*/

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.btn_new_customer -> {
                nexActivityIntent = Intent(this@ActivityWelcome, RegistrationRevampActivity::class.java)
                startActivity(nexActivityIntent)
            }
            R.id.btn_existing_member -> {
                nexActivityIntent = Intent(this@ActivityWelcome, ActLogin::class.java)
                startActivity(nexActivityIntent)
            }
          /*  R.id.btnWelcomeContinue -> {
//                showShort(this, "Existing Member Clicked")
//                startActivity(Intent(this@Welcome, MainActivity::class.java))
                if (selectedButton == null) {
                    showShort(this@Welcome, "Kindly select an Option")
                } else {
                    startActivity(nexActivityIntent)
                }
//                selectedButton = selectCardButton(this as CardView, selectedButton)

            }*/
        }
    }


    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_READ_PHONE_STATE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
            ActivityMain.REQUEST_CODE_FINE_LOCATION -> {
                if (checkAllGranted(grantResults)) {
                    logD(APP_TAG,"all Granted")


                }

            }
            else -> {
            }
        }
    }
    private fun checkAllGranted(grantResults: IntArray): Boolean {
        var granted = true
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                granted = false
                break
            }
        }
        return granted
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (v?.id) {
            R.id.btn_new_customer -> {
                if (event != null) {
                    selectorWelcomeCardButton(v as CardView, event!!)
                }
            }
            R.id.btn_existing_member -> {
                if (event != null) {
                    selectorWelcomeCardButtonEM(v as CardView, event!!)
                }
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        if (deviceRootCheck() || isRooted()) {
            showOnlyAlertMessage(this,"UnAuthorized Alert","This device is rooted. You can't use this app."){
                finish()
            }
        }
    }

    private fun deviceRootCheck(): Boolean {
        val RootBeer: RootBeer
        RootBeer = RootBeer(this)
        return if (RootBeer.isRooted) {
            true
        } else {
            false
        }
    }
    fun findBinary(binaryName: String): Boolean {
        var found = false
        if (!found) {
            val places = arrayOf(
                "/sbin/", "/system/bin/", "/system/xbin/",
                "/data/local/xbin/", "/data/local/bin/",
                "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"
            )
            for (where in places) {
                if (File(where + binaryName).exists()) {
                    found = true
                    break
                }
            }
        }
        return found
    }

    private fun isRooted(): Boolean {
        return findBinary("su")
    }
}