package com.github.burachevsky.mqtthub.common.ext

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.ColorInt
import androidx.annotation.MenuRes
import com.github.burachevsky.mqtthub.common.constant.Anim

fun View.showPopupMenu(
    @MenuRes menuRes: Int,
    gravity: Int = Gravity.END,
    onItemSelected: (Int) -> Boolean
) {
    PopupMenu(context, this)
        .also { popup ->
            MenuInflater(context)
                .inflate(menuRes, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                onItemSelected(menuItem.itemId)
            }

            popup.gravity = gravity
        }
        .show()
}

fun View.changeBackgroundColor(
    @ColorInt colorTo: Int,
    timeMillis: Long = Anim.DEFAULT_DURATION
): ValueAnimator? {
    val colorFrom = (background as ColorDrawable).color

    if (colorFrom != colorTo) {
        return ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            .apply {
                duration = timeMillis
                addUpdateListener { animator ->
                    setBackgroundColor(animator.animatedValue as Int)
                }
                start()
            }
    }

    return null
}