package com.example.blink

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ScreenStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {

            Intent.ACTION_SCREEN_ON -> {
                val r1 = ScreenUsageController.onScreenOn()
                if (r1 == ScreenUsageController.Result.ENFORCE_LOCK) {
                    ScreenLocker.lock(context)
                }
            }

            Intent.ACTION_SCREEN_OFF -> {
                ScreenUsageController.onScreenOff()
            }
        }
    }
}
