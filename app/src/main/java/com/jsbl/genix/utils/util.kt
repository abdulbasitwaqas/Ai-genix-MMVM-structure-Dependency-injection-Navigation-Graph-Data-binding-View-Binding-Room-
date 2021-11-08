package com.jsbl.genix.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.jsbl.genix.BuildConfig
import com.jsbl.genix.R
import com.jsbl.genix.model.RedeemItem
import com.jsbl.genix.trips.TripItem
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.services.actionForService
import com.jsbl.genix.views.activities.ActCaptureCamera.Companion.IMAGE_W_H
import com.jsbl.genix.views.activities.ActivityWelcome
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList





val REQUEST_CODE_SEND_SMS = 2324


fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }
}

fun ImageView.loadProfile(url: String?, progressDrawable: CircularProgressDrawable) {

    if (url == null) return
    if (url.equals("")) return
    val options: RequestOptions = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.drawable.ic_login_profile_avatar)
    Glide.with(context)
        .setDefaultRequestOptions(options)
//        .load(BuildConfig.BASE_URL_IMAGES + url.trim())
        .load(BuildConfig.LOAD_PROFILE_BASE_URL_DEV + url.trim())
        .into(this)
}

fun ImageView.loadManufacturerIcon(url: String?, progressDrawable: CircularProgressDrawable) {

    if (url == null) return
    if (url.equals("")) return
    val options: RequestOptions = RequestOptions()
        .placeholder(R.drawable.ic_default_interest)
        .error(R.drawable.ic_default_interest)
    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(BuildConfig.CAR_MANUF_BASE_URL_DEV + url.trim())
        .into(this)
}

fun ImageView.loadAOI(url: String?, progressDrawable: CircularProgressDrawable) {

    if (url == null) return
    if (url.equals("")) return
    val options: RequestOptions = RequestOptions()
        .placeholder(R.drawable.place_holder)
        .error(R.drawable.place_holder)
    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(BuildConfig.ICON_BASEURL + url.trim())
        .into(this)
}

fun ImageView.loadCarColorMain(url: String?, progressDrawable: CircularProgressDrawable) {

    if (url == null) return
    if (url.equals("")) return
    val options: RequestOptions = RequestOptions()
        .placeholder(R.drawable.place_holder)
        .error(R.drawable.new_ic_btn_vector_car_detail_icon)
    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(BuildConfig.CAR_COLOR_BASE_URL_DEV + url.trim())
        .into(this)
}

/**
 * extension method for [ImageView] for loading new Image
 */
fun ImageView.loadCnic(url: String?, progressDrawable: CircularProgressDrawable) {

    if (url == null) return
    if (url.equals("")) return
    val options: RequestOptions = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.drawable.ic_outline_image_24)
    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(BuildConfig.BASE_URL_IMAGES_CNIC + url.trim())
        .into(this)
}

@BindingAdapter("android:profileImage")
fun loadProfile(view: ImageView, url: String?) {
    if (url == null) return
    if (url.equals("")) return
    view.loadProfile(url, getProgressDrawable(view.context))
}

@BindingAdapter("android:loadManufacturerIcon")
fun loadManufacturerIcon(view: ImageView, url: String?) {
    if (url == null) return
    if (url.equals("")) return
    view.loadManufacturerIcon(url, getProgressDrawable(view.context))
}

@BindingAdapter("android:loadAOIIcon")
fun loadAOIIcon(view: ImageView, url: String?) {
    if (url == null) return
    if (url.equals("")) return
    view.loadAOI(url, getProgressDrawable(view.context))
}


@BindingAdapter("android:loadCarColor")
fun loadCarColor(view: ImageView, url: String?) {
    if (url == null) return
    if (url.equals("")) return
    view.loadCarColorMain(url, getProgressDrawable(view.context))
}

@BindingAdapter("app:startDateFormat")
fun startDate(view: View, text: String?) {
    (view as AppCompatTextView).text = if (text == null || text.isEmpty()) {
        ""
    } else {
        val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())
        val date = simpleDateFormat.parse(text)
        simpleDateFormat.applyPattern("dd MMM, yyyy")
        "Started on " + simpleDateFormat.format(date)
    }
}

@BindingAdapter("app:endDateFormat")
fun endDate(view: View, text: String?) {
    (view as AppCompatTextView).text = if (text == null || text.isEmpty()) {
        ""
    } else {
        val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())
        val date = simpleDateFormat.parse(text)
        simpleDateFormat.applyPattern("dd MMM, yyyy")
        "Completed on " + simpleDateFormat.format(date)
    }
}

