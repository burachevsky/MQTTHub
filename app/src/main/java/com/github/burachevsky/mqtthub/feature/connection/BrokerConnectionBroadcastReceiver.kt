package com.github.burachevsky.mqtthub.feature.connection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class BrokerConnectionBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("BrokerConnectionBroadcastReceiver: $intent")
        when (intent.action) {
            Action.DISCONNECT -> {
                context.stopService(Intent(context, BrokerConnectionService::class.java))
            }
        }
    }
}