package com.jsbl.genix.utils.extensions

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.view.ContextThemeWrapper
import com.jsbl.genix.R

fun showOnlyAlertMessage(
    context: Context,
    title: String = "Alert",
    msg: String = "",
    positiveButtonText: String = "OK",
    onPositiveClick: (() -> Unit)? = null
) {

    val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))


    with(builder)
    {
        setCancelable(false)
        setTitle(title)
        setMessage(msg)
        setPositiveButton(positiveButtonText, DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            onPositiveClick?.invoke()
        })
        show()
    }

}

fun showConfirmationDialog(
    context: Context,
    title: String = "Alert",
    msg: String = "",
    positiveButtonText: String = "Yes",
    negativeButtonText: String = "No",
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null

) {

    val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))

    with(builder)
    {
        setCancelable(false)
        setTitle(title)
        setMessage(msg)
        setPositiveButton(
            positiveButtonText,
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                onPositiveClick?.invoke()
            }
        )
        setNegativeButton(
            negativeButtonText,
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                onNegativeClick?.invoke()
            }
        )
        show()
    }


}


fun showCaptureAlert(
    context: Activity,
    positiveButtonText: String = "Confirm",
    negativeButtonText: String = "Retry",
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null,
    bitmap: Bitmap
) {
    val builder =
        androidx.appcompat.app.AlertDialog.Builder(
            ContextThemeWrapper(
                context,
                R.style.AlertDialogCustom
            )
        )
    val inflater: LayoutInflater = context.layoutInflater
    val dialogLayout: View = inflater.inflate(R.layout.dialog_image_view, null)


    with(builder)
    {
//            requestWindowFeature(Window.FEATURE_NO_TITLE)
        setView(dialogLayout)
        setCancelable(false)
        setPositiveButton(
            positiveButtonText,
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                onPositiveClick?.invoke()
            }
        )
        setNegativeButton(
            negativeButtonText,
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                onNegativeClick?.invoke()
            }
        )
        show()
        val image: ImageView = dialogLayout.findViewById<ImageView>(R.id.imageViewer)
        image.setImageBitmap(bitmap)


    }
}


fun showRedeemAlert(
    context: Activity,
    title: String = "",
    message: String = "",
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null,
) {
    val builder =
        androidx.appcompat.app.AlertDialog.Builder(
            ContextThemeWrapper(
                context,
                R.style.AlertDialogCustom
            )
        )

    with(builder)
    {
        setTitle(title)
        setMessage(message)
        setCancelable(false)
        setPositiveButton(
            "Add to Cart",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                onPositiveClick?.invoke()
            }
        )
        setNegativeButton(
            "Cancel",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                onNegativeClick?.invoke()
            }
        )
        show()


    }
}