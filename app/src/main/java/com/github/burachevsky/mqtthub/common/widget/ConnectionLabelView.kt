package com.github.burachevsky.mqtthub.common.widget

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.ext.dp
import com.github.burachevsky.mqtthub.common.ext.getValueFromAttribute
import kotlinx.parcelize.Parcelize

class ConnectionLabelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val label = TextView(context)
        .apply {
            setTextAppearance(R.style.TextAppearance_MQTTHub_Label)
        }

    private val progress = ProgressBar(context)
        .apply {
            isIndeterminate = true
            layoutParams = MarginLayoutParams(16.dp, 16.dp).apply {
                leftMargin = 8.dp
            }
        }

    private var currentState: ConnectionState? = null

    init {
        setPadding(12.dp, 8.dp, 12.dp, 8.dp)
        gravity = Gravity.CENTER
        addView(label)
        addView(progress)
        isVisible = false
    }

    fun applyState(state: ConnectionState) {
        if (currentState == state && state is ConnectionState.Connected)
            return

        if (state is ConnectionState.Empty) {
            isVisible = false
            return
        }

        currentState = state

        label.setText(state.text)
        label.setTextColor(state.getLabelColor(context))
        setBackgroundResource(state.background)
        progress.isVisible = state.progress

        isVisible = true

        if (state is ConnectionState.Connected) {
            postDelayed(1500) {
                isVisible = false
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState(), currentState)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        state?.let {
            if (state is SavedState) {
                super.onRestoreInstanceState(state.superState)
                currentState = state.connectionState
                state.connectionState?.let(::applyState)
            } else {
                super.onRestoreInstanceState(state)
            }
        }
    }

    @Parcelize
    private data class SavedState(
        val superState: Parcelable?,
        val connectionState: ConnectionState?
    ) : Parcelable
}

sealed class ConnectionState(
    @StringRes val text: Int,
    @DrawableRes val background: Int,
    val progress: Boolean,
) : Parcelable {
    abstract fun getLabelColor(context: Context): Int

    @Parcelize
    object Connected : ConnectionState(
        R.string.connected,
        R.drawable.bg_connection_connected,
        false
    ) {
        override fun getLabelColor(context: Context): Int {
            return context.getColor(R.color.light_green)
        }
    }

    @Parcelize
    object Disconnected : ConnectionState(
        R.string.disconnected,
        R.drawable.bg_connection_disconnected,
        false
    ) {
        override fun getLabelColor(context: Context): Int {
            return context.getValueFromAttribute(com.google.android.material.R.attr.colorError)
        }
    }

    @Parcelize
    object Connecting : ConnectionState(
        R.string.connecting,
        R.drawable.bg_connection_connecting,
        true
    ) {
        override fun getLabelColor(context: Context): Int {
            return context.getValueFromAttribute(com.google.android.material.R.attr.colorSecondary)
        }
    }

    @Parcelize
    object Empty : ConnectionState(
        0, 0, false
    ) {
        override fun getLabelColor(context: Context): Int {
            return 0
        }
    }
}