package com.jsbl.genix.network.retrofit.model

import java.io.Serializable

class ApiResponse<T>(
    var isError: Boolean = false,
    var message: String = "",
    var response: String = "",
    var responseObject: T? = null,
    var responseCode: Int = 0
) : Serializable {
}