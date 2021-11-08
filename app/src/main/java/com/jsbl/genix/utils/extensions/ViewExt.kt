package com.jsbl.genix.utils.extensions

import android.R.attr.*
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.jsbl.genix.BuildConfig
import com.jsbl.genix.R
import com.jsbl.genix.url.APIsURL
import com.jsbl.genix.utils.logD
import com.mikhaellopez.circularprogressbar.CircularProgressBar


fun ViewGroup.inflate(@LayoutRes resourceId: Int) =
    LayoutInflater.from(context).inflate(
        resourceId,
        this,
        false
    )

fun <T : ViewDataBinding?> ViewGroup.bindingInflate(@LayoutRes resourceId: Int) =
    DataBindingUtil.inflate<T>(
        LayoutInflater.from(context),
        resourceId,
        this,
        false
    )

inline fun ViewGroup.forEach(action: (view: View) -> Unit) {
    for (index in 0 until childCount) {
        action(getChildAt(index))
    }
}

fun CardView.selectAsButton(isSelected: Boolean) {

    for (index in 0 until childCount) {
        val view = getChildAt(index)
        if (view is CardView) {
            extractChildFromInnerCarView(view, isSelected)
        }
    }
}
fun CardView.selectAsButtonEM(isSelected: Boolean) {

    for (index in 0 until childCount) {
        val view = getChildAt(index)
        if (view is CardView) {
            extractChildFromInnerCarViewEM(view, isSelected)
        }
    }
}


fun extractChildFromInnerCarViewEM(cardView: CardView, isSelected: Boolean) {
    for (index in 0 until cardView.childCount) {
        val view = cardView.getChildAt(index)
        if (view is ConstraintLayout) {
            extractChildFromConstraintLayoutEM(view, isSelected)
        }
    }
}

fun extractChildFromInnerCarView(cardView: CardView, isSelected: Boolean) {
    for (index in 0 until cardView.childCount) {
        val view = cardView.getChildAt(index)
        if (view is ConstraintLayout) {
            extractChildFromConstraintLayout(view, isSelected)
        }
    }
}

fun extractChildFromConstraintLayoutEM(constraintLayout: ConstraintLayout, isSelected: Boolean) {

    for (index in 0 until constraintLayout.childCount) {
        val view = constraintLayout.getChildAt(index)
        if (isSelected) {
            constraintLayout.setBackgroundResource(R.drawable.new_ic_image_button_selected)
            val typedValue = TypedValue()
            val theme = view.context.theme
            theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
            @ColorInt val color = typedValue.data
            if (view is ImageView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setColorFilter(color)
                }

            } else if (view is TextView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setTextColor(color)
                }
            }
        } else {
            constraintLayout.setBackgroundResource(R.drawable.new_ic_image_button_unselected_em)
            val typedValue = TypedValue()
            val theme = view.context.theme
            theme.resolveAttribute(R.attr.colorOnBackground, typedValue, true)
            @ColorInt val color = typedValue.data
            if (view is ImageView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setColorFilter(color)
                }
            } else if (view is TextView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setTextColor(color)
                }
            }
        }
    }
}

fun extractChildFromConstraintLayout(constraintLayout: ConstraintLayout, isSelected: Boolean) {

    for (index in 0 until constraintLayout.childCount) {
        val view = constraintLayout.getChildAt(index)
        if (isSelected) {
            constraintLayout.setBackgroundResource(R.drawable.new_ic_image_button_selected)
            val typedValue = TypedValue()
            val theme = view.context.theme
            theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
            @ColorInt val color = typedValue.data
            if (view is ImageView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setColorFilter(color)
                }

            } else if (view is TextView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setTextColor(color)
                }
            }
        } else {
            constraintLayout.setBackgroundResource(R.drawable.new_ic_image_button_unselected)
            val typedValue = TypedValue()
            val theme = view.context.theme
            theme.resolveAttribute(R.attr.colorOnBackground, typedValue, true)
            @ColorInt val color = typedValue.data
            if (view is ImageView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setColorFilter(color)
                }
            } else if (view is TextView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setTextColor(color)
                }
            }
        }
    }
}


fun extractChildFromConstraintLayoutButton(constraintLayout: ConstraintLayout, color: Int) {

    for (index in 0 until constraintLayout.childCount) {
        val view = constraintLayout.getChildAt(index)
        if (view is ImageView) {
            if (index == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setColorFilter(color)
                }
            }
        } else if (view is TextView) {
            if (index == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setTextColor(color)
                }
            }
        }
    }
}


@BindingAdapter("android:selectButton")
fun selectButton(view: CardView, boolean: Boolean) {
    view.selectAsButton(boolean)
}

@BindingAdapter("android:setBackgroundColor")
fun setBackgroundColor(view: View, color: Int) {
    val gD = GradientDrawable()
    gD.setColor(Color.parseColor("#00ff0000"))
    gD.shape = GradientDrawable.OVAL
    gD.setStroke(5, color)
    view.background = gD
    /*  val back = view.background = gD
      val wrappedDrawable =
          DrawableCompat.wrap(back!!)
      DrawableCompat.setTint(wrappedDrawable, color)*/
}

@BindingAdapter("glide")
fun glide(view: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(view).load(BuildConfig.REEDEM_ICON_BASEURL+url).placeholder(R.drawable.redeem_default_icon).into(view)
    }
}

