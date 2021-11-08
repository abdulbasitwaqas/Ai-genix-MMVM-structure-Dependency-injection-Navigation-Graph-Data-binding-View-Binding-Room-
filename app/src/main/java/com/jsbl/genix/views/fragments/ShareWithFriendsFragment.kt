package com.jsbl.genix.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.flurry.sdk.it
import com.jsbl.genix.BuildConfig
import com.jsbl.genix.R
import com.jsbl.genix.databinding.AltFragmentProfileDetailsBinding
import com.jsbl.genix.databinding.FragmentShareWithFriendsBinding
import com.jsbl.genix.utils.RequestHandler
import com.jsbl.genix.utils.callBacks.OnViewClickListener
import com.jsbl.genix.utils.getProfilePercent
import com.jsbl.genix.utils.logD
import com.jsbl.genix.utils.showShort
import com.jsbl.genix.viewModel.PersonalDetailViewModel
import com.jsbl.genix.views.activities.setAccountProgress
import java.util.*


class ShareWithFriendsFragment :
    BaseFragment<PersonalDetailViewModel, FragmentShareWithFriendsBinding>(
        PersonalDetailViewModel::class.java
    ) {
//    lateinit var binding:FragmentShareWithFriendsBinding

    /*   override fun onCreateView(
           inflater: LayoutInflater, container: ViewGroup?,
           savedInstanceState: Bundle?
       ): View? {
           // Inflate the layout for this fragment
           binding=DataBindingUtil.inflate(layoutInflater,R.layout.fragment_share_with_friends,container,false)
           return binding.root
           //return inflater.inflate(R.layout.fragment_share_with_friends, container, false)
       }*/
    var randomNumber=""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.onClickListener = this

        observeDetails()
        viewModel.fetchFromDatabase()



//        binding.actionBarCustom.drawerImage.setOnClickListener { requireActivity().onBackPressed() }
    }

    companion object {

        /* @JvmStatic
         fun newInstance(param1: String, param2: String) =
             ShareWithFriendsFragment().apply {
                 arguments = Bundle().apply {
                     putString(ARG_PARAM1, param1)
                     putString(ARG_PARAM2, param2)
                 }
             }*/
    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.btnShare -> {
                showPDialog()
                viewModel.shareWithFriendss = true
                val r = Random()
                val numbers = 100000 + (r.nextFloat() * 899900).toInt()
                randomNumber = numbers.toString()

                viewModel.shareWithFriend(randomNumber.toString())
            }
            R.id.drawerImage -> {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onLoading(obj: RequestHandler) {

    }

    override fun onSuccess(obj: RequestHandler) {
        dismissDialog()
        if (viewModel.shareWithFriendss) {
            shareLink()
        }
    }

    override fun onError(obj: RequestHandler) {
    }

    fun shareLink() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            var shareMessage =
                "\n Reference Code: ${randomNumber} \nLet me recommend you this application\n"
            shareMessage =
                """
                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                
                
                """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            //e.toString();
        }
    }

    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.let {
                logD("**percentage", "" + it.percentage)

                binding.actionBarCustom.accountTitle.visibility = View.INVISIBLE
                binding.actionBarCustom.pBar.visibility = View.GONE
//                if (it.percentage == null) {
//                    setAccountProgress(getProfilePercent(it))
//
//                } else {
//                    setAccountProgress(it.percentage!!)
//                }
            }
        })
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_share_with_friends
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
        logD("**percentage", "" + percentage)
        binding.actionBarCustom.pBar.setProgress(percentage)
    }


    fun createRandomReferralCode(): String {

        val r = Random()
        val numbers = 100000 + (r.nextFloat() * 899900).toInt()
        return numbers.toString()
    }
}