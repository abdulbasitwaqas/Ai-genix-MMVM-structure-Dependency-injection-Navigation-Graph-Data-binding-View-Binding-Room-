package com.jsbl.genix.model.scope

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScopeCredential(var userName: String?, var pass: String?) : Parcelable