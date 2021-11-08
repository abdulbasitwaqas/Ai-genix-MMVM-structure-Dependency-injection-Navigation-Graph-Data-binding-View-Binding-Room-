package com.jsbl.genix.network

//import com.jsbl.genix.di.modules.API_SERVIE_LOCAL
//import com.jsbl.genix.di.modules.typeOfService
import android.app.Application
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.StatsModel
import com.jsbl.genix.di.components.DaggerAPIComponent
import com.jsbl.genix.di.modules.AppModule
import com.jsbl.genix.model.GetStatsFeedBackModel
import com.jsbl.genix.model.ShareWithFriendsModel
import com.jsbl.genix.trips.TripItem
import com.jsbl.genix.model.getTripsModel
import com.jsbl.genix.model.profileManagement.*
import com.jsbl.genix.model.registration.*
import com.jsbl.genix.trips.TripItemResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import java.io.File
import javax.inject.Inject


class LocalService(val application: Application) {


    @Inject
//    @field:typeOfService(API_SERVIE_LOCAL)
    lateinit var localApi: LocalApi

    init {
        DaggerAPIComponent.builder().appModule(AppModule(application)).build().injectAPI(this)

    }

    fun getOtp(otpX: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.getOtp(otpX)
    }

    fun getVerifyOtpLogin(otpX: SendEncryptionRequest): Call<SendEncryptionRequest?> {
        return localApi.verifyOtpRegg(otpX)
    }

    fun getVerifyOtpReg(otpX: SendEncryptionRequest): Call<Unit> {
        return localApi.verifyOtpReg(otpX)
    }

    fun validatePassword(password: SendEncryptionRequest): Call<Unit> {
        return localApi.validatePassword(password)
    }

    fun enterData(registration: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.registerUser(registration)
    }

    fun updateUser(registration: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.updateUser(registration)
    }

    fun uploadImage(file: File, callFrom: Int): Call<SendEncryptionRequest> {
        val requestFile: RequestBody =
            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData(
            "image",
            file.getName(), requestFile
        )
        return localApi.uploadProfileImage(body, callFrom)
    }



    fun uploadCNICImages(front: File, backFile: File, callFrom: Int): Call<SendEncryptionRequest> {
        val requestFrontFile: RequestBody =
            front.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val bodyFront = MultipartBody.Part.createFormData(
            "image",
            front.getName(), requestFrontFile
        )
        val requestBackFile: RequestBody =
            backFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val bodyBack = MultipartBody.Part.createFormData(
            "image",
            backFile.getName(), requestBackFile
        )
        return localApi.uploadCNICPics(bodyFront, bodyBack, callFrom)
    }



    fun uploadPofileImages(front: File, callFrom: Int): Call<SendEncryptionRequest> {
        val requestFrontFile: RequestBody =
            front.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val bodyFront = MultipartBody.Part.createFormData(
            "image",
            front.getName(), requestFrontFile
        )

        return localApi.uploadProfileCNICPic(bodyFront, callFrom)
    }



    fun loginUser(login: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.loginUser(login)
    }

    fun getDropDown(): Call<SendEncryptionRequest> {
        return localApi.getDropDownValues()
    }

    fun getQuestions(): Call<SendEncryptionRequest> {
        return localApi.getQuestions()
    }
    fun getUserGamesGET(userid: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.getUserGamesGET(userid)
    }

    fun setCarDetails(carDetail: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.addCarDetails(carDetail)
    }



    fun addFeedback(feedBack: SendEncryptionRequest): Call<ResponseBody> {
        return localApi.addFeedback(feedBack)
    }

    fun addAreaOfInterest(areaOfInterest: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.addAreaOfInterest(areaOfInterest)
    }
    fun getCustomerInterest(areaOfInterest: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.getCustomerInterests(areaOfInterest)
    }
    fun getCustomerInterestsById(areaOfInterest: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.getCustomerInterestsById(areaOfInterest)
    }

    fun addInterest(areaOfInterest: List<Interest>): Call<Interest> {
        return localApi.addInterest(areaOfInterest)
    }


    fun resetPassword(requestResetPassword: SendEncryptionRequest): Call<Any?> {
        return localApi.resetPassword(requestResetPassword)
    }

    fun setDefaultCar(id: Long, customerId: Long): Call<Void?> {
        return localApi.setDefaultCar(id, customerId)
    }

    fun setDeleteCar(id: SendEncryptionRequest): Call<Unit> {
        return localApi.deleteCar(id)
    }

    //encryption basit
    fun getALLTrips(sendEncryptionRequest : SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.allTrips(sendEncryptionRequest)
    }
    fun getALLTrip(sendEncryptionRequest : SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.allTrip(sendEncryptionRequest)
    }
    fun getTripFeedBack(sendEncryptionRequest : SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.getTripFeedback(sendEncryptionRequest)
    }

    fun getLatestFiveTrips(sendEncryptionRequest : SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.getLatestFiveTrips(sendEncryptionRequest)
    }

    fun getStats(sendEncryptionRequest : SendEncryptionRequest): Call<SendEncryptionRequest>{
        return localApi.allStats(sendEncryptionRequest)
    }
    fun getStatsFeedBack(sendEncryptionRequest : SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.getStatsFeedBack(sendEncryptionRequest)
    }


    fun addFeedbackQuestions( userID: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.addFeedbackQuestions(userID)
    }

/*    feedback post api
    fun addFeedbackQuestions(getCustomerFeedbackModel: GetCustomerFeedbackModel): Call<List<FeedBackQuestionsModel>> {
        return localApi.addFeedbackQuestions(getCustomerFeedbackModel)
    }*/
  /*  fun getTrips(scopeToken: String, span: String): Call<ArrayList<getTripsModel>> {
        return localApi.getTrips(scopeToken, span)
    }*/

    // zaheer: Redeem section

    fun getRedeemCart(userId: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.getRedeemCart(userId)
    }

    fun getAvailableRedeem(): Call<SendEncryptionRequest> {
        return localApi.getAvailableRedeems()
    }

    fun getMyRedeems(userId: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.getMyRedeems(userId)
    }

    fun updateRedeemCart(updateRedeemCartRequest: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.updateRedeemCart(updateRedeemCartRequest)
    }

    fun getRedeemReward(redeemRewardRequest: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.getRedeemReward(redeemRewardRequest)
    }

    fun checkoutRedeem(checkOutRedeemRequest: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.checkoutRedeem(checkOutRedeemRequest)
    }

    fun shareWithFriends(shareWithFriend: SendEncryptionRequest): Call<Any?> {
        return localApi.addShareWithFriends(shareWithFriend)
    }
    fun verifyShareWithFriends(shareWithFriend: SendEncryptionRequest): Call<SendEncryptionRequest> {
        return localApi.verifyShareWithFriends(shareWithFriend)
    }
    fun getUserGames(sendEncryptionRequest: SendEncryptionRequest): Call<Unit> {
        return localApi.getUserGames(sendEncryptionRequest)
    }

    fun getHelp(): Call<SendEncryptionRequest> {
        return localApi.getHelp()
    }

}