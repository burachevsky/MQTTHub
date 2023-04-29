package com.github.burachevsky.mqtthub.common.listener

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

open class AppBarStateChangeListener : AppBarLayout.OnOffsetChangedListener {

    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    var currentState = State.IDLE
        private set

    private var prevOffset = 0

    override fun onOffsetChanged(appBarLayout: AppBarLayout, offset: Int) {
        val dy = offset - prevOffset
        prevOffset = offset

        currentState = when {
            offset == 0 -> {
                if (currentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED)
                }
                State.EXPANDED
            }

            abs(offset) >= appBarLayout.totalScrollRange -> {
                if (currentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED)
                }
                State.COLLAPSED
            }

            else -> {
                if (currentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE)
                }
                State.IDLE
            }
        }

        onScrolled(currentState, dy)
    }

    open fun onStateChanged(appBarLayout: AppBarLayout, state: State) {}

    open fun onScrolled(state: State, dy: Int) {}
}