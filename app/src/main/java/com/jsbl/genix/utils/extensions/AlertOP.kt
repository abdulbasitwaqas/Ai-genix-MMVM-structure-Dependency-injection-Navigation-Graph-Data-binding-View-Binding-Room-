package com.app.architecturekotlin.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object AlertOP {
    fun showAlert(
        context: Context?,
        title: String = "Alert",
        message: String = "",
        pBtnText: String = "Yes",
        nBtnText: String? = null,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    ) {
        var pBtnText = pBtnText
        try {
            val builder =
                AlertDialog.Builder(context!!)
            if (title != null) builder.setTitle(title)
            builder.setMessage(message)
            if (pBtnText == null || pBtnText.isEmpty()) pBtnText = "Ok"
            // Set dialog positive button and clickListener
            builder.setPositiveButton(
                pBtnText
            ) { dialog, which ->
                dialog.dismiss()
                onPositiveClick?.invoke()
            }

            // if also have negative button then set it
            if (nBtnText != null && !nBtnText.isEmpty()) {
                builder.setNegativeButton(
                    nBtnText
                ) { dialog, which ->
                    dialog.dismiss()
                    onNegativeClick?.invoke()
                }
            }
            val dialog = builder.create()
            dialog.show()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
} //main
