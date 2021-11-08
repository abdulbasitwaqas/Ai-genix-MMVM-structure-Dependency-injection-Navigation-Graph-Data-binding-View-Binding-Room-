package com.jsbl.genix.views.fragments

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jsbl.genix.R
import com.jsbl.genix.databinding.AltFragmentDashboard2Binding
import com.jsbl.genix.databinding.DetailLayoutBinding
import com.jsbl.genix.databinding.DialogSendSmsBinding
import com.jsbl.genix.model.DogBreed
import com.jsbl.genix.model.DogPalette
import com.jsbl.genix.model.SmsInfo
import com.jsbl.genix.utils.RequestHandler
import com.jsbl.genix.viewModel.DetailViewModel
import com.jsbl.genix.viewModel.MainHomeViewModel
import com.jsbl.genix.views.activities.ActivityMain

/**
 * A simple [Fragment] subclass.
 */

class DetailFragment : BaseFragment<DetailViewModel, DetailLayoutBinding>(
    DetailViewModel::class.java
) {
    private var uuid: String = ""

    private var sendSmsStarted = false

    private var currentDog: DogBreed? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //retrieving Argument
        arguments?.let {
            uuid = DetailFragmentArgs.fromBundle(it).uuid
            viewModel.setDetails(uuid)
//        view.dog
            observeDetails()
        }

    }

    fun observeDetails() {
        viewModel.dogObj.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.dog = it
                currentDog = it
                it.image?.let {
                    setupBackgroundColor(it)
                }
            }
        })
    }

    fun setupBackgroundColor(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate {
                            val color = it?.vibrantSwatch?.rgb ?: 0
                            val customPalette = DogPalette(color)
                            binding.palette = customPalette
                        }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSend -> {
                sendSmsStarted = true
                (activity as ActivityMain).checkSmsPermission()
            }
            R.id.actionShare -> {

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Send Message")
                    putExtra(Intent.EXTRA_TEXT, "${currentDog?.breed}")
                    putExtra(Intent.EXTRA_STREAM, currentDog?.image)

                }
                startActivity(Intent.createChooser(shareIntent, "Share with"))
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View, obj: Any) {
    }

    override fun onLoading(obj: RequestHandler) {
    }

    override  fun onSuccess(obj: RequestHandler) {
    }

    override fun onError(obj: RequestHandler) {
    }

    fun onPermissionResult(permissionResult: Boolean) {

        if (sendSmsStarted && permissionResult) {
            context?.let {
                val smsInfo = SmsInfo("", "${currentDog?.breed}", "${currentDog?.image}")
                val dialogBinding = DataBindingUtil.inflate<DialogSendSmsBinding>(
                    layoutInflater,
                    R.layout.dialog_send_sms,
                    null,
                    false
                )
                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("Send Sms") { dialog, which ->

                        if (!dialogBinding.to.text.isNullOrEmpty()) {
                            smsInfo.to = dialogBinding.to.text.toString()
                            sendSms(smsInfo)
                        }
                    }.setNegativeButton("Cancel") { dialog, which ->

                    }.show()
                dialogBinding.smsInfo = smsInfo
            }

        }
    }

    fun sendSms(smsInfo: SmsInfo) {

        val intent = Intent(context, ActivityMain::class.java).putExtra("show_referance_dialog",false)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(smsInfo.to, null, smsInfo.text, pendingIntent, null)
    }

    override fun getLayoutRes(): Int {
        return R.layout.detail_layout
    }
}

