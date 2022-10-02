package com.github.burachevsky.mqtthub.common.ext

import android.view.Gravity
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.MenuRes

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