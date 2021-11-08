package com.jsbl.genix.network

import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.model.profileManagement.*
import com.jsbl.genix.model.registration.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Muhammad Ali on 05-May-20.
 * Email muhammad.ali9385@gmail.com
 */
interface LocalApi {

    //registration Apis
    @POST("Account/GetOTP")
    fun getOtp(@Body otpX: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("Account/VerifyOTP")
    fun verifyOtpRegg(@Body otpX: SendEncryptionRequest): Call<SendEncryptionRequest?>

    @POST("Account/VerifyOTP")
    fun verifyOtpReg(@Body otpX: SendEncryptionRequest): Call<Unit>

    @POST("Account/ValidatePassword")
    fun validatePassword(@Body otpX: SendEncryptionRequest): Call<Unit>

    @POST("Account/Register")
    fun registerUser(@Body registration: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("Account/UpdateProfile")
    fun updateUser(@Body registration: SendEncryptionRequest): Call<SendEncryptionRequest>

    /*  @POST("Account/ResetPassword")
      fun resetPassword(@Body requestResetPassword: RequestResetPassword): Call<Any?>
  */
    @POST("Account/Forgotpassword")
    fun resetPassword(@Body requestResetPassword: SendEncryptionRequest): Call<Any?>

    @Multipart
    @POST("Account/UploadFile")
    fun uploadProfileImage(
            @Part file: MultipartBody.Part,
            @Query("callFrom") callFrom: Int
    ): Call<SendEncryptionRequest>

    @Multipart
    @POST("Account/UploadFile")
    fun uploadCNICPics(
            @Part front: MultipartBody.Part,
            @Part backMultipart: MultipartBody.Part,
            @Query("callFrom") callFrom: Int
    ): Call<SendEncryptionRequest>

    @Multipart
    @POST("Account/UploadFile")
    fun uploadProfileCNICPic(
            @Part front: MultipartBody.Part,
            @Query("callFrom") callFrom: Int
    ): Call<SendEncryptionRequest>

    @POST("Account/Login")
    fun loginUser(@Body loginMdl: SendEncryptionRequest): Call<SendEncryptionRequest>


    @GET("Account/GetQuestions")
    fun getQuestions(): Call<SendEncryptionRequest>

    @GET("Car/FillDropDowns")
    fun getDropDownValues(): Call<SendEncryptionRequest>


    @POST("Feedback/AddCustomerFeedback")
    fun addFeedback(@Body postFeedBack: SendEncryptionRequest): Call<ResponseBody>

    /*  @POST("Feedback/GetCustomerFeedbackQuestions?UserID=")
      fun addFeedbackQuestions(@Body postFeedBack: PostFeedBack): Call<JsonObject>
  */
    @POST("Feedback/GetCustomerFeedbackQuestions")
    fun addFeedbackQuestions(@Body userRedeemsGetRequest: SendEncryptionRequest): Call<SendEncryptionRequest>

/*

    @POST("Feedback/GetCustomerFeedbackQuestions")
    fun addFeedbackQuestions(
        @Query("UserID") userID: String
    ): Call<List<FeedBackQuestionsModel>>

*/
/* feedback post api

    @POST("Feedback/GetCustomerFeedbackQuestions")
    fun addFeedbackQuestions(@Body getCustomerFeedbackModel: GetCustomerFeedbackModel): Call<List<FeedBackQuestionsModel>>
*/

    @POST("Car/AddCustomerInterestV1")
    fun addAreaOfInterest(@Body areaOfAreaOfInterest: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("Car/GetCustomerInterestByID")
    fun getCustomerInterestsById(@Body areaOfAreaOfInterest: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("Car/GetCustomerInterest")
    fun getCustomerInterests(@Body areaOfAreaOfInterest: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("Car/AddCustomerInterestV1")
    fun addInterest(@Body areaOfAreaOfInterest: List<Interest>): Call<Interest>

    @POST("Car/AddCarDetails")
    fun addCarDetails(@Body postCarDetail: SendEncryptionRequest): Call<SendEncryptionRequest>


    @POST("ShareWithFriend/AddShareWithFriendCode")
    fun addShareWithFriends(@Body shareWithFriendsModel: SendEncryptionRequest): Call<Any?>


    @POST("ShareWithFriend/VerifyShareWithFriendCode")
    fun verifyShareWithFriends(@Body shareWithFriendsModel: SendEncryptionRequest): Call<SendEncryptionRequest>


    @POST("Car/SetCarAsDefault")
    fun setDefaultCar(@Query("id") id: Long, @Query("CustomerId") customerId: Long): Call<Void?>

    @POST("Car/DeleteCar")
    fun deleteCar(@Body sendEncryptionRequest: SendEncryptionRequest): Call<Unit>

    @POST("policy/GetTrips")
    fun allTrips(@Body checkOutRedeemRequest: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("policy/GetTrip")
    fun allTrip(@Body checkOutRedeemRequest: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("policy/GetFeedbackSpecificTrip")
    fun getTripFeedback(@Body getTripFeedBackReq: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("policy/GetLatest5Trips")
    fun getLatestFiveTrips(@Body checkOutRedeemRequest: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("policy/GetStats")
    fun allStats(@Body checkOutRedeemRequest: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("policy/GetStatsFeedback")
    fun getStatsFeedBack(@Body getStatsFeedback: SendEncryptionRequest): Call<SendEncryptionRequest>

//    @POST("Gamifications/GetAllGames")
//    fun getAllGames(@Body checkOutRedeemRequest: SendEncryptionRequest): Call<SendEncryptionRequest>


    @POST("Gamifications/UserGamesGet")
    fun getUserGamesGET(@Body userId: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("Gamifications/UserGames")
    fun getUserGames(@Body userRedeemsGetRequest: SendEncryptionRequest): Call<Unit>

    //zaheer: Redeem Section

    @POST("Reedeem/GetReedeemCart")
    fun getRedeemCart(@Body userRedeemsGetRequest: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("Reedeem/ReedeemsGet")
    fun getAvailableRedeems(): Call<SendEncryptionRequest>

    @POST("Reedeem/UserReedeemsGet")
    fun getMyRedeems(@Body userRedeemsGetRequest: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("Reedeem/ReedeemAddCart")
    fun updateRedeemCart(@Body updateRedeemCartRequest: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("Reedeem/ReedeemLuckyDrawStatus")
    fun getRedeemReward(@Body updateRedeemCartRequest: SendEncryptionRequest): Call<SendEncryptionRequest>

    @POST("Reedeem/UserReedeems")
    fun checkoutRedeem(@Body checkOutRedeemRequest: SendEncryptionRequest): Call<SendEncryptionRequest>

    @GET("Account/GetFAQ")
    fun getHelp(): Call<SendEncryptionRequest>
}