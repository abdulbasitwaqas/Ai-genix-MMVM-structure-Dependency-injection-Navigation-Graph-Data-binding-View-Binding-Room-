package com.jsbl.genix.utils

import android.os.Parcelable
import com.jsbl.genix.model.RequestResponse
import kotlinx.android.parcel.Parcelize

data class RequestHandler(
    var loading: Boolean,
    var isSuccess: Boolean,
    var showAlert: Boolean = false,
    var any: Any?
)