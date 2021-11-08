package com.jsbl.genix.utils.callBacks

import android.view.View
import com.jsbl.genix.model.DogBreed

/**
 * Created by Muhammad Ali on 20-May-20.
 * Email muhammad.ali9385@gmail.com
 */
interface OnItemClickListener {
    fun onItemClick(view: View, pos: Int, obj: Any)
}