@BindingAdapter("android:setProg")
fun setProgressValue(view: CircularProgressBar, progress: Int) {
    if (progress > 100) {
        view.progress = 100f
    } else if (progress < 0) {
        view.progress = 0f
    } else {
        view.progress = progress.toFloat()
    }
    /*  val back = view.background = gD
      val wrappedDrawable =
          DrawableCompat.wrap(back!!)
      DrawableCompat.setTint(wrappedDrawable, color)*/
}

@BindingAdapter("android:setProgColor")
fun setProgressColor(view: CircularProgressBar, color: Int) {
    view.progressBarColor = color
    /*  val back = view.background = gD
      val wrappedDrawable =
          DrawableCompat.wrap(back!!)
      DrawableCompat.setTint(wrappedDrawable, color)*/
}


fun selectCardButton(unSelectedButton: CardView, selectedButton: CardView?): CardView {
    selectedButton?.selectAsButton(false)
    unSelectedButton.selectAsButton(true)
    return unSelectedButton
}

fun toggleCardButton(unSelectedButton: CardView, setSelect: Boolean): Boolean {
    extractChildFromInnerCarView(unSelectedButton, setSelect)
    return setSelect
}


fun selectorConstraintButton(button: ConstraintLayout, motionEvent: MotionEvent): Boolean {
    if (motionEvent.action == MotionEvent.ACTION_UP) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.background =
                button.context.getDrawable(R.drawable.new_ic_btn_bg_unselected)
            val typedValue = TypedValue()
            val theme = button.context.theme
            theme.resolveAttribute(R.attr.colorOnBackground, typedValue, true)
            @ColorInt val color = typedValue.data

            extractChildFromConstraintLayoutButton(button, color = color)

            theme.resolveAttribute(R.attr.colorPrimaryVariant2, typedValue, true)
            @ColorInt val color2 = typedValue.data
            val view = button.getChildAt(2) as ImageView
            view.setColorFilter(color2)
        }
        return true
    } else if (motionEvent.action == MotionEvent.ACTION_DOWN) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.background =
                button.context.getDrawable(R.drawable.new_ic_btn_bg_focused)
            val typedValue = TypedValue()
            val theme = button.context.theme
            theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
            @ColorInt val color = typedValue.data
            extractChildFromConstraintLayoutButton(button, color = color)

            theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
            @ColorInt val color2 = typedValue.data
            val view = button.getChildAt(2) as ImageView
            view.setColorFilter(color2)
        }
    }
    return false
}


@SuppressLint("ResourceAsColor")
fun selectorHelpButton(button: ConstraintLayout, motionEvent: MotionEvent): Boolean {
    if (motionEvent.action == MotionEvent.ACTION_UP) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.background = button.context.getDrawable(R.drawable.new_ic_btn_bg_un_focused_white)

            val typedValue = TypedValue()
            val theme = button.context.theme
            theme.resolveAttribute(R.attr.colorOnBackground, typedValue, true)
            @ColorInt val color = typedValue.data
            extractChildFromConstraintLayoutButton(button, color = color)
        }
        logD("logGenix","${motionEvent.action} ")
        return true
    } else if (motionEvent.action == MotionEvent.ACTION_DOWN) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.background = button.context.getDrawable(R.drawable.new_ic_btn_bg_focused_black)


            val typedValue = TypedValue()
            val theme = button.context.theme
            theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
            @ColorInt val color = typedValue.data
            extractChildFromConstraintLayoutButton(button, color = color)
        }
        logD("logGenix","${motionEvent.action} ")
    } else if (motionEvent.action == MotionEvent.ACTION_CANCEL){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.background = button.context.getDrawable(R.drawable.new_ic_btn_bg_un_focused_white)

            val typedValue = TypedValue()
            val theme = button.context.theme
            theme.resolveAttribute(R.attr.colorOnBackground, typedValue, true)
            @ColorInt val color = typedValue.data
            extractChildFromConstraintLayoutButton(button, color = color)
        }
        logD("logGenix","${motionEvent.action} ")
        return true
    }

    return false
}

fun selectorWelcomeCardButton(button: CardView, motionEvent: MotionEvent): Boolean {
    if (motionEvent.action == MotionEvent.ACTION_UP) {
        button.selectAsButton(false)
        return true
    } else if (motionEvent.action == MotionEvent.ACTION_DOWN) {
        button?.selectAsButton(true)

    }
    return false
}
fun selectorWelcomeCardButtonEM(button: CardView, motionEvent: MotionEvent): Boolean {
    if (motionEvent.action == MotionEvent.ACTION_UP) {
        button.selectAsButtonEM(false)
        return true
    } else if (motionEvent.action == MotionEvent.ACTION_DOWN) {
        button?.selectAsButtonEM(true)

    }
    return false
}


fun TextInputLayout.disableHintAnimation() {
    isHintAnimationEnabled = false
}

fun EditText.clearBackground() {
    val paddingBottom = paddingBottom
    val paddingTop = paddingTop
    val paddingLeft = paddingLeft
    val paddingRight = paddingRight
    background = null
    setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
}


fun View.hide() {
    visibility = GONE
}

fun View.show() {
    visibility = VISIBLE
}