@BindingAdapter("app:expireDateFormat")
fun expireDate(view: View, text: String?) {
    (view as AppCompatTextView).text = if (text == null || text.isEmpty()) {
        ""
    } else {
        val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())
        val date = simpleDateFormat.parse(text)
        simpleDateFormat.applyPattern("dd MMM, yyyy")
        "Expires on \n" + simpleDateFormat.format(date)
    }
}

@BindingAdapter("android:cnicImage")
fun loadCnic(view: ImageView, url: String?) {
    if (url == null) return
    if (url.equals("")) return
    view.loadCnic(url, getProgressDrawable(view.context))
}

@BindingAdapter("setDrawable")
fun setImageUri(view: ImageView, imageUri: String?) {
    if (imageUri == null) {
        view.setImageURI(null)
    } else {
        view.setImageURI(Uri.parse(imageUri))
    }
}

@BindingAdapter("setDrawable")
fun setImageUri(view: ImageView, imageUri: Uri?) {
    view.setImageURI(imageUri)
}

@BindingAdapter("setDrawable")
fun setImageDrawable(view: ImageView, drawable: Drawable?) {
    view.setImageDrawable(drawable)
}

@BindingAdapter("setDrawable")
fun setImageResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

fun getDaysCount(begin: Date?, end: Date?): Int {
    val start: Calendar = Calendar.getInstance()
    start.time = begin
    start[Calendar.MILLISECOND] = 0
    start[Calendar.SECOND] = 0
    start[Calendar.MINUTE] = 0
    start[Calendar.HOUR_OF_DAY] = 0
    val finish: Calendar = Calendar.getInstance()
    finish.time = end
    finish[Calendar.MILLISECOND] = 999
    finish[Calendar.SECOND] = 59
    finish[Calendar.MINUTE] = 59
    finish[Calendar.HOUR_OF_DAY] = 23
    val delta = finish.timeInMillis - start.timeInMillis
    return Math.floor(delta / (1000.0 * 60 * 60 * 24)).toInt()
}

fun getTimeLeft(date: String): String { // dateFormat = "yyyy-MM-dd"
    val DateSplit = date.split("-").toTypedArray()
    val month = DateSplit[1].toInt() - 1
    // if month is november  then subtract by 1
    val year = DateSplit[0].toInt()
    val day = DateSplit[2].toInt()
    val hour = 0
    val minute = 0
    val second = 0
    val now = Calendar.getInstance()
    var sec = second - Calendar.getInstance()[Calendar.SECOND]
    var min = (minute
            - Calendar.getInstance()[Calendar.MINUTE])
    var hr = (hour
            - Calendar.getInstance()[Calendar.HOUR_OF_DAY])
    var dy = (day
            - Calendar.getInstance()[Calendar.DATE])
    var mnth = (month
            - Calendar.getInstance()[Calendar.MONTH])
    val daysinmnth = 32 - dy
    val end = Calendar.getInstance()
    end[year, month] = day
    if (mnth != 0) {
        if (dy != 0) {
            if (sec < 0) {
                sec = (sec + 60) % 60
                min--
            }
            if (min < 0) {
                min = (min + 60) % 60
                hr--
            }
            if (hr < 0) {
                hr = (hr + 24) % 24
                dy--
            }
            if (dy < 0) {
                dy = (dy + daysinmnth) % daysinmnth
                mnth--
            }
            if (mnth < 0) {
                mnth = (mnth + 12) % 12
            }
        }
    }
    val hrtext = if (hr == 1) "hour" else "hours"
    val dytext = if (dy == 1) "day" else "days"
    val mnthtext = if (mnth == 1) "month" else "months"
    return if (now.after(end)) {
        ""
    } else {
        var months = ""
        var days = ""
        var hours = ""
        months = if (mnth > 0) "$mnth $mnthtext" else ""
        if (mnth <= 0) {
            days = if (dy > 0) "$dy $dytext" else ""
            if (dy <= 0) {
                hours = if (hr > 0) "$hr $hrtext" else ""
            }
        }
        //Log.d("DATE", months + " 1 " + days + " 2 " + hours);
        months + days + hours
    }
}

/**
 * extension methods for logs.
 */

val Any.APP_TAG: String
    get() = "logGenix::" + this::class.simpleName

fun logV(tag: String, msg: String) {
    if (BuildConfig.DEBUG) Log.v(tag, msg)
}

// do something for a debug build
fun logD(tag: String, msg: String) {
    if (BuildConfig.DEBUG) Log.d(tag, msg)
}

fun logE(tag: String, msg: String) {
    if (BuildConfig.DEBUG) Log.e(tag, msg)
}


/**
 * extension methods for Toasts.
 */
