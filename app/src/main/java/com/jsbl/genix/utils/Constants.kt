package com.jsbl.genix.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scope.common.utils.SingleLiveEvent
import com.scope.common.utils.liveActiveTripInProgress
import org.joda.time.Period

object Constants {
    const val BLACK_CAR_ICON = "Black_Car.PNG"
    const val WHITE_CAR_ICON = "White_Car.PNG"
    const val GRAY_CAR_ICON = "Grey_Car.PNG"
    const val BLUE_CAR_ICON = "Blue_Car.PNG"


    var tripInProgress: MutableLiveData<Boolean> = liveActiveTripInProgress
    var tripDistance: MutableLiveData<Float> = MutableLiveData()
    var tripDuration: MutableLiveData<Period> = MutableLiveData()
    var currentSpeed: LiveData<Float> = MutableLiveData()
    var tripEnded: SingleLiveEvent<Void> = SingleLiveEvent()
}