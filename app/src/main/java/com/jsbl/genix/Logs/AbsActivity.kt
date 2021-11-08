package com.scope.smartdrivedemo

import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

abstract class AbsActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        Timber.i("Opening ${this.javaClass.simpleName}")
    }
}