package com.b12give.driver.utils

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.jsbl.genix.R

/*AlertOp is used to show alert from any where in the application.
There are different methods created for hanlding the different types of Alerts.
* */

object AlertOP {
    private var dialog: AlertDialog? = null

    fun showAlert(
        context: Context,
        topTitle: String? = null,
        topDescription: String? = null,
        bottomTitle: String? = null,
        bottomDescription: String? = null,
        positiveText: Int = android.R.string.ok,
        negativeText: Int = android.R.string.cancel,
        showTopTitleBold: Boolean = false,
        showBottomTitleBold: Boolean = false,
        showTopDescriptionBold: Boolean = false,
        showBottomDescriptionBold: Boolean = false,
        onPositiveClickListener: (() -> Unit)? = null,
        onNegativeClickListener: (() -> Unit)? = null,
        isError: Boolean = false,
        cancelable: Boolean = false,
        title: String? = null,
        message: String? = null
    ) {
       /* if (!isError) {
            try {
                val builder = AlertDialog.Builder(context, R.style.CustomDialogNormalStyle)
                val view = LayoutInflater.from(context).inflate(R.layout.layout_dialog, null)

                builder.setView(view)
                if (topTitle == null)
                    view.textTopTitle.setVisibilityGone()
                else {
                    view.textTopTitle.setVisibilityVisible()
                    view.textTopTitle.text = topTitle
                }
                if (topDescription == null)
                    view.textTopDescription.setVisibilityGone()
                else {
                    view.textTopDescription.setVisibilityVisible()
                    view.textTopDescription.text = topDescription
                }
                if (bottomTitle == null)
                    view.textBottomTitle.setVisibilityGone()
                else {
                    view.textBottomTitle.setVisibilityVisible()
                    view.textBottomTitle.text = bottomTitle
                }
                if (bottomDescription == null)
                    view.textBottomDescription.setVisibilityGone()
                else {
                    view.textBottomDescription.setVisibilityVisible()
                    view.textBottomDescription.text = bottomDescription
                }

                if (showTopTitleBold)
                    view.textTopTitle.setTypeface(null, Typeface.BOLD)
                if (showBottomTitleBold)
                    view.textBottomTitle.setTypeface(null, Typeface.BOLD)
                if (showTopDescriptionBold)
                    view.textTopDescription.setTypeface(null, Typeface.BOLD)
                if (showBottomDescriptionBold)
                    view.textBottomDescription.setTypeface(null, Typeface.BOLD)

                builder.setCancelable(cancelable)


                view.buttonOk.text = context.getString(positiveText)
                view.buttonCancel.text = context.getString(negativeText)

                view.buttonOk.setOnClickListener {
                    onPositiveClickListener?.invoke()
                    dialog?.dismiss()
                }


                if (onNegativeClickListener != null) {
                    view.buttonCancel.setVisibilityVisible()
                    view.buttonCancel.setOnClickListener {
                        onNegativeClickListener.invoke()
                        dialog?.dismiss()
                    }
                } else {
                    view.buttonCancel.setVisibilityGone()
                }

                dialog = builder.create()
                dialog?.show()

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } else {
            try {
                val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
                if (title != null)
                    builder.setTitle(title)
                else
                    builder.setTitle(context.getString(R.string.app_name))
                builder.setCancelable(cancelable)
                builder.setMessage(message ?: "Please Wait")
                builder.setPositiveButton(positiveText) { dialog, _ ->
                    onPositiveClickListener?.invoke()
                    dialog?.dismiss()
                }
                if (onNegativeClickListener != null) {
                    builder.setNegativeButton(negativeText) { dialog, _ ->
                        onNegativeClickListener.invoke()
                        dialog?.dismiss()
                    }
                }


                val dialog = builder.create()

                dialog.show()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }*/

    }
}