package com.jsbl.genix.views.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jsbl.genix.R
import com.jsbl.genix.databinding.AltFragmentFeedbackBinding
import com.jsbl.genix.model.FeedBackQuestionsModel
import com.jsbl.genix.model.UserFeedbackQuestionModel
import com.jsbl.genix.model.profileManagement.*
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.registration.LoginMdl
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.viewModel.FeedbackViewModel
import com.jsbl.genix.views.adapters.CarsItemAdapter
import com.jsbl.genix.views.adapters.FeedBackQuestionAdapter
import kotlinx.android.synthetic.main.alt_fragment_profile_details.*
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [Feedback.newInstance] factory method to
 * create an instance of this fragment.
 */
class Feedback : FeedBackQuestionAdapter.feedbackInterface,
    BaseFragment<FeedbackViewModel, AltFragmentFeedbackBinding>(
        FeedbackViewModel::class.java
    ) {
    var customerX: CustomerX? = null
    val overallExperience = ""
    val registrationExperience = ""
    private lateinit var feedBackQuestionAdapter: FeedBackQuestionAdapter
    var userFeedbackQ = UserFeedbackQuestionModel()
    var userFeedbackQuestionModelArrayList: ArrayList<UserFeedbackQuestionModel> = ArrayList()

    var questionID: Int = -1
    var questionRating: Int = -1


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        clickListeners()
        observeDetails()
        binding.onClickListener = this
        viewModel.fetchFromDatabase()
    }



    fun observeDetails() {
        showPDialog()
        viewModel.customer.observe(viewLifecycleOwner, Observer {
            it?.let {
                customerX = it

                viewModel.feedQuestionsss
//                val getFeedbackQuestionModel = GetCustomerFeedbackModel( it.iD)
                viewModel.getFeedBackQues(""+it.iD)

                if (it.feedBacks != null) {
                    if (it.feedBacks!!.remarks != null) {

                    }
                }


                setAccountProgress(getProfilePercent(it))
            }
        })
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
        binding.actionBarCustom.pBar.setProgress(percentage)
        /*val params =
            binding.accountProgressLayout.ivDropdown.getLayoutParams() as ConstraintLayout.LayoutParams
        params.horizontalBias =
            percentage.toFloat() / 100f // here is one modification for example. modify anything else you want :)
        binding.accountProgressLayout.ivDropdown.setLayoutParams(params)
        binding.accountProgressLayout.tvProgressDigits.text = "$percentage%"*/
    }

    private fun validateRegistrationNumber(): Boolean {

        return if (binding.edFeedback.et.text.toString().trim().isNotEmpty()) {
            binding.edFeedback.til.error = null
            true
        } else {
            binding.edFeedback.til.error = "Please Enter Feedback"
            false
        }
    }


    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.btnFeedback -> {
                if (validateRegistrationNumber()
                ) {

                    showPDialog()
                    viewModel.addFeedback(
                        PostFeedBack(
                            customerX!!.iD,
                            binding.edFeedback.et.text.toString().trim(),
                            userFeedbackQuestionModelArrayList
                        )
                    )
                }
            }
            R.id.drawerImage -> {
                requireActivity().onBackPressed()

            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
    }

    override fun onSuccess(obj: RequestHandler) {

        if (obj.any is java.util.ArrayList<*>) {
            val feedBackQuestionList = obj.any as java.util.ArrayList<FeedBackQuestionsModel>

//                val feedbackQues = obj.any as FeedBackQuestionsModel

            binding.feedbackQuesRV.layoutManager = LinearLayoutManager(context)
//                binding..layoutManager = LinearLayoutManager(context)
            logD("**qustionsSize", "" + feedBackQuestionList.size)

            feedBackQuestionAdapter =
                FeedBackQuestionAdapter(feedBackQuestionList, requireContext(), this)
            binding.feedbackQuesRV.adapter = feedBackQuestionAdapter

            if (feedBackQuestionList.size < 1){
                binding.scroller.visibility = View.GONE
            }else{
                binding.scroller.visibility = View.VISIBLE
            }

            if (feedBackQuestionList.size > 0 &&
                !feedBackQuestionList.get(0).remarks.equals("null") &&
                feedBackQuestionList.get(0).remarks != null)
                binding.edFeedback.et.setText("${feedBackQuestionList.get(0).remarks}")

        }

//        }

        else {
            if (viewModel.getCustomer().percentage == null) {
                setAccountProgress(getProfilePercent(viewModel.getCustomer()))
            } else {
                setAccountProgress(viewModel.getCustomer().percentage!!)
            }
//                        showShort(requireContext(), "")
            showOnlyAlertMessage(
                context = requireContext(),
                title = "Feedback",
                msg = "Thanks for your feedback",
                onPositiveClick = {
                    requireActivity().onBackPressed()
                }
            )
        }


    }

    override fun onError(obj: RequestHandler) {
    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_feedback
    }

    override fun feedback(id: Int, rating: Int) {
        questionID = id
        questionRating = rating
        userFeedbackQ = UserFeedbackQuestionModel("" + id, rating)

        for (i in 0 until userFeedbackQuestionModelArrayList.size) {
            if (userFeedbackQ.fQID.equals(userFeedbackQuestionModelArrayList.get(i).fQID)) {
                userFeedbackQuestionModelArrayList.remove(userFeedbackQuestionModelArrayList.get(i))
                break
            }
        }
        userFeedbackQuestionModelArrayList.add(userFeedbackQ)
    }


}