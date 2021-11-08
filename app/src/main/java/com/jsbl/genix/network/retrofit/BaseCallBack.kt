package com.jsbl.genix.network.retrofit

import com.jsbl.genix.network.retrofit.model.ApiResponse

abstract class BaseCallBack<T> {
    abstract fun onLoading()
    abstract fun onSuccess(response: ApiResponse<T>)
    abstract fun onFailure(response: ApiResponse<T>)
    abstract fun onSessionExpire()
}