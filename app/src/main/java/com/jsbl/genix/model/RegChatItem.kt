package com.jsbl.genix.model

import com.jsbl.genix.views.adapters.RegChatAdapter

data class RegChatItem(
    var sender: Int = RegChatAdapter.VIEW_SENDER,
    var msgType: Int = 0,
    var msg: String = "",
    var cnicImages: ArrayList<String> = arrayListOf<String>("", "")
) {
}