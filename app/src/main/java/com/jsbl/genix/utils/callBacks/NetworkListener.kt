package com.jsbl.genix.utils.callBacks

import com.android.volley.NetworkResponse
import com.jsbl.genix.utils.RequestHandler

interface NetworkListener {
    fun onLoading(obj: RequestHandler)
    fun onSuccess(obj: RequestHandler)
    fun onError(obj: RequestHandler)
}