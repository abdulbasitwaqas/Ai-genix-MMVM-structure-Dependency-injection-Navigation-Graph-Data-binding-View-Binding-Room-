package com.jsbl.genix.utils.eventBus

import android.content.Context
import android.content.IntentFilter
import com.jsbl.genix.utils.eventBus.ObjectBus.Companion.FILTER_ACTION

class ObjectArrivalHelper(
    var context: Context,
    var appArrivedObjectListener: ArrivedObjectListener?
) {
    private var intentFilter: IntentFilter = IntentFilter()
    var objectBus: ObjectBus

    init {
        intentFilter.addAction(FILTER_ACTION)
        objectBus = ObjectBus()
        objectBus.arrivedObjectListener = appArrivedObjectListener
    }

    fun registerBroadCast() {
        context.registerReceiver(objectBus, intentFilter)
    }

    fun unregisterBroadCast() {
        context.unregisterReceiver(objectBus)
    }

    fun setIntentListener(arrivedObjectListener: ArrivedObjectListener) {
        this.appArrivedObjectListener = arrivedObjectListener
        objectBus.arrivedObjectListener = arrivedObjectListener
    }

}