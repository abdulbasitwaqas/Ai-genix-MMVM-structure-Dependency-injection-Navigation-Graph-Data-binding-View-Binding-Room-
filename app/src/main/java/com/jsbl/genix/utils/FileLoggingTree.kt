package com.jsbl.genix.utils

import android.content.Context
import timber.log.Timber

class FileLoggingTree(
    private val context: Context
) : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, message, t)
        logD(APP_TAG, message)
        logD(tag!!, message)
        // You can save log to file here.
    }
}
