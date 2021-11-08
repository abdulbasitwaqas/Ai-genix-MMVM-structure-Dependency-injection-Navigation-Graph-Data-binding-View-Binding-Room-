package com.jsbl.genix.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jsbl.genix.R
import com.jsbl.genix.databinding.*
import com.jsbl.genix.model.redeem.RedeemRewardModel
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.*
import com.jsbl.genix.viewModel.RedeemViewModel
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [RedeemRewardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RedeemRewardFragment : BaseFragment<RedeemViewModel, AltFragmentRedeemRewardBinding>(
    RedeemViewModel::class.java
) {
    private var customerX = CustomerX()
    private var expiryDate = ""
    private var redeemId = -1

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        observeDetails()
//        setRequestHandler()
        //TODO

        binding.onClickListener = this
        viewModel.fetchFromDatabase()
        arguments?.let {
            expiryDate = it.getString("expiry_date")!!
            redeemId = it.getInt("redeem_id")!!

            val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())
            val date = simpleDateFormat.parse(expiryDate)
            simpleDateFormat.applyPattern("yyyy-MM-dd")
            val timeLeft = getDaysCount(Date(), date)
            if (timeLeft > 0) {
                binding.duratoin.text =
                    if (timeLeft > 1) "$timeLeft DAYS LEFT" else "$timeLeft DAY LEFT"
            } else {
                binding.duratoin.text = getString(R.string.now_is_the_time)
                binding.btnNext.visibility = View.VISIBLE
                binding.btnCLose.visibility = View.GONE
            }
        }
        binding.actionBarCustom.accountTitle.setText(getString(R.string.reward))
        binding.actionBarCustom.title.visibility = View.GONE
        binding.actionBarCustom.pBar.visibility = View.GONE
        binding.actionBarCustom.bottomView.visibility = View.GONE
    }


    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner,
            Observer<CustomerX> { t ->
                t?.let {
                    customerX = it

                }
            })
    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.drawerImage -> {
                requireActivity().onBackPressed()
            }

            R.id.btnCLose -> {
                requireActivity().onBackPressed()
            }

            R.id.btnNext -> {
                viewModel.getRedeemReward(customerX.iD.toString(), redeemId.toString())
            }
        }
    }


    override fun onLoading(obj: RequestHandler) {
        Log.d(APP_TAG, "onLoading: ")
    }

    override fun onSuccess(obj: RequestHandler) {
        if (obj.any is RedeemRewardModel) {
            val rewardModel = obj.any as RedeemRewardModel
            binding.duratoin.text = getString(R.string.congrats)
            binding.startingTV.text = getString(R.string.you_won)
            binding.btnNext.visibility = View.GONE
            binding.btnCLose.visibility = View.VISIBLE
            binding.silverCoating.visibility = View.GONE
            binding.endingTV.visibility = View.GONE
            binding.scratchText.text = rewardModel.winningAmount.toString()+" PKR"
        }
    }

    override fun onError(obj: RequestHandler) {
        Log.d(APP_TAG, "onError: ")
        if (obj.any is Response<*>) {
            val responseError = obj.any as Response<*>
            if (responseError.code() == 400) {
                binding.duratoin.text = getString(R.string.hard_luck)
                binding.startingTV.text = getString(R.string.better_luck)
                binding.btnNext.visibility = View.GONE
                binding.btnCLose.visibility = View.VISIBLE
            }
        }
    }

    override fun getLayoutRes(): Int {
        val root = R.layout.alt_fragment_redeem_reward

        return root
    }
}