fun showShort(context: Context, msg: String) {
    if (BuildConfig.DEBUG) Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun showReleaseShort(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

// do something for a debug build
fun showLong(context: Context, msg: String) {
    if (BuildConfig.DEBUG) Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}

// do something for a debug build
fun showReleaseLong(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}


/**
 * extension methods for Views.
 */
fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}


fun getImeiOrDeviceId(context: Context): String {
    /* val tm =
         ContextCompat.getSystemService<Any>(context) as TelephonyManager?
     return tm!!.deviceId*/
    return ""
}

public fun logout(any: Any, activity: Activity) {
    try {
        if (any is Response<*>) {
            if (any.code() == 401) {
                actionForService(activity, start = false)
                activity.startActivity(Intent(activity, ActivityWelcome::class.java))
                activity.finishAffinity()
            }
        }
    } catch (e: Exception) {

    }
}

public fun extractNetworkErrorMsg(any: Any, activity: Activity): String {
    try {
        if (any is Response<*>) {
            if (any.errorBody() != null) {
                val msg2 = any.errorBody()!!.string()
                return if (msg2.isNullOrEmpty()) {
                    any.message()
                } else {
                    try {
                        val trimMsg = msg2.replace("\"", "")
                        trimMsg.trim()
                    } catch (e: Exception) {
                        any.message()

                    }
                }
            } else {
                return any.message()
            }
        } else {
            return activity.getString(R.string.networkError)
        }
    } catch (e: Exception) {
        return activity.getString(R.string.networkError)
    }
}

public fun logoutExplicit(activity: Activity) {
    try {
        actionForService(activity, start = false)
        activity.startActivity(Intent(activity, ActivityWelcome::class.java))
        activity.finishAffinity()

    } catch (e: Exception) {

    }
}

public fun getProfilePercent(customerX: CustomerX?): Int {
    var percent = 0

    if (customerX != null) {
        percent += 25
        if (!customerX.carDetails.isNullOrEmpty()) {
            percent += 25
            logD("**insured", "InsuredCall")
            for (cardetails in customerX.carDetails!!) {
                logD("**insured", "" + cardetails.insured)
                if (cardetails.insured) {
//                    if (!cardetails.insuranceCompany.isNullOrEmpty() || cardetails.notInsuredReasonID != 0L) {
                    percent += 25
                    break
                }
            }
        }
        if (!customerX.customerInterests.isNullOrEmpty()) {
            percent += 25
        }
    }
    return percent
}

fun <T> stringObj(data: String?, clazz: Class<T>?): T? {
    var gson = Gson()
    if (data == null) {
        return null
    }
    return try {
        gson.fromJson(data, clazz)
    } catch (e: Exception) {
        null
    }
}

fun getOtp(message: String): String {
    val pattern: Pattern = Pattern.compile("(\\d{6})")

//   \d is for a digit
//   {} is the number of digits here 6.


//   \d is for a digit
//   {} is the number of digits here 6.
    val matcher: Matcher = pattern.matcher(message)
    var value = ""
    if (matcher.find()) {
        value = matcher.group(0) // 6 digit number
    }
    return value
}


fun getResizedBitmap(image: Bitmap, maxSize: Int = IMAGE_W_H): Bitmap {
    var width = image.width
    var height = image.height
    val bitmapRatio = width.toFloat() / height.toFloat()
    if (bitmapRatio > 1) {
        width = maxSize
        height = (width / bitmapRatio).toInt()
    } else {
        height = maxSize
        width = (height * bitmapRatio).toInt()
    }
    return Bitmap.createScaledBitmap(image, width, height, true)
}

fun getDummyTrips(context: Context): ArrayList<TripItem> {
    val tripItems = java.util.ArrayList<TripItem>()
    tripItems.add(
        TripItem(
            trip = "trips",
            startingAddress = context.getString(R.string.address3),
            endingAddress = context.getString(R.string.address2),
            startingPoint = 0.0,
            filterLabel = "Distance 10 KM",
            endingPoint = 0.0,
            score = 10,
            time = 0,
            acceleration = 0,
            speeding = 0,
            cornering = 0,
            braking = 0
        )

    )
    tripItems.add(
        TripItem(
            trip = "trips",
            startingAddress = context.getString(R.string.address3),
            endingAddress = context.getString(R.string.address2),
            startingPoint = 0.0,
            filterLabel = "Distance 10 KM",
            endingPoint = 0.0,
            score = 20,
            time = 0,
            acceleration = 4,
            speeding = 0,
            cornering = 2,
            braking = 0
        )

    )
    tripItems.add(
        TripItem(
            trip = "trips",
            startingAddress = context.getString(R.string.address3),
            endingAddress = context.getString(R.string.address2),
            startingPoint = 0.0,
            filterLabel = "Distance 10 KM",
            endingPoint = 0.0,
            score = 40,
            time = 0,
            acceleration = 6,
            speeding = 0,
            cornering = 3,
            braking = 5
        )

    )
    tripItems.add(
        TripItem(
            trip = "trips",
            startingAddress = context.getString(R.string.address3),
            endingAddress = context.getString(R.string.address2),
            startingPoint = 0.0,
            filterLabel = "Distance 10 KM",
            endingPoint = 0.0,
            score = 80,
            time = 0,
            acceleration = 3,
            speeding = 4,
            cornering = 6,
            braking = 1
        )

    )
    tripItems.add(
        TripItem(
            trip = "trips",
            startingAddress = context.getString(R.string.address3),
            endingAddress = context.getString(R.string.address2),
            startingPoint = 0.0,
            filterLabel = "Distance 10 KM",
            endingPoint = 0.0,
            score = 60,
            time = 0,
            acceleration = 5,
            speeding = 0,
            cornering = 0,
            braking = 3
        )

    )
    tripItems.add(
        TripItem(
            trip = "trips",
            startingAddress = context.getString(R.string.address3),
            endingAddress = context.getString(R.string.address2),
            startingPoint = 0.0,
            filterLabel = "Distance 10 KM",
            endingPoint = 0.0,
            score = 90,
            time = 0,
            acceleration = 3,
            speeding = 0,
            cornering = 5,
            braking = 0
        )

    )
    tripItems.add(
        TripItem(
            trip = "trips",
            startingAddress = context.getString(R.string.address3),
            endingAddress = context.getString(R.string.address2),
            startingPoint = 0.0,
            filterLabel = "Distance 10 KM",
            endingPoint = 0.0,
            score = 10,
            time = 0,
            acceleration = 6,
            speeding = 3,
            cornering = 0,
            braking = 0
        )

    )
    tripItems.add(
        TripItem(
            trip = "trips",
            startingAddress = context.getString(R.string.address3),
            endingAddress = context.getString(R.string.address2),
            startingPoint = 0.0,
            filterLabel = "Distance 10 KM",
            endingPoint = 0.0,
            score = 30,
            time = 0,
            acceleration = 3,
            speeding = 2,
            cornering = 5,
            braking = 0
        )

    )
    tripItems.add(
        TripItem(
            trip = "trips",
            startingAddress = context.getString(R.string.address3),
            endingAddress = context.getString(R.string.address2),
            startingPoint = 0.0,
            filterLabel = "Distance 10 KM",
            endingPoint = 0.0,
            score = 60,
            time = 0,
            acceleration = 2,
            speeding = 0,
            cornering = 1,
            braking = 0
        )

    )
    tripItems.add(
        TripItem(
            trip = "trips",
            startingAddress = context.getString(R.string.address4),
            endingAddress = context.getString(R.string.address2),
            startingPoint = 0.0,
            filterLabel = "Distance 10 KM",
            endingPoint = 0.0,
            score = 25,
            time = 0,
            acceleration = 0,
            speeding = 2,
            cornering = 5,
            braking = 0
        )

    )
    tripItems.add(
        TripItem(
            trip = "trips",
            startingAddress = context.getString(R.string.address5),
            endingAddress = context.getString(R.string.address2),
            startingPoint = 0.0,
            filterLabel = "Distance 10 KM",
            endingPoint = 0.0,
            score = 80,
            time = 0,
            acceleration = 0,
            speeding = 3,
            cornering = 0,
            braking = 0
        )

    )
    return tripItems
}

fun getDummyRedeems(context: Context): ArrayList<RedeemItem> {
    //Type 0 Lucky Draw , 1 Coffee
    //Status 0 available, 1 started, 2 completed
    val redeemList = java.util.ArrayList<RedeemItem>()
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 0,
            status = 1

        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 0,
            status = 2
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 0,
            status = 0
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 1,
            status = 2
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 1,
            status = 1

        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 0,
            status = 2
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 0,
            status = 0
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 0,
            status = 0
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 0,
            status = 1

        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 1,
            status = 0
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 0,
            status = 2
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 0,
            status = 0
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 1,
            status = 1

        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 1,
            status = 1
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 1,
            status = 0
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 0,
            status = 0
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 0,
            status = 2
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 0,
            status = 2
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 1,
            status = 1
        )

    )
    redeemList.add(
        RedeemItem(
            title = "1 Lac Luck Draw",
            subtitle = "started 2 days ago",
            points = 500,
            type = 0,
            status = 2
        )

    )
    return redeemList
}

/*

fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
    var width = image.width
    var height = image.height
    val bitmapRatio = width.toFloat() / height.toFloat()
    if (bitmapRatio > 1) {
        width = maxSize
        height = (width / bitmapRatio).toInt()
    } else {
        height = maxSize
        width = (height * bitmapRatio).toInt()
    }
    return Bitmap.createScaledBitmap(image, width, height, true)
}*/
